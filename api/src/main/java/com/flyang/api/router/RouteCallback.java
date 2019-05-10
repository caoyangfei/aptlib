package com.flyang.api.router;

import android.net.Uri;

import com.flyang.api.router.response.RouteStatus;

import java.io.Serializable;

/**
 * @author caoyangfei
 * @ClassName RouteCallback
 * @date 2019/4/27
 * ------------- Description -------------
 * 跳转结果回调接口
 */
public interface RouteCallback extends Serializable {
    /**
     * Callback
     *
     * @param status  跳转结果状态标记{@link RouteStatus}
     * @param uri     uri
     * @param message 响应结果String
     */
    void callback(RouteStatus status, Uri uri, String message);
}
