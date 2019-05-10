package com.flyang.demo;

import android.app.Activity;
import android.os.Bundle;

import com.flyang.annotation.aop.Trace;


/**
 * Created by Tony Shen on 2017/2/7.
 */

public class DemoForTraceActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Trace(enable = false)
    private void initData() {

    }
}
