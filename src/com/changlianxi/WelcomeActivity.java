package com.changlianxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.changlianxi.activity.register.RegisterActivity;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

public class WelcomeActivity extends BaseActivity implements OnClickListener {
    private Button btnRegister;
    private Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String userName = SharedUtils.getString("userName", "");
        if (!"".equals(userName)) {
            if (!"".equals(SharedUtils.getString("uid", ""))
                    && !"".equals(SharedUtils.getString("token", ""))) {
                startActivity(new Intent(this, CircleHomeActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        setListener();
    }

    private void setListener() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
        Utils.leftOutRightIn(this);
    }
}
