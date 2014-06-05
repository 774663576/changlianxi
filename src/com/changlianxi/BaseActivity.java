package com.changlianxi;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.Utils;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLXApplication.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    public void exit() {
        finish();
        Utils.rightOut(this);

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
