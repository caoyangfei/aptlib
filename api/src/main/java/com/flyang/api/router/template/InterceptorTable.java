package com.flyang.api.router.template;


import com.flyang.api.router.AptHub;
import com.flyang.api.router.chain.interceptor.RouteInterceptor;

import java.util.Map;

/**
 * @author caoyangfei
 * @ClassName InterceptorTable
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器，通过Gradle添加进Map
 * {@link AptHub#interceptorTable}
 */
public interface InterceptorTable {
    /**
     * Mapping between name and interceptor.
     *
     * @param map name -> interceptor.
     */
    void handle(Map<String, Class<? extends RouteInterceptor>> map);
}
