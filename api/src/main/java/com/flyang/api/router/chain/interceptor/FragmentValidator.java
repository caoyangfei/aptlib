package com.flyang.api.router.chain.interceptor;

import android.support.annotation.NonNull;

import com.flyang.api.router.AptHub;
import com.flyang.api.router.MatcherRegistry;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.api.router.chain.Chain;
import com.flyang.api.router.matcher.AbsExplicitMatcher;

import java.util.List;

/**
 * @author caoyangfei
 * @ClassName FragmentValidator
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器
 * <p>
 */
public class FragmentValidator implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        // Fragment只能匹配显式Matcher
        List<AbsExplicitMatcher> matcherList = MatcherRegistry.getExplicitMatcher();
        if (matcherList.isEmpty()) {
            return RouteResponse.assemble(RouteStatus.FAILED, "The MatcherRegistry contains no explicit matcher.");
        }
        if (AptHub.routeTable.isEmpty()) {
            return RouteResponse.assemble(RouteStatus.FAILED, "The RouteTable is empty.");
        }
        return chain.process();
    }
}
