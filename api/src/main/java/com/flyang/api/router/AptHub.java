package com.flyang.api.router;

import android.app.Activity;
import android.app.Fragment;

import com.flyang.api.router.chain.interceptor.RouteInterceptor;
import com.flyang.api.router.template.ParamInjector;
import com.flyang.basic.log.LogUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author caoyangfei
 * @ClassName AptHub
 * @date 2019/4/27
 * ------------- Description -------------
 * 全部路由存储类（Map）Gradle写入map数据
 */
public final class AptHub {
    private static final String PARAM_CLASS_SUFFIX = "$$IntentRouter$$ParamInjector";

    // 存储实现ParamInjector实体类 -> injectParams写入class
    private static Map<String, Class<ParamInjector>> injectors = new HashMap<>();

    // 路由Uri -> Activity/Fragment
    public final static Map<String, Class<?>> routeTable = new HashMap<>();
    // interceptor's name -> interceptor
    public final static Map<String, Class<? extends RouteInterceptor>> interceptorTable = new HashMap<>();
    // interceptor instance
    public final static Map<String, RouteInterceptor> interceptorInstances = new HashMap<>();

    // Activity/Fragment -> 拦截器名字
    // Note: 这里用LinkedHashMap保证有序
    public final static Map<Class<?>, String[]> targetInterceptorsTable = new LinkedHashMap<>();

    /**
     * 自动注入参数
     *
     * @param obj Activity or Fragment.
     */
    @SuppressWarnings("unchecked")
    static void injectParams(Object obj) {
        if (obj instanceof Activity || obj instanceof Fragment || obj instanceof android.support.v4.app.Fragment) {
            String key = obj.getClass().getCanonicalName();
            Class<ParamInjector> clz;
            if (!injectors.containsKey(key)) {
                try {
                    clz = (Class<ParamInjector>) Class.forName(key + PARAM_CLASS_SUFFIX);
                    injectors.put(key, clz);
                } catch (ClassNotFoundException e) {
                    LogUtils.e("Inject params failed.", e);
                    return;
                }
            } else {
                clz = injectors.get(key);
            }
            try {
                ParamInjector injector = clz.newInstance();
                injector.inject(obj);
            } catch (Exception e) {
                LogUtils.e("Inject params failed.", e);
            }
        } else {
            LogUtils.e("The obj you passed must be an instance of Activity or Fragment.");
        }
    }
}
