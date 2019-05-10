package com.flyang.complier.inter;


import java.util.Set;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by baixiaokang on 16/10/8.
 * 注解处理器接口
 */

public interface IProcessor {

    boolean process(RoundEnvironment roundEnv);

    Set<String> getSupportedAnnotationTypes();

}
