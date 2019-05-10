package com.flyang.plugin.router.asm


import com.flyang.plugin.router.model.Record
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IMethodVisitor extends MethodVisitor {

    IMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv)
    }

    @Override
    void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) { // handle init code before return
            Record.records.each { record ->
                record.aptClasses.each { className ->
                    println("router: handle $className")
                    mv.visitTypeInsn(Opcodes.NEW, className)
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className, "<init>", "()V", false)
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "com/flyang/api/router/AptHub", getFieldNameByInterface(record), "Ljava/util/Map;")
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "handle", "(Ljava/util/Map;)V", false)
                }
            }
        }
        super.visitInsn(opcode)
    }

    /**
     * @return AptHub中用于存储table的类变量名字
     */
    synchronized String getFieldNameByInterface(Record record) {
        if (record.templateName == Record.TEMPLATE_ROUTE_TABLE) {
            return "routeTable"
        } else if (record.templateName == Record.TEMPLATE_INTERCEPTOR_TABLE) {
            return "interceptorTable"
        } else if (record.templateName == Record.TEMPLATE_TARGET_INTERCEPTORS_TABLE) {
            return "targetInterceptorsTable"
        }
        throw IllegalArgumentException("Unrecognized record[${record.templateName}]")
    }

}