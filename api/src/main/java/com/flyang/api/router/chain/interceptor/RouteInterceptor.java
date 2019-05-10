package com.flyang.api.router.chain.interceptor;

import android.support.annotation.NonNull;

import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.chain.Chain;

/**
 * @author caoyangfei
 * @ClassName RouteInterceptor
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器接口
 */
public interface RouteInterceptor {
    @NonNull
    RouteResponse intercept(Chain chain);
}
