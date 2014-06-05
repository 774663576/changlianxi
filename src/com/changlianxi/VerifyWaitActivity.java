package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.umeng.analytics.MobclickAgent;

/**
 * 获取验证码等待页面
 * @author LG
 *
 */
public class VerifyWaitActivity extends BaseActivity implements
        OnClickListener, OnSizeChangedListenner {
    private TextView tv_second;
    private int second = 60;// 用于重新获取验证码时间倒计时
    private TextView txtShowNum;
    private ImageView iv_back;
    private String uid = "";// 成功后才有，代表用户ID
    private String token = "";
    private String yanzhengma = "";
    private EditText editText;
    private String rid = "";
    private String cellphone = "";
    private String PATH = "/users/isetBindCellphone";
    private String PATH2 = "/users/iverifyAuthCode";
    private Button btn_yz;
    private LinearLayout bg;
    private TextView title;
    private InputMethodRelativeLayout parent;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_second.setText(second + "秒后您可以");
                    second--;
                    if (second < 0) {
                        Intent intent = new Intent();
                        intent.setClass(VerifyWaitActivity.this,
                                VerifyFinishActivity.class);
                        intent.putExtra("rid", rid);
                        intent.putExtra("cellphone", getIntent().getExtras()
                                .getString("cellphone"));
                        startActivity(intent);
                        finish();
                        return;
                    }
                    this.sendEmptyMessageDelayed(0, 1000);
                    break;
                default:
                    break;
            }

        }
    };

    @SuppressLint({ "HandlerLeak", "HandlerLeak" })
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifywait);
        CLXApplication.addActivity(this);
        Message msg = new Message();
        msg.what = 0;
        mHandler.sendEmptyMessage(0);
        uid = SharedUtils.getString("uid", "");
        token = SharedUtils.getString("token", "");
        txtShowNum = (TextView) findViewById(R.id.txt_show_num2);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editCode2);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tv_second = (TextView) findViewById(R.id.tv_num);
        btn_yz = (Button) findViewById(R.id.btfinish_yz2);
        btn_yz.setOnClickListener(this);
        rid = getIntent().getExtras().getString("rid");
        cellphone = getIntent().getExtras().getString("cellphone")
                .replace("-", "");
        txtShowNum.setText(getIntent().getExtras().getString("cellphone"));
        bg = (LinearLayout) findViewById(R.id.bg);
        bg.setBackgroundResource(R.drawable.back_trans6);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("验证手机号");
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btfinish_yz2:
                yanzhengma = editText.getText().toString();
                if ("".equals(yanzhengma)) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", rid);
                map.put("auth_code", yanzhengma);
                map.put("type", "bindRegister");
                PostAsyncTask task = new PostAsyncTask(this, map, PATH2);
                task.setTaskCallBack(new PostCallBack() {

                    @Override
                    public void taskFinish(String result) {
                        try {
                            final JSONObject object = new JSONObject(result);
                            int rt = object.getInt("rt");
                            if (rt != 1) {
                                Utils.showToast("啊哦，验证码不对，验证码为6个数字，请再输入一次",
                                        Toast.LENGTH_SHORT);
                                return;
                            }
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("uid", uid);
                            map2.put("token", token);
                            map2.put("rid", rid);
                            map2.put("cellphone", cellphone);
                            PostAsyncTask task = new PostAsyncTask(
                                    VerifyWaitActivity.this, map2, PATH);
                            task.setTaskCallBack(new PostCallBack() {

                                @Override
                                public void taskFinish(String result) {
                                    try {
                                        int rt = object.getInt("rt");
                                        if (rt == 1) {
                                            mHandler.removeMessages(0);
                                            Intent it = new Intent();
                                            it.setClass(
                                                    VerifyWaitActivity.this,
                                                    MainActivity.class);
                                            startActivity(it);
                                            finish();
                                        } else {
                                            Utils.showToast(
                                                    "啊哦，验证没有成功，请查看下您的网络是否正常！",
                                                    Toast.LENGTH_SHORT);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            task.execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                task.execute();
                break;
            case R.id.back:
                finish();
                mHandler.removeMessages(0);
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
            default:
                break;
        }
    }

    /**
     * 在Activity中实现OnSizeChangedListener，原理是设置该布局的paddingTop属性来控制子View的偏移
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        if (flag) {// 键盘弹出时
            parent.setPadding(0, -150, 0, 0);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);

        }
    }
}
