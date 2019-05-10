package com.flyang.plugin.router.asm


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import com.flyang.plugin.router.model.Record

class ScanClassVisitor extends ClassVisitor {

    ScanClassVisitor() {
        super(Opcodes.ASM5)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (interfaces != null) {
            Record.records.each { record ->
                interfaces.each { interfaceName ->
                    if (interfaceName == record.templateName) {
                        record.aptClasses.add(name)
                    }
                }
            }
        }
    }
}