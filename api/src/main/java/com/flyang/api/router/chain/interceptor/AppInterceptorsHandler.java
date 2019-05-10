package com.flyang.api.router.chain.interceptor;

import android.support.annotation.NonNull;

import com.flyang.api.router.AptHub;
import com.flyang.api.router.chain.RealInterceptorChain;
import com.flyang.api.router.response.RouteRequest;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.IntentRouter;
import com.flyang.api.router.chain.Chain;
import com.flyang.basic.log.LogUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caoyangfei
 * @ClassName AppInterceptorsHandler
 * @date 2019/4/27
 * ------------- Description -------------
 * 自定义拦截器处理队列
 */
public class AppInterceptorsHandler implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        if (chain.getRequest().isSkipInterceptors()) {
            return chain.process();
        }

        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        RouteRequest request = chain.getRequest();

        // 全局拦截器
        if (!IntentRouter.getGlobalInterceptors().isEmpty()) {
            realChain.getInterceptors().addAll(IntentRouter.getGlobalInterceptors());
        }

        Set<String> finalInterceptors = new LinkedHashSet<>();
        // 注解拦截器
        if (realChain.getTargetClass() != null) {
            String[] baseInterceptors = AptHub.targetInterceptorsTable.get(realChain.getTargetClass());
            if (baseInterceptors != null && baseInterceptors.length > 0) {
                Collections.addAll(finalInterceptors, baseInterceptors);
            }
        }

        // 添加 or 移除临时拦截器
        if (request.getTempInterceptors() != null) {
            for (Map.Entry<String, Boolean> entry : request.getTempInterceptors().entrySet()) {
                if (entry.getValue() == Boolean.TRUE) {
                    finalInterceptors.add(entry.getKey());
                } else {
                    finalInterceptors.remove(entry.getKey());
                }
            }
        }

        if (!finalInterceptors.isEmpty()) {
            for (String name : finalInterceptors) {
                RouteInterceptor interceptor = AptHub.interceptorInstances.get(name);
                if (interceptor == null) {
                    Class<? extends RouteInterceptor> clz = AptHub.interceptorTable.get(name);
                    try {
                        interceptor = clz.newInstance();
                        AptHub.interceptorInstances.put(name, interceptor);
                    } catch (Exception e) {
                        LogUtils.e("Can't construct a interceptor instance for: " + name, e);
                    }
                }
                // 添加进队列
                if (interceptor != null) {
                    realChain.getInterceptors().add(interceptor);
                }
            }
        }

        return chain.process();
    }
}
