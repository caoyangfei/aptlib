package com.flyang.plugin.inject.asm

import com.flyang.plugin.inject.model.InjectInfo
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IMethodVisitor extends MethodVisitor {

    static final String TAG = "IMethodVisitor"

    File injectToClass
    String injectToClassName
    String methodName

    IMethodVisitor(File file, String injectToClassName, String method, MethodVisitor mv) {
        super(Opcodes.ASM5, mv)
        this.injectToClass = file
        this.injectToClassName = injectToClassName
        this.methodName = method
    }

    /**
     * 方法注解解析
     * @param desc
     * @param visible
     * @return
     */
    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (InjectInfo.INJECT_METHOD.equals(desc)) {
            if (injectToClassName != null) {
                InjectInfo.get().injectToClass = injectToClass
                InjectInfo.get().injectToClassName = injectToClassName
                InjectInfo.get().injectToMethodName = methodName
            }
        }
        return super.visitAnnotation(desc, visible)
    }

}