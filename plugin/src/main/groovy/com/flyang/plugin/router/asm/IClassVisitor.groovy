package com.flyang.plugin.router.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
/**
 * 定义在读取Class字节码时会触发的事件，如类头解析完成、注解解析、字段解析、方法解析等。
 */
class IClassVisitor extends ClassVisitor {

    IClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        if (name == "<clinit>") {
            mv = new IMethodVisitor(mv)
        }
        return mv
    }
}