package com.flyang.api.router.chain;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flyang.api.router.response.RouteRequest;
import com.flyang.api.router.response.RouteResponse;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/4/27
 * 拦截器处理器接口
 */
public interface Chain {
    /**
     * 获取路由实体
     */
    @NonNull
    RouteRequest getRequest();

    /**
     * 获取 activity or fragment instance.
     */
    @NonNull
    Object getSource();

    @NonNull
    Context getContext();

    @Nullable
    Fragment getFragment();

    @Nullable
    android.support.v4.app.Fragment getFragmentV4();

    /**
     * 继续
     */
    @NonNull
    RouteResponse process();

    /**
     * 拦截跳转
     */
    @NonNull
    RouteResponse intercept();
}
