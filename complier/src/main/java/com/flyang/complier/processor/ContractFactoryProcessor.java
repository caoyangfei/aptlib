package com.flyang.complier.processor;

import com.flyang.annotation.apt.ContractFactory;
import com.flyang.complier.manager.GenerateContractFactoryClass;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author caoyangfei
 * @ClassName ContractFactoryProcessor
 * @date 2019/7/14
 * ------------- Description -------------
 * Contract生成
 */
public class ContractFactoryProcessor extends BaseProcessor {

    public ContractFactoryProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        GenerateContractFactoryClass generateContractFactoryClass = new GenerateContractFactoryClass(roundEnv, processingEnv);
        generateContractFactoryClass.generateFile();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ContractFactory.class.getCanonicalName());
        return types;
    }
}
