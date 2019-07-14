package com.flyang.complier.manager;

import com.flyang.annotation.apt.ContractFactory;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import static com.flyang.complier.Consts.OPTION_MODULE_NAME;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author caoyangfei
 * @ClassName GenerateContractFactoryClass
 * @date 2019/7/14
 * ------------- Description -------------
 * 生成Contract
 */
public class GenerateContractFactoryClass implements GenerateClass {

    public static final String CONTRACT_FACTORY = "Contract";

    public static final String CLASS_JAVA_DOC = "@ 实例化工厂 此类由apt自动生成\n此类用于IView和BasePresenter关联\n";
    public static final String METHOD_VIEW_JAVA_DOC = "@此方法由apt自动生成\n处理结果界面必须实现此接口\n";
    public static final String METHOD_PRESENTER_JAVA_DOC = "@此方法由apt自动生成\n实现接口方法，presenter继承此类\n";

    private static final ClassName IView = ClassName.get("com.flyang.base.contract", "IView");
    private static final ClassName BasePresenter = ClassName.get("com.flyang.base.presenter", "BasePresenter");

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类
    private String mModuleName;

    //每个模块只生成一个类，所有不需要map区分
    private Set<TypeElement> typeElements;

    public GenerateContractFactoryClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        roundEnvironment = roundEnv;
        processingEnvironment = processingEnv;
        mFiler = processingEnv.getFiler();
        mModuleName = processingEnv.getOptions().get(OPTION_MODULE_NAME);
        typeElements = new HashSet<>();
    }

    @Override
    public void generateFile() {
        addField();
        generateInterceptors();
    }

    public void addField() {
        Set<? extends TypeElement> elements = ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(ContractFactory.class));
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
     */
    private void generateInterceptors() {
        for (TypeElement element : typeElements) {
            //要生成类名
            String CLASS_NAME = element.getSimpleName().toString() + CONTRACT_FACTORY;
            //注解所在的路径
            String elementPackageName = element.getEnclosingElement().toString();
            //要生成的路径
            String PACKAGE_NAME = elementPackageName.substring(0, elementPackageName.lastIndexOf(".")) + ".contract";

            //view接口类
            TypeSpec.Builder viewInter = TypeSpec.interfaceBuilder("View")
                    .addJavadoc(METHOD_VIEW_JAVA_DOC)
                    .addModifiers(PUBLIC, STATIC)
                    .addSuperinterface(IView);

            /*********************此部分找出接口以及继承类全部生成出来************************/
            //键值对 方法和类型一一对应
            Map<String, ClassName> methodType = new LinkedHashMap<>();
            //先找出继承的，后边有重写的方法替换掉
            List<? extends TypeMirror> interfaces = element.getInterfaces();
            //找出继承接口
            getSuperClass(interfaces, methodType);

            //对应的类
            if (setMap(methodType, element)) return;

            for (Map.Entry<String, ClassName> entry : methodType.entrySet()) {
                MethodSpec.Builder successMethodBuilder = MethodSpec.methodBuilder(entry.getKey() + "Success")
                        .addModifiers(PUBLIC, ABSTRACT);
                if (entry.getValue() != null) {
                    successMethodBuilder.addParameter(entry.getValue(), "entity");
                }
                MethodSpec.Builder failedMethodBuilder =
                        MethodSpec.methodBuilder(entry.getKey() + "Failed")
                                .addParameter(ClassName.get(String.class), "errorMsg")
                                .addModifiers(PUBLIC, ABSTRACT);
                viewInter.addMethod(successMethodBuilder.build());
                viewInter.addMethod(failedMethodBuilder.build());
            }
            /******************************************************************************/


            TypeSpec.Builder presenterInter =
                    classBuilder("Presenter")
                            .addJavadoc(METHOD_PRESENTER_JAVA_DOC)
                            .addModifiers(PUBLIC, STATIC, ABSTRACT)
                            .superclass(ParameterizedTypeName.get(BasePresenter,
                                    ClassName.get(PACKAGE_NAME, CLASS_NAME + ".View")))
                            .addSuperinterface(ClassName.get(elementPackageName, element.getSimpleName().toString()));


            //生成接口
            TypeSpec type = TypeSpec.interfaceBuilder(CLASS_NAME)
                    .addModifiers(PUBLIC)
                    .addType(viewInter.build())
                    .addType(presenterInter.build())
                    .addJavadoc(CLASS_JAVA_DOC)
                    .build();
            try {
                JavaFile.builder(PACKAGE_NAME, type).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //找出对应的值
    public String[] getValue(TypeElement foo) {
        AnnotationMirror am = getAnnotationMirror(foo, ContractFactory.class);
        if (am == null) {
            return null;
        }
        AnnotationValue av = getAnnotationValue(am, "entites");
        String[] split = av.toString().replace(".class", "").replace("{", "")
                .replace("}", "").split(",");
        return split;
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = this.processingEnvironment.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }

    //递归查询所有继承的类
    private void getSuperClass(List<? extends TypeMirror> interfaces, Map<String, ClassName> methodType) {
        if (interfaces == null) return;
        for (TypeMirror typeMirror : interfaces) {
            TypeElement typeElement = asTypeElement(typeMirror);
            //找出所有继承的
            List<? extends TypeMirror> childInterfaces = typeElement.getInterfaces();
            if (childInterfaces != null) {
                getSuperClass(childInterfaces, methodType);
            }
            if (setMap(methodType, typeElement)) return;
        }
    }

    /**
     * 以方法名，参数的键值对存储起来
     *
     * @param methodType
     * @param typeElement
     * @return
     */
    private boolean setMap(Map<String, ClassName> methodType, TypeElement typeElement) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        if (enclosedElements == null) return true;
        String[] value = getValue(typeElement);
        for (int i = 0; i < enclosedElements.size(); i++) {
            if (value.length > i && !value[i].equals("")) {
                ClassName className = ClassName.get(value[i].substring(0, value[i].lastIndexOf(".")), value[i].substring(value[i].lastIndexOf(".") + 1));
                methodType.put(enclosedElements.get(i).getSimpleName().toString()
                        , className);
            } else {
                methodType.put(enclosedElements.get(i).getSimpleName().toString(), null);
            }
        }
        return false;
    }

    /**
     * 检查当前类是不是可以创建对象(检查是不是接口)
     *
     * @param element
     * @return
     */
    public static boolean isValidClass(TypeElement element) {
        if (element.getKind() != ElementKind.INTERFACE) {
            return false;
        }
        return true;
    }
}
