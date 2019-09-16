package com.flyang.complier.manager;

import com.flyang.annotation.apt.Router;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
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

import static com.flyang.complier.Consts.ACTIVITY_FULL_NAME;
import static com.flyang.complier.Consts.FRAGMENT_FULL_NAME;
import static com.flyang.complier.Consts.FRAGMENT_V4_FULL_NAME;
import static com.flyang.complier.Consts.IPROVIDER;
import static com.flyang.complier.Consts.OPTION_MODULE_NAME;
import static com.flyang.complier.Consts.ROUTER_PACKAGE_NAME;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/24
 * ------------- Description -------------
 * 创建java文件生成类 router
 */
public class GenerateRouterClass implements GenerateClass {

    //路由
    public static final String ROUTE_TABLE_FULL_NAME = "com.flyang.api.router.template.RouteTable";
    //拦截器
    public static final String TARGET_INTERCEPTORS_FULL_NAME = "com.flyang.api.router.template.TargetInterceptorsTable";

    public static final String CLASS_ROUTER_JAVA_DOC = "保存跳转路由Uri到map\n";
    public static final String CLASS_INTERCEPTOR_JAVA_DOC = "保存当前类使用的拦截器String到Map\n";
    public static final String ROUTE_TABLE = "RouteTable";

    public static final String TARGET_INTERCEPTORS_TABLE = "TargetInterceptorsTable";
    public static final String METHOD_HANDLE = "handle";

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类
    private String mModuleName;

    //每个模块只生成一个类，所有不需要map区分
    private Set<TypeElement> typeElements;

    public GenerateRouterClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
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
            generateRouteTable(validModuleName);
            generateTargetInterceptorsTable(validModuleName);
        } else {
            Logger.error("未获取到模块名称!");
        }
    }

    public void addField() {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);
        if (elements == null || elements.isEmpty()) {
            Logger.info("没有找到注解!");
            return;
        }
        for (Element element : elements) {
            if (element.getKind().isClass() && validateClass((TypeElement) element)) {
                //注解在class
                typeElements.add((TypeElement) element);
            }
        }
    }

    /**
     * 生成RouteTable文件
     *
     * @param moduleName
     */
    private void generateRouteTable(String moduleName) {
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(Object.class)));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();

        MethodSpec.Builder methodHandle = MethodSpec.methodBuilder(METHOD_HANDLE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);

        // 记录path->element，防止重复的route path
        Map<String, String> pathRecorder = new HashMap<>();
        for (TypeElement element : typeElements) {
            Router route = element.getAnnotation(Router.class);
            String[] paths = route.value();
            for (String path : paths) {
                if (pathRecorder.containsKey(path)) {
                    throw new RuntimeException(String.format("Duplicate router path: %s[%s, %s]",
                            path, element.getQualifiedName(), pathRecorder.get(path)));
                }
                methodHandle.addStatement("map.put($S, $T.class)", path, ClassName.get(element));
                pathRecorder.put(path, element.getQualifiedName().toString());
            }
        }

        TypeElement interfaceType = processingEnvironment.getElementUtils().getTypeElement(ROUTE_TABLE_FULL_NAME);

        TypeSpec type = TypeSpec.classBuilder(capitalize(moduleName) + ROUTE_TABLE)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodHandle.build())
                .addJavadoc(CLASS_ROUTER_JAVA_DOC)
                .build();
        try {
            JavaFile.builder(ROUTER_PACKAGE_NAME, type).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成InterceptorTable
     *
     * @param moduleName
     */
    private void generateTargetInterceptorsTable(String moduleName) {
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(Object.class)),
                TypeName.get(String[].class));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();
        MethodSpec.Builder methodHandle = MethodSpec.methodBuilder(METHOD_HANDLE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);
        boolean hasInterceptor = false; // flag
        for (TypeElement element : typeElements) {
            Router route = element.getAnnotation(Router.class);
            String[] interceptors = route.interceptors();
            if (interceptors.length > 1) {
                hasInterceptor = true;
                StringBuilder sb = new StringBuilder();
                for (String interceptor : interceptors) {
                    sb.append("\"").append(interceptor).append("\",");
                }
                methodHandle.addStatement("map.put($T.class, new String[]{$L})",
                        ClassName.get(element), sb.substring(0, sb.lastIndexOf(",")));
            } else if (interceptors.length == 1) {
                hasInterceptor = true;
                methodHandle.addStatement("map.put($T.class, new String[]{$S})",
                        ClassName.get(element), interceptors[0]);
            }
        }
        if (!hasInterceptor) {
            return;
        }
        TypeElement interfaceType = processingEnvironment.getElementUtils().getTypeElement(TARGET_INTERCEPTORS_FULL_NAME);
        TypeSpec injectClass = TypeSpec.classBuilder(capitalize(moduleName) + TARGET_INTERCEPTORS_TABLE)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodHandle.build())
                .addJavadoc(CLASS_INTERCEPTOR_JAVA_DOC)
                .build();
        try {
            JavaFile.builder(ROUTER_PACKAGE_NAME, injectClass).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是不是注解的activity和fragment
     *
     * @param typeElement
     * @return
     */
    private boolean validateClass(TypeElement typeElement) {
        if (!isSubtype(typeElement, ACTIVITY_FULL_NAME) && !isSubtype(typeElement, FRAGMENT_V4_FULL_NAME)
                && !isSubtype(typeElement, FRAGMENT_FULL_NAME) && !isSubtype(typeElement, IPROVIDER)) {
            Logger.error(typeElement, String.format("%s is not a subclass of Activity or Fragment or IProvider.",
                    typeElement.getSimpleName().toString()));
            return false;
        }
        Set<Modifier> modifiers = typeElement.getModifiers();

        // 抽象类
        if (modifiers.contains(Modifier.ABSTRACT)) {
            Logger.error(typeElement, String.format("The class %s is abstract. You can't annotate abstract classes with @%s.",
                    (typeElement).getQualifiedName(), Router.class.getSimpleName()));
            return false;
        }
        return true;
    }

    private boolean isSubtype(Element typeElement, String type) {
        return processingEnvironment.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnvironment.getElementUtils().getTypeElement(type).asType());
    }

    private String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }
}
