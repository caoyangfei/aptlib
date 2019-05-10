package com.flyang.demo.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flyang.annotation.apt.Router;
import com.flyang.demo.R;

//@Route(value = "intercepted", interceptors = {"AInterceptor", "BInterceptor"})
@Router(value = "intercepted", interceptors = {"BInterceptor", "AInterceptor"})
public class InterceptedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercepted);
    }
}
