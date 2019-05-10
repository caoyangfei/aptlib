package com.flyang.complier.manager;

import com.flyang.annotation.apt.Interceptor;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.flyang.complier.Consts.APT_PACKAGE_NAME;
import static com.flyang.complier.Consts.OPTION_MODULE_NAME;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/26
 * ------------- Description -------------
 * 创建java文件生成类 拦截器
 */
public class GenerateInterceptorClass implements GenerateClass {

    public static final String INTERCEPTOR_FULL_NAME = "com.flyang.api.router.chain.interceptor.RouteInterceptor";
    public static final String METHOD_HANDLE = "handle";
    public static final String INTERCEPTOR_TABLE_FULL_NAME = "com.flyang.api.router.template.InterceptorTable";
    public static final String INTERCEPTOR_TABLE = "InterceptorTable";
    public static final String CLASS_JAVA_DOC = "保存拦截器Class到map集合\n";

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类
    private String mModuleName;

    //每个模块只生成一个类，所有不需要map区分
    private Set<TypeElement> typeElements;

    public GenerateInterceptorClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        roundEnvironment = roundEnv;
        processingEnvironment = processingEnv;
        mFiler = processingEnv.getFiler();
        mModuleName = processingEnv.getOptions().get(OPTION_MODULE_NAME);
        typeElements = new HashSet<>();
    }

    @Override
    public void generateFile() {
        addField();
        if (mModuleName != null) {
            String validModuleName = mModuleName.replace(".", "_").replace("-", "_");
            generateInterceptors(validModuleName);
        } else {
            throw new RuntimeException(String.format("No option `%s` passed to Interceptor annotation processor.", OPTION_MODULE_NAME));
        }
    }

    public void addField() {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Interceptor.class);
        if (elements == null || elements.isEmpty()) {
            Logger.info("没有找到注解!");
            return;
        }
        // TypeElement集合
        for (Element element : elements) {
            //检查是不是继承RouteInterceptor的拦截器
            if (validateElement(element)) {
                typeElements.add((TypeElement) element);
            } else {
                Logger.error(element, String.format("The annotated element is not a implementation class of %s",
                        INTERCEPTOR_FULL_NAME));
            }
        }
    }

    /**
     * 创建拦截器
     *
     * @param moduleName
     */
    private void generateInterceptors(String moduleName) {
        TypeElement interceptorType = processingEnvironment.getElementUtils().getTypeElement(INTERCEPTOR_FULL_NAME);
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(interceptorType))));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();
        //生成方法
        MethodSpec.Builder handleInterceptors = MethodSpec.methodBuilder(METHOD_HANDLE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);

        Map<String, String> interceptorRecorder = new HashMap<>();
        for (TypeElement element : typeElements) {
            Logger.info(String.format("Found interceptor: %s", element.getQualifiedName()));
            Interceptor interceptor = element.getAnnotation(Interceptor.class);
            String name = interceptor.value();
            if (interceptorRecorder.containsKey(name)) {
                throw new RuntimeException(String.format("Duplicate interceptor name: %s[%s, %s]",
                        name, element.getQualifiedName(), interceptorRecorder.get(name)));
            }
            handleInterceptors.addStatement("map.put($S, $T.class)", name, ClassName.get(element));
            interceptorRecorder.put(name, element.getQualifiedName().toString());
        }

        //生成类
        TypeElement interfaceType = processingEnvironment.getElementUtils().getTypeElement(INTERCEPTOR_TABLE_FULL_NAME);
        TypeSpec type = TypeSpec.classBuilder(capitalize(moduleName) + INTERCEPTOR_TABLE)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(handleInterceptors.build())
                .addJavadoc(CLASS_JAVA_DOC)
                .build();

        try {
            JavaFile.builder(APT_PACKAGE_NAME, type).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }

    private boolean validateElement(Element element) {
        return element.getKind().isClass() && processingEnvironment.getTypeUtils().isAssignable(element.asType(),
                processingEnvironment.getElementUtils().getTypeElement(INTERCEPTOR_FULL_NAME).asType());
    }
}
