package com.flyang.api.router.response;

/**
 * @author caoyangfei
 * @ClassName RouteStatus
 * @date 2019/4/27
 * ------------- Description -------------
 * 响应结果状态
 */
public enum RouteStatus {
    PROCESSING,//初始
    SUCCEED,//成功
    INTERCEPTED,//拦截
    NOT_FOUND,//没找到intent/fragment
    FAILED;//失败

    public boolean isSuccessful() {
        return this == SUCCEED;
    }
}
