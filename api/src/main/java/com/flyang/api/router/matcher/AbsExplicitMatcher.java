package com.flyang.api.router.matcher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * @author caoyangfei
 * @ClassName AbsExplicitMatcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 显式路由匹配(跳转到有注解的activit or fragment)
 */
public abstract class AbsExplicitMatcher extends AbsMatcher {

    public AbsExplicitMatcher(int priority) {
        super(priority);
    }

    @Override
    public Object generate(Context context, Uri uri, @Nullable Class<?> target) {
        if (target == null) {
            return null;
        }
        Object result = null;
        if (Activity.class.isAssignableFrom(target)) {
            result = new Intent(context, target);
        } else if (Fragment.class.isAssignableFrom(target)) {
            try {
                result = target.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (android.support.v4.app.Fragment.class.isAssignableFrom(target)) {
            try {
                result = target.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
