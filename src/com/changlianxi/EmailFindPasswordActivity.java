package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

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

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.inteface.PasswordEditTextWatcher;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.changlianxi.view.MyViewGroup;

public class EmailFindPasswordActivity extends BaseActivity implements
        OnClickListener, OnSizeChangedListenner, PostCallBack {
    private MyViewGroup rGroup;
    private View find1, find2, find3;
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
    private Button findByCellphone;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    txtSecond.setText(second + "秒后您可以");
                    second--;
                    if (second < 0) {
                        txtSecond.setVisibility(View.GONE);
                        btnAgainGetCode.setEnabled(true);
                        btnAgainGetCode
                                .setBackgroundResource(R.drawable.btn_tran51);
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
        setContentView(R.layout.activity_email_find_password);
        initView();
    }

    private void initView() {
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        rGroup = (MyViewGroup) findViewById(R.id.myGroup);
        flater = LayoutInflater.from(this);
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("找回密码");
        back = (ImageView) findViewById(R.id.back);
        initReg1();
        rGroup.addView(find1, params);
        initReg2();
        initReg3();
        setListener();
    }

    private void initReg1() {
        find1 = flater.inflate(R.layout.email_find1, null);
        reg1BtnNext = (Button) find1.findViewById(R.id.next);
        reg1EditEmail = (EditText) find1.findViewById(R.id.editEmail);
        reg1EditEmail.requestFocus();
        findByCellphone = (Button) find1.findViewById(R.id.btnFindByCellphone);
    }

    private void initReg2() {
        find2 = flater.inflate(R.layout.email_find2, null);
        textView2 = (TextView) find2.findViewById(R.id.textView2);
        emailShow = (TextView) find2.findViewById(R.id.txt_show_email);
        bthFinishYz = (Button) find2.findViewById(R.id.btfinish_yz);
        layButtom = (LinearLayout) find2.findViewById(R.id.lay_bottom);
        emailCode = (EditText) find2.findViewById(R.id.emailCode);
        btnAgainGetCode = (Button) find2.findViewById(R.id.btAgainCode);
        txtSecond = (TextView) find2.findViewById(R.id.txt_second);
    }

    private void initReg3() {
        find3 = flater.inflate(R.layout.email_find3, null);
        setPassword = (EditText) find3.findViewById(R.id.setPassword);
        btnFinishRegister = (Button) find3.findViewById(R.id.btfinish_register);

    }

    private void setListener() {
        parent.setOnSizeChangedListenner(this);
        back.setOnClickListener(this);
        reg1EditEmail.addTextChangedListener(new PasswordEditTextWatcher(
                reg1EditEmail, this, true));
        emailCode.addTextChangedListener(new PasswordEditTextWatcher(emailCode,
                this, true));
        setPassword.addTextChangedListener(new PasswordEditTextWatcher(
                setPassword, this, true));
        reg1BtnNext.setOnClickListener(this);
        bthFinishYz.setOnClickListener(this);
        parent.setOnSizeChangedListenner(this);
        btnFinishRegister.setOnClickListener(this);
        btnAgainGetCode.setOnClickListener(this);
        findByCellphone.setOnClickListener(this);

    }

    @Override
    public void onSizeChange(boolean paramBoolean, int w, int h) {
        if (paramBoolean) {// 键盘弹出时
            parent.setPadding(0, -150, 0, 0);
            textView2.setVisibility(View.GONE);
            emailShow.setVisibility(View.GONE);
            layButtom.setVisibility(View.GONE);
            findByCellphone.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            textView2.setVisibility(View.VISIBLE);
            emailShow.setVisibility(View.VISIBLE);
            layButtom.setVisibility(View.VISIBLE);
            findByCellphone.setVisibility(View.VISIBLE);

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
                findPassword(strEmail);
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
                btnAgainGetCode
                        .setBackgroundResource(R.drawable.btn_tran51_hui);
                getAuthCode();
                break;
            case R.id.btnFindByCellphone:
                exit();
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
        map.put("type", "retrievePasswd");
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
                Utils.showToast("密码设置成功！", Toast.LENGTH_SHORT);
                CLXApplication.exit(false);
                startActivity(new Intent(this, LoginActivity.class));
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
        map.put("type", "retrievePasswd");
        map.put("email", emailShow.getText().toString());
        map.put("passwd", pswd);
        PostAsyncTask task = new PostAsyncTask(this, map, "/users/isetPasswd");
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
                rGroup.setView(find3);
                // Utils.popUp(this);
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
        map.put("type", "retrievePasswd");
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

    private void getFindPasswordResult(String resutl) {
        if (dialog != null) {
            dialog.dismiss();
        }
        try {
            JSONObject object = new JSONObject(resutl);
            int rt = object.getInt("rt");
            if (rt == 1) {
                mHandler.sendEmptyMessage(0);
                SharedUtils.setString("uid", object.getString("uid"));
                rGroup.addView(find2);
                emailShow.setText(reg1EditEmail.getText().toString());
                currentStep = 2;
                return;
            } else {
                String errString = object.getString("err");
                if (errString.equals("NOT_EXIST_USER")) {
                    Utils.showToast("这个号码还没注册哦！", Toast.LENGTH_SHORT);
                } else {
                    Utils.showToast("啊哦，手机号验证没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findPassword(String email) {
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("email", email);
        map.put("version", Utils.getVersionName(this));
        map.put("tag",
                MD5.MD5_32(StringUtils.reverseSort(email)
                        + Utils.getVersionName(this) + email
                        + Utils.getVersionName(this)));

        PostAsyncTask task = new PostAsyncTask(this, map,
                "/users/iretrievePassword");
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                getFindPasswordResult(result);
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
                rGroup.setView(find1);
                currentStep = 1;
                second = 60;
                mHandler.removeMessages(0);
                break;
            case 3:
                rGroup.setView(find1);
                currentStep = 2;
                break;

            default:
                break;
        }
    }
}
