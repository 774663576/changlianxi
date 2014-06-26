package com.changlianxi;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 短信验证跳转
 * @author LG
 *
 */
public class VerifyIntentActivity extends BaseActivity {
    private TextView textView;
    private String getRid = "";
    private String PATH = "/users/iverifyAuthCode2";
    private int num;// 判断是哪里过来的，1：第三方登录，2：注册,3：找回密码
    private int count = 0;
    private Dialog progressDialog;
    private Timer timer;
    private ImageView back;
    private String str;
    private TextView title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yanzhengduanxin);
        CLXApplication.addActivity(this);
        getRid = getIntent().getExtras().getString("rid");
        num = getIntent().getExtras().getInt("num");
        str = "啊哦，验证失败了，请确认您的本机号码"
                + getIntent().getExtras().getString("cellphone");
        textView = (TextView) findViewById(R.id.textView3);
        textView.setText("您已通过本手机向服务器发送了验证短信“常联系”，我们正在努力验证您的个人信息，请稍候。");
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("验证注册短信");
        timer = new Timer();
        timer.scheduleAtFixedRate(new MyTask(), 1, 5000);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Utils.rightOut(VerifyIntentActivity.this);
            }
        });

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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog == null) {
                        progressDialog = DialogUtil.createLoadingDialog(
                                VerifyIntentActivity.this, "");
                        progressDialog.show();
                    }
                    break;
                case 1:
                    timer.cancel();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Intent intent = new Intent();
                    intent.setClass(VerifyIntentActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    timer.cancel();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Dialog dialog = DialogUtil.promptDialog(
                            VerifyIntentActivity.this, str, "确定",
                            new ConfirmDialog() {
                                @Override
                                public void onOKClick() {
                                    finish();
                                    VerifyIntentActivity.this
                                            .overridePendingTransition(
                                                    R.anim.right_in,
                                                    R.anim.right_out);
                                }

                                @Override
                                public void onCancleClick() {

                                }
                            });
                    dialog.show();

                    break;
                case 3:
                    timer.cancel();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Intent intent1 = new Intent();
                    setResult(RESULT_OK, intent1);
                    finish();
                    break;
                default:
                    break;
            }

        };
    };

    /**
     * 第三方登录短信验证
     */
    private void bindMessage(String type, final int i) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", getRid);
        map.put("type", type);
        PostAsyncTask task = new PostAsyncTask(this, map, PATH);
        task.setTaskCallBack(new PostCallBack() {
            public void taskFinish(String result) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    int rt = object.getInt("rt");
                    if (rt == 1) {
                        mHandler.sendEmptyMessage(i);
                        return;
                    }
                    count++;
                    if (count == 12) {
                        mHandler.sendEmptyMessage(2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute();
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            if (num == 1) {
                bindMessage("bindRegister", 1);
            } else if (num == 2) {
                bindMessage("register", 3);

            } else {
                bindMessage("retrievePasswd", 3);
            }
        }
    }

}
