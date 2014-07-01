package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.PasswordEditTextWatcher;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录界面
 * 
 * @author teeker_bin
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
        PostCallBack, OnSizeChangedListenner {
    private Button btReg;// 去往注册界面按钮
    private Button btLogin;// 登录按钮
    private SearchEditText ediNum;// 手机号码输入框
    private SearchEditText ediPassword;// 密码输入框
    private String uid = "";// 成功后才有，代表用户ID
    private String token = "";
    private Button btFindWd;// 找回密码按钮
    private Dialog dialog;
    private InputMethodRelativeLayout parent;
    private TextView buttonTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ShareSDK.initSDK(this);
        MobclickAgent.openActivityDurationTrack(false);
        initView();
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

    /**
     * 初始化控件
     */
    private void initView() {
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
        btFindWd = (Button) findViewById(R.id.findpd);
        btFindWd.setOnClickListener(this);
        btReg = (Button) findViewById(R.id.btregister);
        btReg.setOnClickListener(this);
        btLogin = (Button) findViewById(R.id.btlogin);
        btLogin.setOnClickListener(this);
        ediNum = (SearchEditText) findViewById(R.id.edtNum);
        // ediNum.addTextChangedListener(new EditWather(ediNum, this));
        ediNum.setOnFocusChangeListener(new OnEditFocusChangeListener(ediNum,
                this));
        ediPassword = (SearchEditText) findViewById(R.id.edtPassword);
        ediPassword.addTextChangedListener(new PasswordEditTextWatcher(
                ediPassword, this, true));
        buttonTxt = (TextView) findViewById(R.id.buttomTxt);
    }

    /**
     * 登录时判断用户名是否存在
     * 
     * @param str
     * @return
     */
    private boolean isUserExist(String str) {
        try {
            JSONObject object = new JSONObject(str);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setInt("loginType", 2);// 登录方式标记 1 注册登录 2 正常登录
                token = object.getString("token");
                uid = object.getString("uid");
                SharedUtils.setString("uid", uid);
                SharedUtils.setString("token", token);
                Intent it = new Intent();
                it.setClass(LoginActivity.this, MainActivity.class);
                startActivity(it);
                finish();
                return true;
            } else {
                dialog.dismiss();
                String errorCoce = object.getString("err");
                if (errorCoce.equals("NOT_EXIST_USER")
                        || errorCoce.equals("WRONG_PASSWORD")) {
                    if (ediNum.getText().toString().contains("@")) {
                        Utils.showToast("邮箱地址或密码有误！", Toast.LENGTH_SHORT);

                    } else {
                        Utils.showToast("手机号或密码有误！", Toast.LENGTH_SHORT);

                    }
                } else {
                    Utils.showToast("啊哦，登陆没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 控件的点击事件
     */
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.btregister:
                intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btlogin:
                ediNum.clearFocus();
                ediPassword.clearFocus();
                if (!Utils.isNetworkAvailable()) {
                    Utils.showToast("杯具，网络不通，快检查下吧！", Toast.LENGTH_SHORT);
                    return;
                }
                String num = ediNum.getText().toString().trim();
                String pass = ediPassword.getText().toString().trim();
                if (num.length() == 0 || pass.length() == 0) {
                    Utils.showToast("号码密码都输入，才能登录常联系:)", Toast.LENGTH_SHORT);
                    return;
                }
                if (num.contains("@")) {
                    if (!Utils.isEmail(num)) {
                        Utils.showToast("邮箱格式不正确", Toast.LENGTH_SHORT);
                        ediNum.setFocusable(true);
                        return;
                    }
                } else {
                    if (!Utils.isPhoneNum(num)) {
                        Utils.showToast("地球上貌似没有这种格式的手机号码:)",
                                Toast.LENGTH_SHORT);
                        ediNum.setFocusable(true);
                        return;
                    }
                    if (ediPassword.getText().toString().length() == 0) {
                        Utils.showToast("号码密码都输入，才能登录常联系:)", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                Utils.hideSoftInput(this);
                dialog = DialogUtil.getWaitDialog(this, "登录中");
                dialog.show();
                login(num);
                break;
            case R.id.findpd:
                intent = new Intent();
                intent.setClass(this, FindPasswordActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            // case R.id.qita:
            // intent = new Intent();
            // intent.setClass(this, ThreeLoginActivity.class);
            // startActivity(intent);
            // Utils.leftOutRightIn(this);
            default:
                break;
        }

    }

    /**
     * 登录
     */
    private void login(String num) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (num.contains("@")) {
            map.put("email", num);
        } else {
            map.put("cellphone", num);
        }
        map.put("passwd", ediPassword.getText().toString());
        map.put("device", Utils.getModelAndRelease());
        map.put("version", Utils.getVersionName(this));
        map.put("os", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, "/users/ilogin2");
        task.setTaskCallBack(this);
        task.execute();
    }

    /**
     * 登录接口处理回调
     */
    @Override
    public void taskFinish(String result) {
        isUserExist(result);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 在Activity中实现OnSizeChangedListener，原理是设置该布局的paddingTop属性来控制子View的偏移
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        if (flag) {// 键盘弹出时
            parent.setPadding(0, -150, 0, 0);
            // layButtom.setVisibility(View.GONE);
            buttonTxt.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            // layButtom.setVisibility(View.VISIBLE);
            buttonTxt.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

}
