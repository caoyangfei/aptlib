package com.flyang.demo.aop;

import android.app.Activity;
import android.os.Bundle;

import com.flyang.annotation.aop.HookMethod;


/**
 * Created by Tony Shen on 2017/2/7.
 */

public class DemoForHookMethodActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        testRx();
    }

    @HookMethod(beforeMethod = "method1",afterMethod = "method2")
    private void initData() {

    }

    private void method1() {
    }

    private void method2() {
    }

    private void testRx() {

    }

}
