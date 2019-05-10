package com.flyang.api.router.template;

import com.flyang.api.router.AptHub;

import java.util.Map;

/**
 * @author caoyangfei
 * @ClassName TargetInterceptorsTable
 * @date 2019/4/27
 * ------------- Description -------------
 * 目标拦截器，通过Gradle添加进Map
 * {@link AptHub#targetInterceptorsTable}
 */
public interface TargetInterceptorsTable {
    /**
     * Mapping between target and interceptors, the target class may be an {@link android.app.Activity},
     * {@link android.app.Fragment} or {@link android.support.v4.app.Fragment}.
     *
     * @param map target -> interceptors array.
     */
    void handle(Map<Class<?>, String[]> map);
}
