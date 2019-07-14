package com.flyang.complier.manager;

import com.flyang.annotation.apt.InstanceFactory;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import static com.flyang.complier.Consts.FACTORY_PACKAGE_NAME;
import static com.flyang.complier.Consts.OPTION_MODULE_NAME;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author caoyangfei
 * @ClassName GenerateInstanceFactoryClass
 * @date 2019/7/14
 * ------------- Description -------------
 * 生成InstanceFactory
 */
public class GenerateInstanceFactoryClass implements GenerateClass {

    public static final String INSTANCE_FACTORY = "InstanceFactory";

    public static final String CLASS_JAVA_DOC = "@ 实例化工厂 此类由apt自动生成\n";
    public static final String METHOD_JAVA_DOC = "@此方法由apt自动生成\n";

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类
    private String mModuleName;

    //每个模块只生成一个类，所有不需要map区分
    private Set<TypeElement> typeElements;

    public GenerateInstanceFactoryClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
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
        Set<? extends TypeElement> elements = ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(InstanceFactory.class));
        if (elements == null || elements.isEmpty()) {
            Logger.info("没有找到注解!");
            return;
        }
        // TypeElement集合
        for (TypeElement element : elements) {
            //检查是不是继承RouteInterceptor的拦截器
            if (!isValidClass(element)) return;
            typeElements.add(element);
        }
    }

    /**
     * 工厂类
     *
     * @param moduleName
     */
    private void generateInterceptors(String moduleName) {

        MethodSpec.Builder methodCreate = MethodSpec.methodBuilder("create")
                .addJavadoc(METHOD_JAVA_DOC)
                .returns(Object.class)
                .addModifiers(PUBLIC, STATIC)
                .addParameter(Class.class, "mClass");

        List<ClassName> mList = new ArrayList<>();

        CodeBlock.Builder blockBuilder = CodeBlock.builder();
        blockBuilder.beginControlFlow(" switch (mClass.getSimpleName())");//括号开始
        for (TypeElement element : typeElements) {
            ClassName currentType = ClassName.get(element);
            if (mList.contains(currentType)) continue;
            mList.add(currentType);
            blockBuilder.addStatement("case $S: return  new $T()", currentType.simpleName(), currentType);//初始化Presenter
        }
        blockBuilder.addStatement("default: return null");
        blockBuilder.endControlFlow();
        methodCreate.addCode(blockBuilder.build());

        //生成类
        TypeSpec type = TypeSpec.classBuilder(capitalize(moduleName) + INSTANCE_FACTORY)
                .addModifiers(PUBLIC, FINAL)
                .addMethod(methodCreate.build())
                .addJavadoc(CLASS_JAVA_DOC)
                .build();

        try {
            JavaFile.builder(FACTORY_PACKAGE_NAME, type).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }

    /**
     * 检查当前类是不是可以创建对象
     *
     * @param element
     * @return
     */
    public static boolean isValidClass(TypeElement element) {
        String ANNOTATION = "@";
        if (element.getKind() != ElementKind.CLASS) {
            return false;
        }
        if (!element.getModifiers().contains(PUBLIC)) {
            String message = String.format("Classes annotated with %s must be public.", ANNOTATION);
            Logger.error(message);
            return false;
        }

        if (element.getModifiers().contains(ABSTRACT)) {
            String message = String.format("Classes annotated with %s must not be abstract.", ANNOTATION);
            Logger.error(message);
            return false;
        }

        return true;
    }
}
