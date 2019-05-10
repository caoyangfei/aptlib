package com.flyang.api.router.matcher;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.flyang.api.router.response.RouteRequest;


/**
 * @author caoyangfei
 * @ClassName DirectMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 直接解析（优先级最高，通常用法）
 */
public class DirectMatcher extends AbsExplicitMatcher {

    public DirectMatcher(int priority) {
        super(priority);
    }

    @Override
    public boolean match(Context context, Uri uri, @Nullable String route, RouteRequest routeRequest) {
        return !isEmpty(route) && uri.toString().equals(route);
    }

}
