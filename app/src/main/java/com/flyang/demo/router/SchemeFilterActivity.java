package com.flyang.demo.router;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flyang.api.router.IntentRouter;

/**
 * How to handle route from browser.
 * Created by chen on 17-5-9.
 */
public class SchemeFilterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d("SchemeFilterActivity", "uri: " + uri.toString());
            if (!"router://filter".equals(uri.toString())) {
                IntentRouter.build(uri).go(this);
            }
            finish();
        }
    }
}
