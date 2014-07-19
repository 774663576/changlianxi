package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.changlianxi.activity.findpassword.FindPasswordActivity;
import com.changlianxi.activity.register.RegisterActivity;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.MyEditTextWatcher;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements OnClickListener,
        OnTextLengthChange {
    private ImageView back;
    private TextView title;
    private EditText editUserName;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnFindPswd;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity1);
        initView();
        ShareSDK.initSDK(this);
        MobclickAgent.openActivityDurationTrack(false);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.txt_title);
        title.setText("登录");
        editPassword = (EditText) findViewById(R.id.edit_password);
        editUserName = (EditText) findViewById(R.id.edit_userName);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnFindPswd = (Button) findViewById(R.id.btn_findPasswrod);
        setListener();
        editUserName.setText(SharedUtils.getString("userName", ""));
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnFindPswd.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        editUserName.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editUserName, this));
        editUserName.addTextChangedListener(new MyEditTextWatcher(editUserName,
                this, this));
        editPassword.addTextChangedListener(new MyEditTextWatcher(editPassword,
                this, this));
        editPassword.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editPassword, this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_login:
                Utils.getFocus(v);
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                login();
                break;
            case R.id.btn_findPasswrod:
                startActivity(new Intent(this, FindPasswordActivity.class));
                Utils.leftOutRightIn(this);
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                Utils.leftOutRightIn(this);
                break;
            default:
                break;
        }
    }

    private void login() {
        if (!Utils.isNetworkAvailable()) {
            Utils.showToast("杯具，网络不通，快检查下吧！", Toast.LENGTH_SHORT);
            return;
        }
        String userName = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        if (userName.length() == 0 || password.length() == 0) {
            Utils.showToast("号码密码都输入，才能登录常联系:)", Toast.LENGTH_SHORT);
            return;
        }
        if (userName.contains("@")) {
            if (!Utils.isEmail(userName)) {
                Utils.showToast("邮箱格式不正确", Toast.LENGTH_SHORT);
                return;
            }
        } else {
            if (!Utils.isPhoneNum(userName)) {
                Utils.showToast("地球上貌似没有这种格式的手机号码:)", Toast.LENGTH_SHORT);
                return;
            }
        }
        loginTask(userName, password);
    }

    /**
    * 登录
    */
    private void loginTask(String unserName, String password) {
        dialog = DialogUtil.getWaitDialog(this, "登录中");
        dialog.show();
        Map<String, Object> map = new HashMap<String, Object>();
        if (unserName.contains("@")) {
            map.put("email", unserName);
        } else {
            map.put("cellphone", unserName);
        }
        map.put("passwd", password);
        map.put("device", Utils.getModelAndRelease());
        map.put("version", Utils.getVersionName(this));
        map.put("os", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, "/users/ilogin2");
        task.setTaskCallBack(new PostCallBack() {
            @Override
            public void taskFinish(String result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                isUserExist(result);
            }
        });
        task.execute();
    }

    /**
     * 登录时判断用户名是否存在
     * 
     * @param str
     * @return
     */
    private void isUserExist(String str) {
        try {
            JSONObject object = new JSONObject(str);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setString("userName", editUserName.getText()
                        .toString());
                SharedUtils.setInt("loginType", 2);// 登录方式标记 1 注册登录 2 正常登录
                SharedUtils.setString("uid", object.getString("uid"));
                SharedUtils.setString("token", object.getString("token"));
                startActivity(new Intent(this, CircleHomeActivity.class));
                finish();
            } else {
                String errorCoce = object.getString("err");
                if (errorCoce.equals("NOT_EXIST_USER")
                        || errorCoce.equals("WRONG_PASSWORD")) {
                    if (editUserName.getText().toString().contains("@")) {
                        Utils.showToast("邮箱地址或密码有误！", Toast.LENGTH_SHORT);
                    } else {
                        Utils.showToast("手机号或密码有误！", Toast.LENGTH_SHORT);
                    }
                } else {
                    Utils.showToast("啊哦，登陆没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTextLengthChanged(boolean isBlank) {
        if (!isBlank) {
            if (editPassword.getText().toString().length() != 0
                    && editUserName.getText().toString().length() != 0) {
                btnLogin.setEnabled(true);
                btnLogin.setBackgroundResource(R.drawable.button_new);
                return;
            }
        }
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundResource(R.drawable.button_hui_new);
    }

}
