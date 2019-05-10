package com.flyang.api.router.matcher;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.flyang.api.router.response.RouteRequest;

/**
 * @author caoyangfei
 * @ClassName BrowserMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 打开网页
 * {@link android.content.Intent#ACTION_VIEW}
 */
public class BrowserMatcher extends AbsImplicitMatcher {
    public BrowserMatcher(int priority) {
        super(priority);
    }

    @Override
    public boolean match(Context context, Uri uri, @Nullable String route, RouteRequest routeRequest) {
        return (uri.toString().toLowerCase().startsWith("http://")
                || uri.toString().toLowerCase().startsWith("https://"));
    }
}
