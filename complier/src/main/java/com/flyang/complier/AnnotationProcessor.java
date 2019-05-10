package com.flyang.complier;

import com.flyang.complier.processor.InjectParamProcessor;
import com.flyang.complier.processor.InterceptorProcessor;
import com.flyang.complier.processor.MethodViewProcessor;
import com.flyang.complier.processor.RouterProcessor;
import com.flyang.complier.util.Logger;
import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import static com.flyang.complier.Consts.OPTION_MODULE_NAME;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * 编译生成入口类
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)//java版本支持
@SupportedOptions(OPTION_MODULE_NAME)
public class AnnotationProcessor extends AbstractProcessor {

    private RouterProcessor routerProcessor;
    private InterceptorProcessor interceptorProcessor;
    private InjectParamProcessor injectParamProcessor;
    private MethodViewProcessor methodViewProcessor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Logger.init(processingEnv.getMessager());
        methodViewProcessor = new MethodViewProcessor(processingEnvironment);
        routerProcessor = new RouterProcessor(processingEnvironment);
        interceptorProcessor = new InterceptorProcessor(processingEnvironment);
        injectParamProcessor = new InjectParamProcessor(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.size()>0) {
            methodViewProcessor.process(roundEnvironment);
            routerProcessor.process(roundEnvironment);
            interceptorProcessor.process(roundEnvironment);
            injectParamProcessor.process(roundEnvironment);
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = methodViewProcessor.getSupportedAnnotationTypes();
        types.addAll(interceptorProcessor.getSupportedAnnotationTypes());
        types.addAll(injectParamProcessor.getSupportedAnnotationTypes());
        return types;
    }
}
