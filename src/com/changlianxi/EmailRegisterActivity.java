package com.changlianxi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.RegisterTask;
import com.changlianxi.task.RegisterTask.RegisterFinish;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.changlianxi.view.MyViewGroup;
import com.changlianxi.view.SearchEditText;

public class EmailRegisterActivity extends BaseActivity implements
        OnClickListener, OnSizeChangedListenner, PostCallBack {
    private MyViewGroup rGroup;
    private View reg1, reg2, reg3;
    private LayoutParams params;
    private LayoutInflater flater;
    private Button reg1BtnNext;
    private EditText reg1EditEmail;
    private InputMethodRelativeLayout parent;
    private LinearLayout layButtom;
    private TextView title;
    private ImageView back;
    private TextView textView2;
    private TextView emailShow;
    private Button bthFinishYz;
    private EditText emailCode;
    private EditText setPassword;
    private Button btnFinishRegister;
    private Button btnAgainGetCode;
    private TextView txtSecond;
    private Dialog dialog;
    private int currentStep = 1;
    private int second = 60;// 用于重新获取验证码时间倒计时

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    txtSecond.setText(second + "秒后您可以");
                    second--;
                    if (second < 0) {
                        txtSecond.setVisibility(View.GONE);
                        btnAgainGetCode.setEnabled(true);
                        return;
                    }
                    this.sendEmptyMessageDelayed(0, 1000);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);
        initView();
    }

    private void initView() {
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        rGroup = (MyViewGroup) findViewById(R.id.regisGroup);
        flater = LayoutInflater.from(this);
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("注册新用户");
        back = (ImageView) findViewById(R.id.back);
        initReg1();
        rGroup.addView(reg1, params);
        initReg2();
        initReg3();
        setListener();
    }

    private void initReg1() {
        reg1 = flater.inflate(R.layout.emailregister1, null);
        reg1BtnNext = (Button) reg1.findViewById(R.id.next);
        reg1EditEmail = (SearchEditText) reg1.findViewById(R.id.editEmail);
        reg1EditEmail.requestFocus();

    }

    private void initReg2() {
        reg2 = flater.inflate(R.layout.emailregister2, null);
        textView2 = (TextView) reg2.findViewById(R.id.textView2);
        emailShow = (TextView) reg2.findViewById(R.id.txt_show_email);
        bthFinishYz = (Button) reg2.findViewById(R.id.btfinish_yz);
        layButtom = (LinearLayout) reg2.findViewById(R.id.lay_bottom);
        emailCode = (EditText) reg2.findViewById(R.id.emailCode);
        btnAgainGetCode = (Button) reg2.findViewById(R.id.btAgainCode);
        txtSecond = (TextView) reg2.findViewById(R.id.txt_second);
    }

    private void initReg3() {
        reg3 = flater.inflate(R.layout.emailregister3, null);
        setPassword = (EditText) reg3.findViewById(R.id.setPassword);
        btnFinishRegister = (Button) reg3.findViewById(R.id.btfinish_register);

    }

    private void setListener() {
        parent.setOnSizeChangedListenner(this);
        back.setOnClickListener(this);
        reg1EditEmail.setOnFocusChangeListener(new OnEditFocusChangeListener(
                reg1EditEmail, this));
        reg1BtnNext.setOnClickListener(this);
        bthFinishYz.setOnClickListener(this);
        parent.setOnSizeChangedListenner(this);
        btnFinishRegister.setOnClickListener(this);
        btnAgainGetCode.setOnClickListener(this);

    }

    @Override
    public void onSizeChange(boolean paramBoolean, int w, int h) {
        if (paramBoolean) {// 键盘弹出时
            parent.setPadding(0, -150, 0, 0);
            textView2.setVisibility(View.GONE);
            emailShow.setVisibility(View.GONE);
            layButtom.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            textView2.setVisibility(View.VISIBLE);
            emailShow.setVisibility(View.VISIBLE);
            layButtom.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                OnBackClick();
                break;
            case R.id.next:
                String strEmail = reg1EditEmail.getText().toString();
                if (!Utils.isEmail(strEmail)) {
                    Utils.showToast("邮箱格式不正确", Toast.LENGTH_SHORT);
                    return;
                }
                register(strEmail);
                break;
            case R.id.btfinish_yz:
                verifyAuthCode();
                break;
            case R.id.btfinish_register:
                setPassword();
                break;
            case R.id.btAgainCode:
                if (second > 0) {
                    return;
                }
                getAuthCode();
                break;
            default:
                break;
        }
    }

    /**
     * 重新获取验证码
     * 
     * @param result
     */
    private void checkGetAuthCode(String result) {
        if (dialog != null) {
            dialog.dismiss();
        }
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt != 1) {
                Utils.showToast("啊哦，验证码获取没有成功，请查看下您的网络是否正常！",
                        Toast.LENGTH_SHORT);
                return;
            }
            second = 60;
            mHandler.sendEmptyMessage(0);
            txtSecond.setVisibility(View.VISIBLE);
            btnAgainGetCode.setEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAuthCode() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("type", "register");
        map.put("email", emailShow.getText().toString());
        PostAsyncTask task = new PostAsyncTask(this, map,
                "/users/isendAuthCode");
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                checkGetAuthCode(result);
            }
        });
        task.execute();
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
    }

    /**
     * 判断密码是否设置成功
     * 
     * @param result
     */
    private void checkPassword(String result) {
        if (dialog != null) {
            dialog.dismiss();
        }
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setString("token", object.getString("token"));
                SharedUtils.setString("uid", object.getString("uid"));
                Intent intent = new Intent();
                intent.setClass(EmailRegisterActivity.this,
                        RegisterFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Utils.showToast("啊哦，密码设置没有成功，请查看下您的网络是否正常！", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPassword() {
        String pswd = setPassword.getText().toString().replace(" ", "");
        if (pswd.length() < 6) {
            Utils.showToast("请设置至少6个字符的密码", Toast.LENGTH_SHORT);
            return;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("version", Utils.getVersionName(EmailRegisterActivity.this));
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        map.put("email", emailShow.getText().toString());
        map.put("passwd", pswd);
        PostAsyncTask task = new PostAsyncTask(this, map,
                "/users/icompleteRegister");
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                checkPassword(result);
            }
        });
        task.execute();
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
    }

    /**
     * 判断验证码是否正确
     * 
     * @param result
     */
    private void CheckCode(String result) {
        if (dialog != null) {
            dialog.dismiss();
        }
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                rGroup.setView(reg3);
                Utils.popUp(this);
                currentStep = 3;
            } else {
                Utils.showToast("啊哦，验证码不对\n验证码为6个数字，请再确认输入一次",
                        Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            Logger.error(this, e);
            e.printStackTrace();
        }
    }

    /**
     * 验证
     */
    private void verifyAuthCode() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("auth_code", emailCode.getText().toString());
        map.put("type", "register");
        PostAsyncTask task = new PostAsyncTask(this, map,
                "/users/iverifyAuthCode");
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                CheckCode(result);
            }
        });
        task.execute();
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
    }

    private void getRegisiterResult(String resutl) {
        try {
            JSONObject object = new JSONObject(resutl);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setString("uid", object.getString("uid"));
                rGroup.addView(reg2);
                emailShow.setText(reg1EditEmail.getText().toString());
                currentStep = 2;
                mHandler.sendEmptyMessage(0);
                return;
            } else {
                String errorCoce = object.getString("err");
                if (errorCoce.equals("USER_ALREADY_EXIST")) {
                    Utils.showToast("这个邮箱已经注册啦！如忘记密码可以用找回密码功能哦。",
                            Toast.LENGTH_SHORT);
                } else {
                    Utils.showToast("啊哦，操作没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void register(String email) {
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        RegisterTask task = new RegisterTask(this, "", email);
        task.setCallBack(new RegisterFinish() {

            @Override
            public void registerFinish(String result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                getRegisiterResult(result);
            }
        });
        task.execute();
    }

    @Override
    public void taskFinish(String result) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            OnBackClick();
        }
        return false;
    }

    private void OnBackClick() {
        switch (currentStep) {
            case 1:
                exit();
                break;
            case 2:
                rGroup.setView(reg1);
                currentStep = 1;
                break;
            case 3:
                rGroup.setView(reg1);
                currentStep = 2;
                break;
            default:
                break;
        }
    }
}
