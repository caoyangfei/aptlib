package com.flyang.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flyang.annotation.apt.BindView;
import com.flyang.annotation.apt.OnClick;
import com.flyang.api.bind.FacadeBind;
import com.flyang.api.router.IntentRouter;
import com.flyang.demo.inject.InjectToClass;


/**
 * Created by Tony Shen on 2017/2/7.
 */

public class MainActivity extends Activity {

    @BindView("aop")
    Button aop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacadeBind.bind(this);

        InjectToClass injectToClass = new InjectToClass();
        injectToClass.startInject();
    }

    @OnClick(value = {"aop", "router"})
    void clickText1(View view) {
        switch (view.getId()) {
            case R.id.aop:
                IntentRouter.build("Aop").go(this);
                break;
            case R.id.router:
                IntentRouter.build("Router").go(this);
                break;
        }
    }

}
