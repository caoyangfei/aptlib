package com.flyang.demo.aop;

import android.app.Activity;
import android.os.Bundle;

import com.flyang.annotation.aop.LogMethod;
import com.flyang.annotation.apt.Router;


/**
 * Created by Tony Shen on 2017/2/7.
 */

@Router(value = {"DemoForLog"}, interceptors = {"AInterceptor"})
public class DemoForLogMethodActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData1();

        initData2("test");

    }

    @LogMethod
    private void initData1() {
    }

    @LogMethod
    private String initData2(String s) {

        return s;
    }

}
