package com.flyang.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyang.annotation.apt.BindView;
import com.flyang.annotation.apt.OnClick;
import com.flyang.api.bind.FacadeBind;
import com.flyang.api.router.IntentRouter;


/**
 * Created by Tony Shen on 2017/2/7.
 */

public class MainActivity extends Activity {
    @BindView("text1")
    TextView textView1;
    @BindView("text2")
    TextView textView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacadeBind.bind(this);

        InjectToClass injectToClass = new InjectToClass();
        injectToClass.startInject();
    }

    @OnClick(value = {"text1"})
    void clickText1(View view) {
        Intent i = new Intent(MainActivity.this, DemoForAsyncActivity.class);
        startActivity(i);
    }

    //
//    @OnClick(id = {R.id.text2})
//    void clickText2() {
//
//        Intent i = new Intent(MainActivity.this, DemoForCacheableActivity.class);
//        startActivity(i);
//    }
//
    public void clickText3(View view) {
        switch (view.getId()) {
            case R.id.text3:
                IntentRouter.build("DemoForLog").go(this);
//                Intent i = new Intent(MainActivity.this, DemoForLogMethodActivity.class);
//                startActivity(i);
                break;
        }
    }

    //    @OnClick(id = {R.id.text4})
//    void clickText4() {
//
//        Intent i = new Intent(MainActivity.this, DemoForHookMethodActivity.class);
//        startActivity(i);
//    }
//
//    @OnClick(id = {R.id.text5})
//    void clickText5() {
//
//        Intent i = new Intent(MainActivity.this, DemoForPrefsActivity.class);
//        startActivity(i);
//    }
//
//    @OnClick(id = {R.id.text6})
//    void clickText6() {
//
//        Intent i = new Intent(MainActivity.this, DemoForSafeActivity.class);
//        startActivity(i);
//    }
//
//    @OnClick(id = {R.id.text7})
//    void clickText7() {
//
//        Intent i = new Intent(MainActivity.this, DemoForTraceActivity.class);
//        startActivity(i);
//    }
//
//    @SuppressLint("MissingPermission")
//    @Permission({Manifest.permission.CALL_PHONE})
//    @OnClick(id = {R.id.text8})
//    void clickText8() {
//
//        Intent intent = new Intent(Intent.ACTION_CALL);
//        intent.setData(Uri.parse("tel:" + "10000"));
//        startActivity(intent);
//    }
//

    public void clickText9(View view) {

        IntentRouter.build("moudleTest").go(this);
//        Intent i = new Intent(MainActivity.this, TestActivity.class);
//        startActivity(i);
    }
}
