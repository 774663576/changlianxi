package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
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
 * 完成验证
 * 重新获取
 * 发送短信
 * @author LG
 *
 */
@SuppressWarnings("deprecation")
public class VerifyFinishActivity extends BaseActivity implements
        OnClickListener, OnSizeChangedListenner {
    private String uid = "";
    private String rid = "";
    private String token = "";
    private String cellphone = "";
    private Button btfinish_yz;
    private Button btGetCode;// 注册界面重新获取验证码按钮
    private TextView textView;
    private EditText editText;
    private String yanzhengma = "";
    private ImageView iv_back;
    private Button btSendMessage;// 发送验证短信按钮
    private String msgText;
    private String PATH = "/users/isetBindCellphone";
    private String PATH2 = "/users/iverifyAuthCode";
    private String PATH3 = "/users/isendAuthCode";
    private TextView titleName;
    private InputMethodRelativeLayout parent;
    private LinearLayout layButtom;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dengluyanzheng);
        CLXApplication.addActivity(this);
        uid = SharedUtils.getString("uid", "");
        rid = getIntent().getExtras().getString("rid");
        token = SharedUtils.getString("token", "");
        cellphone = getIntent().getExtras().getString("cellphone");
        btfinish_yz = (Button) findViewById(R.id.btfinish_yz);
        btfinish_yz.setOnClickListener(this);
        btSendMessage = (Button) findViewById(R.id.bt_sendmessage);
        btSendMessage.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.txt_show_num);
        textView.setText(cellphone);
        editText = (EditText) findViewById(R.id.editCode);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        btGetCode = (Button) findViewById(R.id.bt_get_code);
        btGetCode.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.titleTxt);
        titleName.setText("验证手机号");
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
        layButtom = (LinearLayout) findViewById(R.id.layoutBottom);
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
            case R.id.btfinish_yz:
                yanzhengma = editText.getText().toString();
                if ("".equals(yanzhengma)) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", rid);
                map.put("auth_code", yanzhengma);
                map.put("type", "bindRegister");
                final PostAsyncTask task = new PostAsyncTask(this, map, PATH2);
                task.setTaskCallBack(new PostCallBack() {

                    @Override
                    public void taskFinish(String result) {
                        try {
                            final JSONObject object = new JSONObject(result);
                            int rt = object.getInt("rt");
                            if (rt != 1) {
                                Utils.showToast("啊哦，验证码不对，验证码为6个数字，请再输入一次",
                                        Toast.LENGTH_SHORT);
                            }
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("uid", uid);
                            map2.put("token", token);
                            map2.put("rid", rid);
                            map2.put("cellphone", cellphone.replace("-", ""));
                            PostAsyncTask task = new PostAsyncTask(
                                    VerifyFinishActivity.this, map2, PATH);
                            task.setTaskCallBack(new PostCallBack() {

                                @Override
                                public void taskFinish(String result) {
                                    try {
                                        int rt = object.getInt("rt");
                                        if (rt == 1) {

                                            Intent it = new Intent();
                                            it.setClass(
                                                    VerifyFinishActivity.this,
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

            case R.id.bt_get_code:
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("uid", rid);
                map2.put("cellphone", cellphone.replace("-", ""));
                map2.put("type", "bindRegister");
                PostAsyncTask postAsyncTask = new PostAsyncTask(this, map2,
                        PATH3);
                postAsyncTask.setTaskCallBack(new PostCallBack() {

                    @Override
                    public void taskFinish(String result) {

                    }
                });
                postAsyncTask.execute();
                Intent intent3 = new Intent();
                intent3.setClass(this, VerifyWaitActivity.class);
                intent3.putExtra("cellphone", cellphone);
                intent3.putExtra("rid", rid);
                startActivity(intent3);
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;
            case R.id.back:
                finish();
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;

            case R.id.bt_sendmessage:
                msgText = "常联系#绑定手机号";
                SmsManager smsManager = SmsManager.getDefault();
                PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent(), 0);
                smsManager.sendTextMessage("12114", null, msgText, pIntent,
                        null);
                Intent intent = new Intent();
                intent.setClass(VerifyFinishActivity.this,
                        VerifyIntentActivity.class);
                intent.putExtra("cellphone", cellphone.replace("-", ""));
                intent.putExtra("rid", rid);
                intent.putExtra("num", 1);
                startActivity(intent);
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
            layButtom.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            layButtom.setVisibility(View.VISIBLE);

        }
    }
}
