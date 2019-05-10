package com.flyang.plugin.inject.asm

import com.flyang.plugin.inject.model.InjectInfo
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class InjectClassVisitor extends ClassVisitor {

    static final String TAG = "InjectClassVisitor"

    String own
    String method

    InjectClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    InjectClassVisitor(int api, ClassVisitor cv) {
        super(api, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        own = name
        method = InjectInfo.INJECT_TO_CLASS_Method
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        if (name.endsWith(InjectInfo.get().injectToMethodName)) {
            InjectMethodAdapter injectMethodAdapter = new InjectMethodAdapter(methodVisitor)
            return injectMethodAdapter
        } else {
            return methodVisitor
        }
    }

    class InjectMethodAdapter extends MethodVisitor {

        InjectMethodAdapter(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

        @Override
        void visitInsn(int opcode) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                InjectInfo.get().injectClasses.each { injectClass ->
                    injectClass = injectClass.replace('/', '.')
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitLdcInsn(injectClass)
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, own, method, "(Ljava/lang/String;)V", false)
                }
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocal) {
            mv.visitMaxs(maxStack + 4, maxLocal)
        }
    }

}