package com.flyang.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.flyang.annotation.apt.OnClick;
import com.flyang.annotation.apt.Router;
import com.flyang.api.bind.FacadeBind;
import com.flyang.demo.aop.DemoForAsyncActivity;
import com.flyang.demo.aop.DemoForCacheableActivity;
import com.flyang.demo.aop.DemoForHookMethodActivity;
import com.flyang.demo.aop.DemoForLogMethodActivity;
import com.flyang.demo.aop.DemoForPrefsActivity;
import com.flyang.demo.aop.DemoForSafeActivity;
import com.flyang.demo.aop.DemoForTraceActivity;
import com.flyang.demo.aop.TestActivity;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/5/10
 * ------------- Description -------------
 */
@Router("Aop")
public class AopActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aop);
        FacadeBind.bind(this);

    }

    @OnClick(value = {"text1"})
    void clickText1(View view) {
        Intent i = new Intent(AopActivity.this, DemoForAsyncActivity.class);
        startActivity(i);
    }


    @OnClick(value = {"text2"})
    void clickText2(View view) {

        Intent i = new Intent(AopActivity.this, DemoForCacheableActivity.class);
        startActivity(i);
    }

    @OnClick(value = {"text3"})
    public void clickText3(View view) {
        switch (view.getId()) {
            case R.id.text3:
                Intent i = new Intent(AopActivity.this, DemoForLogMethodActivity.class);
                startActivity(i);
                break;
        }
    }

    @OnClick(value = {"text4"})
    void clickText4(View view) {

        Intent i = new Intent(AopActivity.this, DemoForHookMethodActivity.class);
        startActivity(i);
    }

    @OnClick(value = {"text5"})
    void clickText5(View view) {

        Intent i = new Intent(AopActivity.this, DemoForPrefsActivity.class);
        startActivity(i);
    }

    @OnClick(value = {"text6"})
    void clickText6(View view) {

        Intent i = new Intent(AopActivity.this, DemoForSafeActivity.class);
        startActivity(i);
    }

    @OnClick(value = {"text7"})
    void clickText7(View view) {

        Intent i = new Intent(AopActivity.this, DemoForTraceActivity.class);
        startActivity(i);
    }

    @OnClick(value = {"text8"})
    void clickText8(View view) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + "10000"));
        startActivity(intent);
    }

    @OnClick(value = {"text9"})
    void clickText9(View view) {

        Intent i = new Intent(AopActivity.this, TestActivity.class);
        startActivity(i);
    }

}
