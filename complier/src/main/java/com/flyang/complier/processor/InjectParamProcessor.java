package com.flyang.complier.processor;

import com.flyang.annotation.apt.InjectParam;
import com.flyang.complier.manager.GenerateInjectParamClass;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;


/**
 * @author caoyangfei
 * @ClassName InjectParamProcessor
 * @date 2019/4/26
 * ------------- Description -------------
 * 传参
 */
public class InjectParamProcessor extends BaseProcessor {


    public InjectParamProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        GenerateInjectParamClass generateInjectParamClass = new GenerateInjectParamClass(roundEnv, processingEnv);
        generateInjectParamClass.generateFile();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(InjectParam.class.getCanonicalName());
        return types;
    }
}
