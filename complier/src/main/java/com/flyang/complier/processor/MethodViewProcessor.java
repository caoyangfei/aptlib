package com.flyang.complier.processor;

import com.flyang.annotation.apt.OnClick;
import com.flyang.annotation.apt.BindView;
import com.flyang.complier.manager.GenerateMethodViewClass;
import com.flyang.complier.model.FieldViewModel;
import com.flyang.complier.model.MethodViewModel;
import com.flyang.complier.model.ViewModel;
import com.flyang.complier.util.Logger;
import com.google.auto.common.SuperficialValidation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * 注解生成view
 */
public class MethodViewProcessor extends BaseProcessor {

    private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(
            OnClick.class
    );

    //map通过唯一KEY存放Set<Element>，区分注解所在的类
    private Map<TypeElement, Set<ViewModel>> elementMap;

    public MethodViewProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
        elementMap = new HashMap();
    }

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        getOnClickForRoundEnv(roundEnv);
        getBindViewForRoundEnv(roundEnv);
        GenerateMethodViewClass generateMethodViewClass = new GenerateMethodViewClass(roundEnv, processingEnv, elementMap);
        generateMethodViewClass.generateFile();
        return true;
    }


    /**
     * 获取RoundEnvironment里面的@OnClick注解
     *
     * @param roundEnvironment RoundEnvironment
     */
    private void getOnClickForRoundEnv(RoundEnvironment roundEnvironment) {
        for (Class<? extends Annotation> annotationClass : LISTENERS) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotationClass);
            if (elements == null || elements.isEmpty()) {
                Logger.info("没有找到注解!");
                return;
            }
            for (Element element : elements) {
                if (!SuperficialValidation.validateElement(element)) continue;
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                MethodViewModel methodViewModel = new MethodViewModel(element, annotationClass);
                if (elementMap.containsKey(enclosingElement)) {
                    elementMap.get(enclosingElement).add(methodViewModel);
                } else {
                    Set<ViewModel> typeElements = new HashSet<>();
                    typeElements.add(methodViewModel);
                    elementMap.put(enclosingElement, typeElements);
                }
            }
        }

    }

    /**
     * 获取RoundEnvironment里面的@BindViewId注解
     *
     * @param roundEnvironment RoundEnvironment
     */
    private void getBindViewForRoundEnv(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        if (elements == null || elements.isEmpty()) {
            Logger.info("没有找到注解!");
            return;
        }
        for (Element element : elements) {
            if (!SuperficialValidation.validateElement(element)) continue;
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            FieldViewModel fieldViewModel = new FieldViewModel(element, BindView.class);
            if (elementMap.containsKey(typeElement)) {
                elementMap.get(typeElement).add(fieldViewModel);
            } else {
                Set<ViewModel> typeElements = new HashSet<>();
                typeElements.add(fieldViewModel);
                elementMap.put(typeElement, typeElements);
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : LISTENERS) {
            types.add(annotation.getCanonicalName());
        }
        types.add(BindView.class.getCanonicalName());
        return types;
    }

}
