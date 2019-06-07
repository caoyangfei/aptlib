package com.flyang.complier.manager;

import com.flyang.annotation.apt.InjectParam;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

import static com.flyang.complier.Consts.ACTIVITY_FULL_NAME;
import static com.flyang.complier.Consts.FRAGMENT_FULL_NAME;
import static com.flyang.complier.Consts.FRAGMENT_V4_FULL_NAME;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/26
 * ------------- Description -------------
 * 创建java文件生成类 参数代理
 */
public class GenerateInjectParamClass implements GenerateClass {

    private static class TypeUtil {
        static final ClassName ParamInjector = ClassName.get("com.flyang.api.router.template", "ParamInjector");
    }

    public static final String PARAM_CLASS_SUFFIX = "$$IntentRouter$$ParamInjector";
    public static final String METHOD_INJECT = "inject";
    public static final String CLASS_JAVA_DOC = "注解获取bundle传递数据\n{@link Router#injectParams(Object)}\n";

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类

    //map通过唯一KEY存放Set<Element>，区分注解所在的类
    private Map<TypeElement, Set<Element>> elementMap;

    public GenerateInjectParamClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        roundEnvironment = roundEnv;
        processingEnvironment = processingEnv;
        mFiler = processingEnv.getFiler();
        elementMap = new HashMap<>();
    }

    @Override
    public void generateFile() {
        addField();
        try {
            generateInjectorParam();
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * 获取注解的Element元素，保存在map
     */
    public void addField() {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(InjectParam.class);
        if (elements == null || elements.isEmpty()) {
            Logger.info("没有找到注解!");
            return;
        }
        for (Element element : elements) {
            if (element.getKind().isField()) { // 注解到字段，支持Activity/Fragment
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                if (elementMap.containsKey(typeElement)) {
                    elementMap.get(typeElement).add(element);
                } else {
                    Set<Element> typeElements = new HashSet();
                    typeElements.add(element);
                    elementMap.put(typeElement, typeElements);
                }
            }
        }
    }

    private void generateInjectorParam() throws IllegalAccessException {
        final String TARGET = "target";
        final String EXTRAS = "extras";
        final String OBJ = "obj";

        ParameterSpec objectParamSpec = ParameterSpec.builder(TypeName.OBJECT, OBJ).build();

        for (Map.Entry<TypeElement, Set<Element>> entry : elementMap.entrySet()) {
            TypeElement parent = entry.getKey();
            Set<Element> params = entry.getValue();

            String qualifiedName = parent.getQualifiedName().toString();
            String simpleName = parent.getSimpleName().toString();
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            String fileName = simpleName + PARAM_CLASS_SUFFIX;

            // 判断在activity或fragment中
            boolean isActivity;
            if (isSubtype(parent, ACTIVITY_FULL_NAME)) {
                isActivity = true;
            } else if (isSubtype(parent, FRAGMENT_V4_FULL_NAME) || isSubtype(parent, FRAGMENT_FULL_NAME)) {
                isActivity = false;
            } else {
                throw new IllegalAccessException(
                        String.format("The target class %s must be Activity or Fragment.", simpleName));
            }

            Logger.info(String.format("Start to process injected params in %s ...", simpleName));

            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(METHOD_INJECT)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(objectParamSpec);
            injectMethodBuilder.addStatement("$T $L = ($T) $L",
                    ClassName.get(parent), TARGET, ClassName.get(parent), OBJ);
            if (isActivity) {
                injectMethodBuilder.addStatement("$T $L = $L.getIntent().getExtras()",
                        ClassName.get("android.os", "Bundle"), EXTRAS, TARGET);
            } else {
                injectMethodBuilder.addStatement("$T $L = $L.getArguments()",
                        ClassName.get("android.os", "Bundle"), EXTRAS, TARGET);
            }

            for (Element param : params) {
                InjectParam injectParam = param.getAnnotation(InjectParam.class);
                String fieldName = param.getSimpleName().toString();
                String key = isEmpty(injectParam.key()) ? fieldName : injectParam.key();

                StringBuilder statement = new StringBuilder();
                if (param.getModifiers().contains(Modifier.PRIVATE)) {
                    Logger.warn(param, String.format(
                            "Found private field: %s, please remove 'private' modifier for a better performance.", fieldName));

                    String reflectName = "field_" + fieldName;

                    injectMethodBuilder.beginControlFlow("try")
                            .addStatement("$T $L = $T.class.getDeclaredField($S)",
                                    ClassName.get(Field.class), reflectName, ClassName.get(parent), fieldName)
                            .addStatement("$L.setAccessible(true)", reflectName);

                    Object[] args;
                    statement.append("$L.set($L, $L.get")
                            .append(getAccessorType(param.asType()))
                            .append("(")
                            .append("$S");
                    if (supportDefaultValue(param.asType())) {
                        statement.append(", ($T) $L.get($L)");
                        args = new Object[]{reflectName, TARGET, EXTRAS, key,
                                ClassName.get(param.asType()), reflectName, TARGET};
                    } else {
                        args = new Object[]{reflectName, TARGET, EXTRAS, key};
                    }
                    statement.append("))");

                    injectMethodBuilder.addStatement(statement.toString(), args)
                            .nextControlFlow("catch ($T e)", Exception.class)
                            .addStatement("e.printStackTrace()")
                            .endControlFlow();
                } else {
                    Object[] args;
                    statement.append("$L.$L = ($T) $L.get")
                            .append(getAccessorType(param.asType())).append("(")
                            .append("$S");
                    // , target.field
                    if (supportDefaultValue(param.asType())) {
                        statement.append(", $L.$L");
                        args = new Object[]{TARGET, fieldName, ClassName.get(param.asType()), EXTRAS, key, TARGET, fieldName};
                    } else {
                        args = new Object[]{TARGET, fieldName, ClassName.get(param.asType()), EXTRAS, key};
                    }
                    statement.append(")");

                    injectMethodBuilder.addStatement(statement.toString(), args);
                }
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(fileName)
                    .addSuperinterface(TypeUtil.ParamInjector)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(injectMethodBuilder.build())
                    .addJavadoc(CLASS_JAVA_DOC)
                    .build();

            try {
                JavaFile.builder(packageName, typeSpec).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.info(String.format("Params in class %s have been processed: %s.", simpleName, fileName));
        }
    }

    /**
     * 是否支持默认值
     *
     * @param typeMirror
     * @return
     */
    private boolean supportDefaultValue(TypeMirror typeMirror) {
        if (typeMirror instanceof PrimitiveType) {
            return true;
        }
        if (isSubtype(typeMirror, "java.lang.String") || isSubtype(typeMirror, "java.lang.CharSequence")) {
            return true;
        }
        return false;
    }

    /**
     * 获取传递类型
     *
     * @param typeMirror The type to access in the bundle
     * @return The string to append to 'get' or 'put'
     */
    private String getAccessorType(TypeMirror typeMirror) {
        if (typeMirror instanceof PrimitiveType) {
            return typeMirror.toString().toUpperCase().charAt(0) + typeMirror.toString().substring(1);
        } else if (typeMirror instanceof DeclaredType) {
            Element element = ((DeclaredType) typeMirror).asElement();
            if (element instanceof TypeElement) {
                if (isSubtype(element, "java.util.List")) {
                    List<? extends TypeMirror> typeArgs = ((DeclaredType) typeMirror).getTypeArguments();
                    if (typeArgs != null && !typeArgs.isEmpty()) {
                        TypeMirror argType = typeArgs.get(0);
                        if (isSubtype(argType, "java.lang.Integer")) {
                            return "IntegerArrayList";
                        } else if (isSubtype(argType, "java.lang.String")) {
                            return "StringArrayList";
                        } else if (isSubtype(argType, "java.lang.CharSequence")) {
                            return "CharSequenceArrayList";
                        } else if (isSubtype(argType, "android.os.Parcelable")) {
                            return "ParcelableArrayList";
                        }
                    }
                } else if (isSubtype(element, "android.os.Bundle")) {
                    return "Bundle";
                } else if (isSubtype(element, "java.lang.String")) {
                    return "String";
                } else if (isSubtype(element, "java.lang.CharSequence")) {
                    return "CharSequence";
                } else if (isSubtype(element, "android.util.SparseArray")) {
                    return "SparseParcelableArray";
                } else if (isSubtype(element, "android.os.Parcelable")) {
                    return "Parcelable";
                } else if (isSubtype(element, "java.io.Serializable")) {
                    return "Serializable";
                } else if (isSubtype(element, "android.os.IBinder")) {
                    return "Binder";
                }
            }
        } else if (typeMirror instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror compType = arrayType.getComponentType();
            if (compType instanceof PrimitiveType) {
                return compType.toString().toUpperCase().charAt(0) + compType.toString().substring(1) + "Array";
            } else if (compType instanceof DeclaredType) {
                Element compElement = ((DeclaredType) compType).asElement();
                if (compElement instanceof TypeElement) {
                    if (isSubtype(compElement, "java.lang.String")) {
                        return "StringArray";
                    } else if (isSubtype(compElement, "java.lang.CharSequence")) {
                        return "CharSequenceArray";
                    } else if (isSubtype(compElement, "android.os.Parcelable")) {
                        return "ParcelableArray";
                    }
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isSubtype(Element typeElement, String type) {
        return processingEnvironment.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnvironment.getElementUtils().getTypeElement(type).asType());
    }

    private boolean isSubtype(TypeMirror typeMirror, String type) {
        return processingEnvironment.getTypeUtils().isSubtype(typeMirror,
                processingEnvironment.getElementUtils().getTypeElement(type).asType());
    }

    private boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }
}
