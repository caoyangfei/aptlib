package com.flyang.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flyang.annotation.apt.BindView;
import com.flyang.annotation.apt.InjectParam;
import com.flyang.annotation.apt.OnClick;
import com.flyang.annotation.apt.Router;
import com.flyang.api.bind.FacadeBind;
import com.flyang.api.router.IntentRouter;
import com.flyang.api.router.RouteCallback;
import com.flyang.api.router.response.RouteStatus;
import com.flyang.api.router.template.RouteTable;
import com.flyang.demo.router.DynamicActivity;
import com.flyang.demo.router.HelloService;
import com.flyang.demo.router.WebActivity;

import java.util.Map;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/5/10
 * ------------- Description -------------
 */
@Router("Router")
public class RouterActivity extends Activity {

    @BindView("edit_route")
    EditText edit_route;
    @BindView("btn0")
    Button btn0;
    @BindView("btn1")
    Button btn1;
    @BindView("btn6")
    Button btn6;

    private String uri;

    @InjectParam(key = "HelloServiceImpl")
    HelloService helloService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);
        FacadeBind.bind(this);
        IntentRouter.injectParams(this);

        edit_route.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                uri = s.toString();
                btn0.setText(getString(R.string.go_to, s));
            }
        });

        // 动态添加路由
        IntentRouter.handleRouteTable(new RouteTable() {
            @Override
            public void handle(Map<String, Class<?>> map) {
                map.put("dynamic", DynamicActivity.class);
            }
        });
    }

    @OnClick(value = {"btn0", "btn1", "btn2", "btn3", "btn4", "btn5", "btn6", "btn7", "btn8", "btn9", "btn10", "btn11"})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                IntentRouter.build(uri).callback(new RouteCallback() { // 添加结果回调
                    @Override
                    public void callback(RouteStatus status, Uri uri, String message) {
                        if (status == RouteStatus.SUCCEED) {
                            Toast.makeText(RouterActivity.this, "succeed: " + uri.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RouterActivity.this, "error: " + uri + ", " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).go(this);
                break;
            case R.id.btn1:
                helloService.sayHello("测试");
                IntentRouter.build(btn1.getText().toString()).go(this);
                break;
            case R.id.btn2:
                IntentRouter.build("dynamic").go(this);
                break;
            case R.id.btn3:
                IntentRouter.build("result").requestCode(0).with("extra", "Bundle from MainActivity.").go(this);
                break;
            case R.id.btn4:
                startActivity(new Intent(this, WebActivity.class));
                break;
            case R.id.btn5:
                IntentRouter.build(Uri.parse("router://implicit?id=9527&status=success")).go(this);
                break;
            case R.id.btn6:
                IntentRouter.build(btn6.getText().toString()).go(this);
                break;
            case R.id.btn7:
                IntentRouter.build("module1").go(this);
                break;
            case R.id.btn8:
                IntentRouter.build("module2").go(this);
                break;
            case R.id.btn9:
                IntentRouter.build("intercepted").go(this);
                break;
            case R.id.btn10:
                IntentRouter.build("intercepted").skipInterceptors().go(this);
                break;
            case R.id.btn11:
                IntentRouter.build("test").addInterceptors("AInterceptor").go(this);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("extra");
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
