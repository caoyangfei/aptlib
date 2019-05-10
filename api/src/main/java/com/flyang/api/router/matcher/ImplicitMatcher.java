package com.flyang.api.router.matcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.flyang.api.router.response.RouteRequest;


/**
 * @author caoyangfei
 * @ClassName ImplicitMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 隐示路由过滤"http(s)"
 * <p>
 * 处理"http(s)"可以使用
 * {@link SchemeMatcher} or {@link BrowserMatcher}
 */
public class ImplicitMatcher extends AbsImplicitMatcher {
    public ImplicitMatcher(int priority) {
        super(priority);
    }

    @Override
    public boolean match(Context context, Uri uri, @Nullable String route, RouteRequest routeRequest) {
        if (uri.toString().toLowerCase().startsWith("http://")
                || uri.toString().toLowerCase().startsWith("https://")) {
            return false;
        }
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                new Intent(Intent.ACTION_VIEW, uri), PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            // bundle 解析
            if (uri.getQuery() != null) {
                parseParams(uri, routeRequest);
            }
            return true;
        }
        return false;
    }
}
