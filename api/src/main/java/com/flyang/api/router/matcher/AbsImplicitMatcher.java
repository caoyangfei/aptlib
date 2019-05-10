package com.flyang.api.router.matcher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * @author caoyangfei
 * @ClassName AbsImplicitMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 隐式路由匹配(跳转到没有注解的activity)
 * example:
 * Browser,短信，电话等第三方
 */
public abstract class AbsImplicitMatcher extends AbsMatcher {

    public AbsImplicitMatcher(int priority) {
        super(priority);
    }

    @Override
    public Object generate(Context context, Uri uri, @Nullable Class<?> target) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }

}
