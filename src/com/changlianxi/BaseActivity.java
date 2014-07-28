package com.changlianxi;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLXApplication.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (savedInstanceState != null) {
            // Intent intent = new Intent();
            // intent.setClass(this, WelcomeActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
            // startActivity(intent);
            // finish();// 关掉自己
        }
    }

    protected void exit() {
        finish();
        Utils.rightOut(this);

    }

    /**
     * 数据统计
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Utils.rightOut(this);
        }
        return super.onKeyDown(keyCode, event);

    }
}
