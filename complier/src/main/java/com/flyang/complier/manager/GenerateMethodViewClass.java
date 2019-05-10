package com.flyang.complier.manager;

import com.flyang.annotation.apt.inter.ListenerClass;
import com.flyang.annotation.apt.inter.ListenerMethod;
import com.flyang.complier.inter.GenerateClass;
import com.flyang.complier.model.FieldViewModel;
import com.flyang.complier.model.MethodViewModel;
import com.flyang.complier.model.ViewModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.flyang.complier.Consts.UNBINDER;
import static com.flyang.complier.Consts.VIEW_TYPE;
import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author yangfei.cao
 * @ClassName aptlib
 * @date 2019/3/28
 * ------------- Description -------------
 * 创建java文件生成类 bind
 */
public class GenerateMethodViewClass implements GenerateClass {

    private static final ClassName VIEW = ClassName.get("android.view", "View");

    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Filer mFiler; //文件相关的辅助类

    //map通过唯一KEY存放Set<Element>，区分注解所在的类
    private Map<TypeElement, Set<ViewModel>> elementMap;


    public GenerateMethodViewClass(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv, Map<TypeElement, Set<ViewModel>> elementMap) {
        roundEnvironment = roundEnv;
        processingEnvironment = processingEnv;
        mFiler = processingEnv.getFiler();
        this.elementMap = elementMap;
    }

    /**
     * 创建java文件
     *
     * @return
     */
    @Override
    public void generateFile() {
        for (Map.Entry<TypeElement, Set<ViewModel>> entry : elementMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            Set<ViewModel> value = entry.getValue();

            ClassName R = ClassName.get(processingEnvironment.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString(), "R");

            //创建bindView方法
            MethodSpec.Builder bindViewMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(typeElement.asType()), "host")
                    .addParameter(VIEW, "source")
                    .addStatement("this.target = host");

            //创建unBindView方法
            MethodSpec.Builder unBindViewMethod = MethodSpec.methodBuilder("unBind")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            for (ViewModel viewModel : value) {

                if (viewModel instanceof FieldViewModel) {
                    FieldViewModel fieldViewModel = (FieldViewModel) viewModel;
                    bindViewMethod.addStatement("host.$N = (($T)host.findViewById($T.id.$N))", fieldViewModel.getSimpleName(), ClassName.get(fieldViewModel.getFieldType()), R, fieldViewModel.getFieldResId());
                    //解除绑定对象方法
                    unBindViewMethod.addStatement("target.$N = null", fieldViewModel.getSimpleName());
                }
                if (viewModel instanceof MethodViewModel) {
                    MethodViewModel methodViewModel = (MethodViewModel) viewModel;
                    ListenerClass listenerClass = methodViewModel.getListenerClass();
                    for (String id : methodViewModel.getResIds()) {
                        String targetType = listenerClass.targetType();
                        // 创建onClick
                        TypeSpec.Builder callback = TypeSpec.anonymousClassBuilder("")
                                .superclass(ClassName.bestGuess(listenerClass.type()));
                        List<ListenerMethod> listenerMethods = getListenerMethods(listenerClass);

                        for (ListenerMethod method : listenerMethods) {
                            MethodSpec.Builder callbackMethod = MethodSpec.methodBuilder(method.name())
                                    .addAnnotation(Override.class)
                                    .addModifiers(PUBLIC);
                            String[] parameterTypes = method.parameters();
                            StringBuilder onclick = new StringBuilder();
                            onclick.append("target.$L(");
                            for (int i = 0, count = parameterTypes.length; i < count; i++) {
                                callbackMethod.addParameter(bestGuess(parameterTypes[i]), "p" + i);
                                onclick.append("p" + i);
                            }
                            onclick.append(")");
                            callbackMethod.addStatement(onclick.toString(), methodViewModel.getSimpleName().toString());
                            callback.addMethod(callbackMethod.build());
                        }
                        if (!VIEW_TYPE.equals(targetType)) {
                            bindViewMethod.addStatement("(($T)host.findViewById($T.id.$N)).$L($L)", bestGuess(targetType), R, id, listenerClass.setter(),
                                    callback.build());
                        } else {
                            bindViewMethod.addStatement("host.findViewById($T.id.$N).$L($L)", R, id, listenerClass.setter(),
                                    callback.build());
                        }
                    }
                }
            }

            // 构造类
            TypeSpec.Builder viewBinder = TypeSpec.classBuilder(String.format("%s$$ViewBinder", typeElement.getSimpleName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(UNBINDER)
                    .addField(FieldSpec.builder(TypeName.get(typeElement.asType()), "target", Modifier.PROTECTED).build())
                    .addTypeVariable(TypeVariableName.get("T", TypeName.get(typeElement.asType()))) // 添加泛型<T extends MainActivity>
                    .addMethod(bindViewMethod.build())
                    .addMethod(unBindViewMethod.build());

            //获取包名
            String packageName = getPackage(typeElement).getQualifiedName().toString();

            try {
                JavaFile.builder(packageName, viewBinder.build()).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * 获取类型
     *
     * @param type
     * @return
     */
    private static TypeName bestGuess(String type) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                int left = type.indexOf('<');
                if (left != -1) {
                    ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        typeArguments.add(WildcardTypeName.subtypeOf(Object.class));
                        left = type.indexOf('<', left + 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }

    private static List<ListenerMethod> getListenerMethods(ListenerClass listener) {
        if (listener.method().length == 1) {
            return Arrays.asList(listener.method());
        }

        try {
            List<ListenerMethod> methods = new ArrayList<>();
            Class<? extends Enum<?>> callbacks = listener.callbacks();
            for (Enum<?> callbackMethod : callbacks.getEnumConstants()) {
                Field callbackField = callbacks.getField(callbackMethod.name());
                ListenerMethod method = callbackField.getAnnotation(ListenerMethod.class);
                if (method == null) {
                    throw new IllegalStateException(String.format("@%s's %s.%s missing @%s annotation.",
                            callbacks.getEnclosingClass().getSimpleName(), callbacks.getSimpleName(),
                            callbackMethod.name(), ListenerMethod.class.getSimpleName()));
                }
                methods.add(method);
            }
            return methods;
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }
}
