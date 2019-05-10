package com.flyang.demo.aop;

import android.app.Activity;
import android.os.Bundle;

import com.flyang.annotation.aop.Cacheable;
import com.flyang.demo.R;


/**
 * Created by Tony Shen on 2017/2/7.
 */

public class DemoForCacheableActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_for_cacheable);
//        Injector.injectInto(this);

        initData();
    }

    @Cacheable(key = "address")
    private Address initData() {

        Address address = new Address();
        address.country = "China";
        address.province = "Jiangsu";
        address.city = "Suzhou";
        address.street = "Ren min Road";

        return address;
    }

//    @OnClick(id={R.id.text})
    void clickText() {

    }
}
