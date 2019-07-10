package com.flyang.complier.processor;

import com.flyang.annotation.apt.InstanceFactory;
import com.flyang.complier.manager.GenerateInstanceFactoryClass;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/7/9
 * ------------- Description -------------
 * 工厂实例化
 */
public class InstanceFactoryProcessor extends BaseProcessor {

    public InstanceFactoryProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        GenerateInstanceFactoryClass generateInstanceFactoryClass = new GenerateInstanceFactoryClass(roundEnv, processingEnv);
        generateInstanceFactoryClass.generateFile();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(InstanceFactory.class.getCanonicalName());
        return types;
    }
}
