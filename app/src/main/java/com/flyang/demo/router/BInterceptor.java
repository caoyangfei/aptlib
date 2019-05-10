package com.flyang.demo.router;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.flyang.annotation.apt.Interceptor;
import com.flyang.api.router.chain.Chain;
import com.flyang.api.router.chain.interceptor.RouteInterceptor;
import com.flyang.api.router.response.RouteResponse;


/**
 * Created by chenenyu on 2018/5/18.
 */
@Interceptor("BInterceptor")
public class BInterceptor implements RouteInterceptor {
    @NonNull
    @Override
    public RouteResponse intercept(Chain chain) {
        Toast.makeText(chain.getContext(), String.format("Intercepted: {uri: %s, interceptor: %s}",
                chain.getRequest().getUri().toString(), BInterceptor.class.getName()),
                Toast.LENGTH_LONG).show();
        return chain.intercept();
    }
}
