package com.flyang.api.router.chain.interceptor;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.flyang.api.router.response.RouteRequest;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.api.router.chain.Chain;


/**
 * @author caoyangfei
 * @ClassName BaseValidator
 * @date 2019/4/27
 * ------------- Description -------------
 * 拦截器
 * <p>
 * 验证context是不是activity or fragment
 */

public class BaseValidator implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        RouteRequest request = chain.getRequest();
        if (request.getUri() == null) {
            return RouteResponse.assemble(RouteStatus.FAILED, "uri == null.");
        }

        Context context = null;
        if (chain.getSource() instanceof Context) {
            context = (Context) chain.getSource();
        } else if (chain.getSource() instanceof Fragment) {
            if (Build.VERSION.SDK_INT >= 23) {
                context = ((Fragment) chain.getSource()).getContext();
            } else {
                context = ((Fragment) chain.getSource()).getActivity();
            }
        } else if (chain.getSource() instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) chain.getSource()).getContext();
        }
        if (context == null) {
            return RouteResponse.assemble(RouteStatus.FAILED, "Can't retrieve context from source.");
        }

        return chain.process();
    }
}
