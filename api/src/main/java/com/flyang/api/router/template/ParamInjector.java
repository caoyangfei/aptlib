package com.flyang.api.router.template;

/**
 * @author caoyangfei
 * @ClassName ParamInjector
 * @date 2019/4/27
 * ------------- Description -------------
 * 参数
 */
public interface ParamInjector {
    /**
     * 初始化注入
     *
     * @param obj Activity or fragment instance.
     */
    void inject(Object obj);
}
