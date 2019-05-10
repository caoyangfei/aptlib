package com.flyang.api.router.matcher;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.flyang.api.router.response.RouteRequest;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author caoyangfei
 * @ClassName AbsMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 路由匹配抽象类
 */
public abstract class AbsMatcher implements Matcher {

    //设置优先级
    private int priority;

    public AbsMatcher(int priority) {
        this.priority = priority;
    }

    //解析参数
    protected void parseParams(Uri uri, RouteRequest routeRequest) {
        if (uri.getQuery() != null) {
            Bundle bundle = routeRequest.getExtras();
            if (bundle == null) {
                bundle = new Bundle();
                routeRequest.setExtras(bundle);
            }

            Set<String> keys = uri.getQueryParameterNames();
            Iterator<String> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                List<String> values = uri.getQueryParameters(key);
                if (values.size() > 1) {
                    bundle.putStringArray(key, values.toArray(new String[0]));
                } else if (values.size() == 1) {
                    bundle.putString(key, values.get(0));
                }
            }
        }
    }

    protected boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    @Override
    public int compareTo(@NonNull Matcher matcher) {
        if (this == matcher) {
            return 0;
        }
        if (matcher instanceof AbsMatcher) {
            if (this.priority > ((AbsMatcher) matcher).priority) {
                return -1;
            } else {
                return 1;
            }
        }
        return matcher.compareTo(this);
    }

}
