package com.flyang.demo.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flyang.annotation.apt.InjectParam;
import com.flyang.annotation.apt.Router;
import com.flyang.api.router.IntentRouter;
import com.flyang.demo.R;

//隐式启动
@Router({"test", "http://example.com/user", "router://filter/test"})
public class TestActivity extends AppCompatActivity {
    @InjectParam
    String id = "0000";
    @InjectParam(key = "status")
    String sts = "default";

    @InjectParam
    short test1;
    @InjectParam
    byte[] test2;
    @InjectParam
    Model test3;
    @InjectParam
    Model test4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        IntentRouter.injectParams(this);

        Bundle mExtras = getIntent().getExtras();
        id = mExtras.getString("id", id);

    }
}
