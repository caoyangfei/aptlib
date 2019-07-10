package com.flyang.moudle;

import com.flyang.annotation.apt.InstanceFactory;
import com.flyang.util.log.LogUtils;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/7/9
 * ------------- Description -------------
 */
@InstanceFactory
public class TestPresenter {

    public void setString() {
        LogUtils.e("打印测试");
    }
}
