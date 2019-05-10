package com.flyang.complier.processor;

import com.flyang.complier.inter.IProcessor;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/24
 * ------------- Description -------------
 */
public abstract class BaseProcessor implements IProcessor {

    protected ProcessingEnvironment processingEnv;

    public BaseProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

}
