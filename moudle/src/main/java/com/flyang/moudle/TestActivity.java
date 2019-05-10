package com.flyang.moudle;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flyang.annotation.aop.NeedPermission;
import com.flyang.annotation.aop.PermissionCanceled;
import com.flyang.annotation.aop.PermissionDenied;
import com.flyang.annotation.apt.BindView;
import com.flyang.annotation.apt.OnClick;
import com.flyang.annotation.apt.Router;
import com.flyang.api.bind.FacadeBind;
import com.flyang.api.router.IntentRouter;

@Router({"moudleTest"})
public class TestActivity extends Activity {
    @BindView("btn_click2")
    TextView btn_click12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moudle_test);
        FacadeBind.bind(this);
    }

    @OnClick(value = "btn_click2")
    public void click(View view) {
        IntentRouter.build("moudleTest2").go(this);
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA}, requestCode = 10)
    public void test(View view) {
        Toast.makeText(this, "电话、相机权限申请通过", Toast.LENGTH_SHORT).show();
    }

    /**
     * 申请多个权限
     */
    @PermissionDenied
    public void Denie() {
        Toast.makeText(this, "电话、相机权限拒绝", Toast.LENGTH_SHORT).show();
    }

    /**
     * 申请多个权限
     */
    @PermissionCanceled(requestCode = 10)
    public void cancle() {
        Toast.makeText(this, "电话、相机权限取消", Toast.LENGTH_SHORT).show();
    }
}
