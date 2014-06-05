package com.changlianxi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBaseHelper;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.util.BaiDuPushUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.umeng.analytics.MobclickAgent;

public class EditLoginActivity extends BaseActivity {
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_login);
        CLXApplication.addActivity(this);

        editDialog();
    }

    /**
     * 设置页面统计
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    private void editDialog() {
        dialog = DialogUtil.promptDialog(this, "您的账号已在其他设备登录，请重新登录!", "确定",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        exit1();
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    private void exit1() {
        finish();
        SharedUtils.setString("uid", "");
        SharedUtils.setString("token", "");
        BaiDuPushUtils.setBind(CLXApplication.getInstance(), false);
        DataBaseHelper.setIinstanceNull();
        DBUtils.dbase = null;
        CLXApplication.exit(false);
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);

    }
}
