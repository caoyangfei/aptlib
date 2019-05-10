package com.flyang.api.router.chain.interceptor;

import android.support.annotation.NonNull;

import com.flyang.api.router.MatcherRegistry;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.api.router.chain.Chain;
import com.flyang.api.router.matcher.AbsMatcher;

import java.util.List;

/**
 * @author caoyangfei
 * @ClassName IntentValidator
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器
 * <p>
 * 验证路由
 */
public class IntentValidator implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        List<AbsMatcher> matcherList = MatcherRegistry.getMatcher();
        if (matcherList.isEmpty()) {
            return RouteResponse.assemble(RouteStatus.FAILED, "The MatcherRegistry contains no matcher.");
        }
        return chain.process();
    }
}
