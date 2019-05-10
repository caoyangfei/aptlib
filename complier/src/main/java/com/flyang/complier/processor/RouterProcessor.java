package com.flyang.complier.processor;

import com.flyang.annotation.apt.InjectParam;
import com.flyang.annotation.apt.Router;
import com.flyang.complier.manager.GenerateRouterClass;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/24
 * ------------- Description -------------
 * 路由器
 */
public class RouterProcessor extends BaseProcessor {
    public RouterProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        GenerateRouterClass generateClassManager = new GenerateRouterClass(roundEnv, processingEnv);
        generateClassManager.generateFile();

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Router.class.getCanonicalName());
        types.add(InjectParam.class.getCanonicalName());
        return types;
    }


}
