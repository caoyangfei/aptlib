package com.flyang.api.router.template;

import com.flyang.api.router.AptHub;

import java.util.Map;

/**
 * @author caoyangfei
 * @ClassName RouteTable
 * @date 2019/4/27
 * ------------- Description -------------
 * 路由表，通过Gradle添加进Map
 * {@link AptHub#routeTable}
 */
public interface RouteTable {
    /**
     * Mapping between uri and target, the target class may be an {@link android.app.Activity},
     * {@link android.app.Fragment} or {@link android.support.v4.app.Fragment}.
     *
     * @param map uri -> target.
     */
    void handle(Map<String, Class<?>> map);
}
