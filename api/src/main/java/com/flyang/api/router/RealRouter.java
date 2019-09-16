package com.flyang.api.router;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.flyang.api.router.chain.RealInterceptorChain;
import com.flyang.api.router.chain.interceptor.AppInterceptorsHandler;
import com.flyang.api.router.chain.interceptor.BaseValidator;
import com.flyang.api.router.chain.interceptor.FragmentProcessor;
import com.flyang.api.router.chain.interceptor.FragmentValidator;
import com.flyang.api.router.chain.interceptor.IntentProcessor;
import com.flyang.api.router.chain.interceptor.IntentValidator;
import com.flyang.api.router.chain.interceptor.RouteInterceptor;
import com.flyang.api.router.response.RouteResponse;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.util.app.ApplicationUtils;
import com.flyang.util.log.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author caoyangfei
 * @ClassName RealRouter
 * @date 2019/4/27
 * ------------- Description -------------
 * 真正路由处理类，不对外提供
 */
final class RealRouter extends AbsRouter {
    private final RouteInterceptor mBaseValidator = new BaseValidator();
    private final RouteInterceptor mIntentValidator = new IntentValidator();
    private final RouteInterceptor mFragmentValidator = new FragmentValidator();
    private final RouteInterceptor mIntentProcessor = new IntentProcessor();
    private final RouteInterceptor mFragmentProcessor = new FragmentProcessor();
    private final RouteInterceptor mAppInterceptorsHandler = new AppInterceptorsHandler();

    private static final ThreadLocal<RealRouter> mRouterThreadLocal = new ThreadLocal<RealRouter>() {
        @Override
        protected RealRouter initialValue() {
            return new RealRouter();
        }
    };

    private RealRouter() {
    }

    static RealRouter getInstance() {
        return mRouterThreadLocal.get();
    }

    private void callback(RouteResponse response) {
        if (response.getStatus() != RouteStatus.SUCCEED) {
            LogUtils.w(response.getMessage());
        }
        if (mRouteRequest.getRouteCallback() != null) {
            mRouteRequest.getRouteCallback().callback(
                    response.getStatus(), mRouteRequest.getUri(), response.getMessage());
        }
    }

    @Override
    public Object getFragment(@NonNull Object source) {
        List<RouteInterceptor> interceptors = new ArrayList<>();
        Collections.addAll(interceptors, mBaseValidator, mFragmentValidator,
                mFragmentProcessor, mAppInterceptorsHandler);
        RealInterceptorChain chain = new RealInterceptorChain(source, mRouteRequest, interceptors);
        RouteResponse response = chain.process();
        callback(response);
        return response.getResult();
    }

    @Override
    public Intent getIntent(@NonNull Object source) {
        List<RouteInterceptor> interceptors = new ArrayList<>();
        Collections.addAll(interceptors, mBaseValidator, mIntentValidator,
                mIntentProcessor, mAppInterceptorsHandler);
        RealInterceptorChain chain = new RealInterceptorChain(source, mRouteRequest, interceptors);
        RouteResponse response = chain.process();
        callback(response);
        return (Intent) response.getResult();
    }

    @Override
    public void go(Context context) {
        Intent intent = getIntent(context);
        if (intent != null) {
            Bundle options = mRouteRequest.getActivityOptionsBundle();
            if (context instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) context, intent,
                        mRouteRequest.getRequestCode(), options);
                if (mRouteRequest.getEnterAnim() >= 0 && mRouteRequest.getExitAnim() >= 0) {
                    ((Activity) context).overridePendingTransition(
                            mRouteRequest.getEnterAnim(), mRouteRequest.getExitAnim());
                }
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // The below api added in v4:25.1.0
                // ContextCompat.startActivity(context, intent, options);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.startActivity(intent, options);
                } else {
                    context.startActivity(intent);
                }
            }
        }
    }

    @Override
    public void go(Fragment fragment) {
        Intent intent = getIntent(fragment);
        if (intent != null) {
            Bundle options = mRouteRequest.getActivityOptionsBundle();
            if (mRouteRequest.getRequestCode() < 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // 4.1
                    fragment.startActivity(intent, options);
                } else {
                    fragment.startActivity(intent);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // 4.1
                    fragment.startActivityForResult(intent, mRouteRequest.getRequestCode(), options);
                } else {
                    fragment.startActivityForResult(intent, mRouteRequest.getRequestCode());
                }
            }
            if (mRouteRequest.getEnterAnim() >= 0 && mRouteRequest.getExitAnim() >= 0
                    && fragment.getActivity() != null) {
                // Add transition animation.
                fragment.getActivity().overridePendingTransition(
                        mRouteRequest.getEnterAnim(), mRouteRequest.getExitAnim());
            }
        }
    }

    @Override
    public void go(android.support.v4.app.Fragment fragment) {
        Intent intent = getIntent(fragment);
        if (intent != null) {
            Bundle options = mRouteRequest.getActivityOptionsBundle();
            if (mRouteRequest.getRequestCode() < 0) {
                fragment.startActivity(intent, options);
            } else {
                fragment.startActivityForResult(intent, mRouteRequest.getRequestCode(), options);
            }
            if (mRouteRequest.getEnterAnim() >= 0 && mRouteRequest.getExitAnim() >= 0
                    && fragment.getActivity() != null) {
                // Add transition animation.
                fragment.getActivity().overridePendingTransition(
                        mRouteRequest.getEnterAnim(), mRouteRequest.getExitAnim());
            }
        }
    }

    /**
     * 反射获取接口实现对象
     *
     * @return
     */
    public Object navigation() {
        String path = mRouteRequest.getUri().toString();
        if (!AptHub.routeTable.isEmpty() && AptHub.routeTable.containsKey(path)) {
            Class<?> aClass = AptHub.routeTable.get(path);
            try {
                IProvider provider = AptHub.providers.get(aClass);
                if (provider == null) {
                    provider = (IProvider) aClass.getConstructor().newInstance();
                    provider.init(ApplicationUtils.getApp());
                    AptHub.providers.put(aClass, provider);
                }
                return provider;
            } catch (Exception e) {
                LogUtils.e("get intetface failed.", e);
            }
        } else {
            LogUtils.e("没有找到实现接口");
        }
        return null;
    }

}
