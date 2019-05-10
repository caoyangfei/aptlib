package com.flyang.complier.processor;

import com.flyang.annotation.apt.Interceptor;
import com.flyang.complier.manager.GenerateInterceptorClass;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author caoyangfei
 * @ClassName InterceptorProcessor
 * @date 2019/4/26
 * ------------- Description -------------
 * 拦截器
 */
public class InterceptorProcessor extends BaseProcessor {

    public InterceptorProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        GenerateInterceptorClass generateInterceptorClass = new GenerateInterceptorClass(roundEnv, processingEnv);
        generateInterceptorClass.generateFile();

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Interceptor.class.getCanonicalName());
        return types;
    }
}
