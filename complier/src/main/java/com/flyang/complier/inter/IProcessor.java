package com.flyang.complier.inter;


import java.util.Set;

import javax.annotation.processing.RoundEnvironment;

/**
 * @author caoyangfei
 * @ClassName IProcessor
 * @date 2019/7/7
 * ------------- Description -------------
 * 注解处理器接口
 */
public interface IProcessor {

    boolean process(RoundEnvironment roundEnv);

    Set<String> getSupportedAnnotationTypes();

}
