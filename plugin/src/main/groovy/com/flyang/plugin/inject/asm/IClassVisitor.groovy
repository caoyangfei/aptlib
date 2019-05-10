package com.flyang.plugin.inject.asm

import com.flyang.plugin.inject.model.InjectInfo
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 定义在读取Class字节码时会触发的事件，如类头解析完成、注解解析、字段解析、方法解析等。
 */
class IClassVisitor extends ClassVisitor {

    static final String TAG = "IClassVisitor"

    File injectToClass
    String injectToClassName
    String injectClassName

    IClassVisitor(File injectToClass, ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
        this.injectToClass = injectToClass
    }

    /**
     * 类解析
     * @param version
     * @param access
     * @param name
     * @param signature
     * @param superName
     * @param interfaces
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        injectClassName = name
        for (String i : interfaces) {
            if (InjectInfo.INJECT_TO_CLASS.equals(i)) {
                injectToClassName = name
            }
        }
    }

    /**
     * 注解解析
     * @param desc
     * @param visible
     * @return
     */
    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (InjectInfo.INJECT_CLASS.equals(desc)) {
            if (!InjectInfo.get().injectClasses.contains(injectClassName)) {
                InjectInfo.get().injectClasses.add(injectClassName)
            }
        }
        return super.visitAnnotation(desc, visible)
    }

    /**
     * 方法解析
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        IMethodVisitor iMethodVisitor = new IMethodVisitor(injectToClass, injectToClassName, name, methodVisitor)
        return iMethodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }
}