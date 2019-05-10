package com.flyang.api.router.matcher;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.flyang.api.router.response.RouteRequest;


/**
 * @author caoyangfei
 * @ClassName Matcher
 * @date 2019/4/27
 * ------------- Description -------------
 * 路由匹配规则(Match rule)
 */
interface Matcher extends Comparable<Matcher> {
    /**
     * 路由匹配.
     *
     * @param context      Context.
     * @param uri          the given uri.
     * @param route        path in router table.
     * @param routeRequest {@link RouteRequest}.
     * @return {@code true}: success<br>{@code false}: fail
     */
    boolean match(Context context, Uri uri, @Nullable String route, RouteRequest routeRequest);

    /**
     * 路由匹配成功后调用（返回Intent对象）
     *
     * @param context Context.
     * @param uri     The given uri.
     * @param target  目标 Activity or Fragment.
     * @return An object(intent/fragment) that the matcher generated.
     */
    Object generate(Context context, Uri uri, @Nullable Class<?> target);
}
