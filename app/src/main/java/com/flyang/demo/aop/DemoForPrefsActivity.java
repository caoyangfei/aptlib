package com.flyang.demo.aop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.flyang.annotation.aop.Prefs;
import com.flyang.annotation.apt.OnClick;
import com.flyang.demo.R;


/**
 * Created by Tony Shen on 2017/2/8.
 */

public class DemoForPrefsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_for_prefs);
//        Injector.injectInto(this);

        initData();
    }

    @Prefs(key = "article")
    private Article initData() {

        Article article = new Article();
        article.author = "tony";
        article.title = "kotlin in action";
        article.createDate = "2017-01-02";
        article.content = "just a test...";

        return article;
    }
    @OnClick(value={"text"})
    void clickText(View view) {

    }
}
