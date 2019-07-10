package com.flyang.complier;


import com.squareup.javapoet.ClassName;

/**
 * @author caoyangfei
 * @ClassName Consts
 * @date 2019/4/27
 * ------------- Description -------------
 * 基础路径
 */
public class Consts {
    public static final String OPTION_MODULE_NAME = "moduleName";

    public static final String ACTIVITY_FULL_NAME = "android.app.Activity";
    public static final String FRAGMENT_FULL_NAME = "android.app.Fragment";
    public static final String FRAGMENT_V4_FULL_NAME = "android.support.v4.app.Fragment";
    public static final String VIEW_TYPE = "android.view.View";
    public static final String ROUTER_PACKAGE_NAME = "com.flyang.router";
    public static final String FACTORY_PACKAGE_NAME = "com.flyang.factory";

    public static final ClassName UNBINDER = ClassName.get("com.flyang.api.bind", "UnBinder");
    public static final ClassName IFACTORY = ClassName.get("com.flyang.api", "IFactory");
}
