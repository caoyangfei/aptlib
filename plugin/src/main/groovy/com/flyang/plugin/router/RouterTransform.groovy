package com.flyang.plugin.router

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.flyang.plugin.router.asm.IClassVisitor
import com.flyang.plugin.router.asm.ScanClassVisitor
import com.flyang.plugin.router.model.Record
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author yangfei.cao
 * @ClassName RouterTransform
 * @date 2019/4/26
 * ------------- Description -------------
 * 基础配置
 */
class RouterTransform extends Transform {
    static File registerTargetFile = null

    Project project

    RouterTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "router"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        long begin = System.currentTimeMillis()
        project.logger.info("- router transform begin:")

        transformInvocation.inputs.each { TransformInput input ->
            if (!input.jarInputs.empty) {
                project.logger.info("-- jarInputs:")
                input.jarInputs.each { JarInput jarInput ->
                    // com.android.support:appcompat-v7:27.1.1 (/path/to/xxx.jar)
                    project.logger.info("--- ${jarInput.name} (${jarInput.file.absolutePath})")
                    String destName = jarInput.name
                    String hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                    if (destName.endsWith(".jar")) { // local jar
                        // rename to avoid the same name, such as classes.jar
                        destName = "${destName.substring(0, destName.length() - 4)}_${hexName}"
                    }
                    File destFile = transformInvocation.outputProvider.getContentLocation(
                            destName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    if (shouldScanJar(jarInput)) {
                        scanJar(jarInput.file, destFile)
                    }

                    FileUtils.copyFile(jarInput.file, destFile)
                }
            }

            if (!input.directoryInputs.empty) {
                project.logger.info("-- directoryInputs:")
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    project.logger.info("-- directory: ${directoryInput.name} (${directoryInput.file.absolutePath})")
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    project.logger.info("-- dest dir: ${dest.absolutePath}")
                    directoryInput.file.eachFileRecurse { File file ->
                        if (file.isFile() && shouldScanClass(file)) {
                            project.logger.info("--- ${file.absolutePath}")
                            scanClass(file)
                        }
                    }

                    FileUtils.copyDirectory(directoryInput.file, dest)
                }
            }
        }

        // 找到了AptHub.class 向其注入代码
        if (registerTargetFile) {
            project.logger.info("begin to register code to ${registerTargetFile.absolutePath}")
            handle()
        } else {
            project.logger.warn("router: register target file not found.")
        }
        project.logger.info("- router transform finish.")
        project.logger.info("cost time: ${(System.currentTimeMillis() - begin) / 1000.0f}s")
    }

    static void handle() {
        File targetFile = registerTargetFile
        assert targetFile != null && targetFile.exists()

        if (targetFile.name.endsWith(".jar")) {
            def optJar = new File(targetFile.getParent(), targetFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
            def jarFile = new JarFile(targetFile)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
            Enumeration enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.name
                ZipEntry zipEntry = new ZipEntry(entryName) // new entry
                jarOutputStream.putNextEntry(zipEntry)
                jarFile.getInputStream(jarEntry).withCloseable { is ->
                    if (entryName == Record.REGISTER_CLASS_NAME) { // find AptHub.class
                        def bytes = modifyClass(is)
                        jarOutputStream.write(bytes)
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(is))
                    }
                    jarOutputStream.closeEntry()
                }
            }
            jarOutputStream.close()
            jarFile.close()

            targetFile.delete()
            optJar.renameTo(targetFile)
        } else if (targetFile.name.endsWith(".class")) { // 一般不会走到这里，因为AptHub位于jar包中
            modifyClass(new FileInputStream(targetFile))
        }
    }

    static byte[] modifyClass(InputStream inputStream) {
        inputStream.withCloseable { is ->
            ClassReader cr = new ClassReader(is)
            ClassWriter cw = new ClassWriter(cr, 0)
            ClassVisitor cv = new IClassVisitor(cw)
            cr.accept(cv, 0)
            return cw.toByteArray()
        }
    }

    static boolean shouldScanJar(JarInput jarInput) {
        Record.excludeJar.each {
            if (jarInput.name.contains(it))
                return false
        }
        return true
    }

    static boolean shouldScanClass(File classFile) {
        return classFile.absolutePath.replaceAll("\\\\", "/").contains(Record.APT_CLASS_PACKAGE_NAME)
    }

    /**
     * 扫描jar包
     */
    static void scanJar(File src, File dest) {
        if (src && src.exists()) {
            def jar = new JarFile(src)
            Enumeration enumeration = jar.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (entryName == Record.REGISTER_CLASS_NAME) {
                    // mark
                    registerTargetFile = dest
                } else if (entryName.startsWith(Record.APT_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = jar.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                }
            }
            jar.close()
        }
    }

    static void scanClass(File classFile) {
        scanClass(new FileInputStream(classFile))
    }

    /**
     * 扫描class
     */
    static void scanClass(InputStream is) {
        is.withCloseable {
            ClassReader cr = new ClassReader(is)
            ScanClassVisitor cv = new ScanClassVisitor()
            cr.accept(cv, 0)
        }
    }
}
