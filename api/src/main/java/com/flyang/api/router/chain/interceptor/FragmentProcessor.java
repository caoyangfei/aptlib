package com.flyang.api.router.chain.interceptor;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.flyang.api.router.AptHub;
import com.flyang.api.router.MatcherRegistry;
import com.flyang.api.router.chain.Chain;
import com.flyang.api.router.chain.RealInterceptorChain;
import com.flyang.api.router.matcher.AbsExplicitMatcher;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.util.log.LogUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author caoyangfei
 * @ClassName FragmentProcessor
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器
 * <p>
 * 获取fragment对象
 */
public class FragmentProcessor implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        Set<Map.Entry<String, Class<?>>> entries = AptHub.routeTable.entrySet();
        List<AbsExplicitMatcher> matcherList = MatcherRegistry.getExplicitMatcher();

        for (AbsExplicitMatcher matcher : matcherList) {
            for (Map.Entry<String, Class<?>> entry : entries) {
                if (matcher.match(chain.getContext(), chain.getRequest().getUri(), entry.getKey(), chain.getRequest())) {
                    LogUtils.i(String.format("{uri=%s, matcher=%s}",
                            chain.getRequest().getUri(), matcher.getClass().getCanonicalName()));
                    realChain.setTargetClass(entry.getValue());
                    Object result = matcher.generate(chain.getContext(), chain.getRequest().getUri(), entry.getValue());
                    if (result instanceof android.support.v4.app.Fragment) {
                        android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) result;
                        Bundle bundle = chain.getRequest().getExtras();
                        if (bundle != null && !bundle.isEmpty()) {
                            fragment.setArguments(bundle);
                        }
                        realChain.setTargetObject(fragment);
                    } else if (result instanceof Fragment) {
                        Fragment fragment = (Fragment) result;
                        Bundle bundle = chain.getRequest().getExtras();
                        if (bundle != null && !bundle.isEmpty()) {
                            fragment.setArguments(bundle);
                        }
                        realChain.setTargetObject(fragment);
                    } else {
                        return RouteResponse.assemble(RouteStatus.FAILED, String.format(
                                "The matcher can't generate a fragment instance for uri: %s",
                                chain.getRequest().getUri().toString()));
                    }
                    return chain.process();
                }
            }
        }

        return RouteResponse.assemble(RouteStatus.NOT_FOUND, String.format(
                "Can't find a fragment that matches the given uri: %s",
                chain.getRequest().getUri().toString()));
    }
}
