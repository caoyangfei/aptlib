package com.flyang.plugin.inject.model

import com.flyang.annotation.inject.IMethod
import com.flyang.annotation.inject.Inject
import org.objectweb.asm.Type

class InjectInfo {
    static final String INJECT_METHOD = Type.getDescriptor(IMethod.class)

    static final String INJECT_CLASS = Type.getDescriptor(Inject.class)

    static final String INJECT_TO_CLASS = "com/flyang/annotation/inject/InjectContract"
    static final String INJECT_TO_CLASS_Method = "injectClass"

    //要注入的类
    List<String> injectClasses = new ArrayList<>()

    File injectToClass //最终要注入代码的class

    String injectToClassName  //被注入class name

    String injectToMethodName  //被注入class method name

    static final InjectInfo info = new InjectInfo()

    static InjectInfo get() {
        return info
    }

    void retEnv() {
        injectClasses.clear()
        injectToClass = null
        injectToClassName = null
        injectToMethodName = null
    }
}