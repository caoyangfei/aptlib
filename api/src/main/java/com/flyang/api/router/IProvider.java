package com.flyang.api.router;

import android.content.Context;

/**
 * @author caoyangfei
 * @ClassName IProvider
 * @date 2019/8/26
 * ------------- Description -------------
 * 组件化接口基类
 */
public interface IProvider {

    /**
     * 初始化上下文
     *
     * @param context
     */
    void init(Context context);

}
