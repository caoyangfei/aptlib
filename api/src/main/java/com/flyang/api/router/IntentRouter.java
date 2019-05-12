package com.flyang.api.router;

import android.net.Uri;

import com.flyang.api.router.chain.interceptor.RouteInterceptor;
import com.flyang.api.router.matcher.AbsMatcher;
import com.flyang.api.router.response.RouteRequest;
import com.flyang.api.router.template.RouteTable;
import com.flyang.util.log.LogUtils;
import com.flyang.util.log.config.LogLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoyangfei
 * @ClassName IntentRouter
 * @date 2019/4/27
 * ------------- Description -------------
 * 路由接口（对外使用）
 */
public class IntentRouter {
    //传递Uri key值 <code>intent.getStringExtra(IntentRouter.RAW_URI)</code>
    public static final String RAW_URI = "raw_uri";

    private static final List<RouteInterceptor> sGlobalInterceptors = new ArrayList<>();

    static {
        LogUtils.getLogConfig()
                .configTagPrefix("IntentRouter:")
                .configLevel(LogLevel.TYPE_VERBOSE); // 配置可展示日志等级;
    }

    public static IRouter build(String path) {
        return build(path == null ? null : Uri.parse(path));
    }

    public static IRouter build(Uri uri) {
        return RealRouter.getInstance().build(uri);
    }

    public static IRouter build(RouteRequest request) {
        return RealRouter.getInstance().build(request);
    }

    /**
     * 动态添加路由
     *
     * @param routeTable
     */
    public static void handleRouteTable(RouteTable routeTable) {
        if (routeTable != null) {
            routeTable.handle(AptHub.routeTable);
        }
    }

    /**
     * 注入bundle，注解获取参数
     *
     * @param obj
     */
    public static void injectParams(Object obj) {
        AptHub.injectParams(obj);
    }

    /**
     * 添加全局拦截器
     *
     * @param routeInterceptor
     */
    public static void addGlobalInterceptor(RouteInterceptor routeInterceptor) {
        sGlobalInterceptors.add(routeInterceptor);
    }

    /**
     * 获取全局拦截器
     *
     * @return
     */
    public static List<RouteInterceptor> getGlobalInterceptors() {
        return sGlobalInterceptors;
    }

    /**
     * 注册自定义路由Uri匹配
     *
     * @param matcher
     */
    public static void registerMatcher(AbsMatcher matcher) {
        MatcherRegistry.register(matcher);
    }

    /**
     * 清楚路由Uri匹配
     */
    public static void clearMatcher() {
        MatcherRegistry.clear();
    }
}
