package com.flyang.api.router.response;

/**
 * @author caoyangfei
 * @ClassName RouteStatus
 * @date 2019/4/27
 * ------------- Description -------------
 * 响应结果状态
 */
public enum RouteStatus {
    PROCESSING,
    SUCCEED,
    INTERCEPTED,
    NOT_FOUND,
    FAILED;

    public boolean isSuccessful() {
        return this == SUCCEED;
    }
}
