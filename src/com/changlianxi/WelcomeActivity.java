package com.changlianxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Global;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {
    private ImageView imgWelcome1;
    private LinearLayout layWelcome2;
    private Button btnGo;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    imgWelcome1.setVisibility(View.GONE);
                    layWelcome2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if (!"".equals(Global.getUid())
                            && !"".equals(Global.getUserToken())) {
                        startActivity(new Intent(WelcomeActivity.this,
                                MainActivity.class));
                    } else {
                        startActivity(new Intent(WelcomeActivity.this,
                                LoginActivity.class));
                    }
                    finish();
                    break;
                default:
                    break;
            }
        };
    };

    /**
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        CLXApplication.exit(false);
        if (SharedUtils.getBoolean("welcome", false)) {
            mHandler.sendEmptyMessageDelayed(1, 1000);
            return;
        }
        imgWelcome1 = (ImageView) findViewById(R.id.imgWelcome1);
        layWelcome2 = (LinearLayout) findViewById(R.id.layWelcome2);
        btnGo = (Button) findViewById(R.id.btnGO);
        btnGo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,
                        LoginActivity.class));
                finish();
                Utils.leftOutRightIn(WelcomeActivity.this);

            }
        });
        mHandler.sendEmptyMessageDelayed(0, 3000);
        SharedUtils.setBoolean("welcome", true);

    }
}
