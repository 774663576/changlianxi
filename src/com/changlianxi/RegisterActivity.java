package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.text.InputType;
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
import com.changlianxi.inteface.PasswordEditTextWatcher;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.changlianxi.view.MyViewGroup;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册界面包含多个注册子view
 * 
 * @author teeker_bin
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
        PostCallBack, OnSizeChangedListenner {
    private MyViewGroup rGroup;
    private View reg1, reg2, reg3, reg4;// 注册1、2、3界面
    private LayoutInflater flater;
    private LayoutParams params;
    private Button btnext;// 注册界面的下一步按钮
    private Button btfinish_yz, btfinish_yz2;// 注册界面的完成验证按钮
    private Button btFinishRegister;// 注册界面的完成注册按钮
    private SearchEditText ediNum;// 注册界面的输入手机号edittext
    private SearchEditText ediPassword;// 注册界面的输入密码edittext
    private int rt;// 返回结果标志，1表示验证通过且生成新用户记录成功，其他值表示未成功TODO
    private String uid = "";// 成功后才有，代表用户ID
    private String token = "";
    private String messageString = "";// 验证短信的内容
    private EditText code, code2;// 注册界面的输入验证码edittext
    private Button btGetCode, btGetCode2;// 注册界面重新获取验证码按钮
    private Button btSendMessage;// 注册界面发送验证短信按钮
    private int second = 60;// 用于重新获取验证码时间倒计时
    private TextView txtShowNum, txtShowNum2;// 注册界面显示用来注册的手机号
    private Dialog progressDialog;
    private String type = "";// 1 验证码处理 2 设置密码处理3 重新获取验证码处理
    private TextView title;
    private TextView tv_second;
    private ImageView back;
    private String txtNum = ""; // 手机号码
    private String txtNumShow = ""; // 手机号码显示格式
    private InputMethodRelativeLayout parent;
    private LinearLayout layButtom;
    private TextView textView2;
    private TextView textView3;
    private int page = 0;
    private Button btnEmail;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_second.setText(second + "秒后您可以");
                    second--;
                    if (second < 0) {
                        rGroup.setView(reg2);
                        page++;
                        txtShowNum.setText(txtNumShow);
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
        setContentView(R.layout.activity_register);
        initView();

    }

    /**设置页面统计
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

    /**
     * 初始化控件
     */
    private void initView() {
        rGroup = (MyViewGroup) findViewById(R.id.regisGroup);
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        flater = LayoutInflater.from(this);
        reg1 = flater.inflate(R.layout.register1, null);
        reg2 = flater.inflate(R.layout.register2, null);
        reg3 = flater.inflate(R.layout.register3, null);
        reg4 = flater.inflate(R.layout.register4, null);
        rGroup.addView(reg1, params);
        initReg1View();
        initReg2View();
        initReg3View();
        initReg4View();
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("注册新用户");
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
    }

    /**
     * 初始化注册界面4的控件
     */
    private void initReg4View() {
        tv_second = (TextView) reg4.findViewById(R.id.tv_num);
        btfinish_yz2 = (Button) reg4.findViewById(R.id.btfinish_yz2);
        btfinish_yz2.setOnClickListener(this);
        btGetCode2 = (Button) reg4.findViewById(R.id.bt_get_code2);
        btGetCode2.setOnClickListener(this);
        code2 = (EditText) reg4.findViewById(R.id.editCode2);
        code2.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtShowNum2 = (TextView) reg4.findViewById(R.id.txt_show_num2);
        textView3 = (TextView) reg4.findViewById(R.id.textView2);
        code2.addTextChangedListener(new PasswordEditTextWatcher(code2, this,
                true));
    }

    /**
     * 初始化注册界面1的控件
     */
    private void initReg1View() {
        btnext = (Button) reg1.findViewById(R.id.next);
        btnext.setOnClickListener(this);
        ediNum = (SearchEditText) reg1.findViewById(R.id.num);
        ediNum.addTextChangedListener(new EditWather(ediNum, this));
        ediNum.setOnFocusChangeListener(new OnEditFocusChangeListener(ediNum,
                this));
        ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        ediNum.requestFocus();
        btnEmail = (Button) reg1.findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(this);
    }

    /**
     * 初始化注册界面2的控件
     */
    private void initReg2View() {
        btfinish_yz = (Button) reg2.findViewById(R.id.btfinish_yz);
        btfinish_yz.setOnClickListener(this);
        code = (EditText) reg2.findViewById(R.id.editCode);
        code.setInputType(InputType.TYPE_CLASS_NUMBER);
        btGetCode = (Button) reg2.findViewById(R.id.bt_get_code);
        btGetCode.setOnClickListener(this);
        btSendMessage = (Button) reg2.findViewById(R.id.bt_sendmessage);
        btSendMessage.setOnClickListener(this);
        txtShowNum = (TextView) reg2.findViewById(R.id.txt_show_num);
        layButtom = (LinearLayout) reg2.findViewById(R.id.lay_buttom);
        textView2 = (TextView) reg2.findViewById(R.id.textView2);
        code.addTextChangedListener(new PasswordEditTextWatcher(code, this,
                true));
    }

    /**
     * 初始化注册界面3的控件
     */
    private void initReg3View() {
        btFinishRegister = (Button) reg3.findViewById(R.id.btfinish_register);
        btFinishRegister.setOnClickListener(this);
        ediPassword = (SearchEditText) reg3.findViewById(R.id.editPassword);
        ediPassword.addTextChangedListener(new PasswordEditTextWatcher(
                ediPassword, this, true));
        ediPassword.setOnFocusChangeListener(new OnEditFocusChangeListener(
                ediPassword, this));

    }

    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        PostAsyncTask task = null;
        Map<String, Object> map = null;
        switch (v.getId()) {
            case R.id.back:
                if (page > 0) {
                    rGroup.setView(reg1);
                    second = 60;
                    mHandler.removeMessages(0);
                    page = 0;
                } else {
                    finish();
                    Utils.rightOut(this);
                }
                break;
            case R.id.next:
                ediNum.clearFocus();
                txtNum = ediNum.getText().toString().replace("-", "");
                txtNumShow = ediNum.getText().toString();
                if (ediNum.getText().toString().length() == 0) {
                    Utils.showToast("您貌似没有填手机号码。。", Toast.LENGTH_SHORT);
                    return;
                }
                if (!Utils.isPhoneNum(txtNum)) {
                    Utils.showToast("地球上貌似没有这种格式的手机号码:)", Toast.LENGTH_SHORT);
                    return;
                }
                new RegisterTask().execute(txtNum, "", "1");
                break;
            case R.id.btfinish_yz:
                if ("".equals(code.getText().toString())) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", code.getText().toString());
                map.put("type", "register");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                type = "1";
                break;
            case R.id.btfinish_yz2:
                if ("".equals(code2.getText().toString())) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", code2.getText().toString());
                map.put("type", "register");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                type = "1";
                break;
            case R.id.btfinish_register:
                ediPassword.clearFocus();
                String pswd = ediPassword.getText().toString();
                if (pswd.length() < 6) {
                    Utils.showToast("请设置至少6个字符的密码", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("version", Utils.getVersionName(RegisterActivity.this));
                map.put("device", Utils.getModelAndRelease());
                map.put("os", Utils.getOS());
                map.put("cellphone",
                        ediNum.getText().toString().replace("-", ""));
                map.put("passwd", pswd);
                task = new PostAsyncTask(this, map, "/users/icompleteRegister");
                task.setTaskCallBack(this);
                task.execute();
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                type = "2";
                break;
            case R.id.bt_get_code:
                second = 60;
                rGroup.setView(reg4);
                page++;
                mHandler.sendEmptyMessage(0);
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("type", "register");
                map.put("cellphone",
                        ediNum.getText().toString().replace("-", ""));
                task = new PostAsyncTask(this, map, "/users/isendAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                type = "3";
                break;
            case R.id.bt_sendmessage:
                messageString = "常联系#注册";
                SmsManager smsManager = SmsManager.getDefault();
                PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent(), 0);
                smsManager.sendTextMessage("12114", null, messageString,
                        pIntent, null);
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,
                        VerifyIntentActivity.class);
                intent.putExtra("rid", SharedUtils.getString("uid", ""));
                intent.putExtra("cellphone", txtNum);
                intent.putExtra("num", 2);
                startActivityForResult(intent, 1000);
                break;
            case R.id.btnEmail:
                Intent eit = new Intent();
                eit.setClass(RegisterActivity.this, EmailRegisterActivity.class);
                startActivity(eit);
                Utils.leftOutRightIn(this);
                break;
            default:
                break;
        }
    }

    /**
     * 注册
     * 
     */
    class RegisterTask extends AsyncTask<String, Integer, String> {
        String txtnum = "";
        String emailtxt;

        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected String doInBackground(String... params) {
            txtnum = params[0];
            emailtxt = params[1];
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cellphone", txtnum);
            map.put("email", emailtxt);
            map.put("version", Utils.getVersionName(RegisterActivity.this));
            map.put("tag",
                    MD5.MD5_32(StringUtils.reverseSort(txtnum)
                            + Utils.getVersionName(RegisterActivity.this)
                            + txtnum
                            + Utils.getVersionName(RegisterActivity.this)));
            String result = HttpUrlHelper.postData(map, "/users/iregister");
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            try {
                JSONObject object = new JSONObject(result);
                rt = object.getInt("rt");
                if (rt == 1) {
                    mHandler.sendEmptyMessage(0);
                    uid = object.getString("uid");
                    SharedUtils.setString("uid", uid);
                    rGroup.setView(reg4);
                    page++;
                    txtShowNum2.setText(txtNumShow);
                    return;
                } else {
                    String errorCoce = object.getString("err");
                    if (errorCoce.equals("USER_ALREADY_EXIST")) {
                        Utils.showToast("这个号码已经注册啦！如忘记密码可以用找回密码功能哦。",
                                Toast.LENGTH_SHORT);
                    } else {
                        Utils.showToast("啊哦，操作没有成功，请查看下您的网络是否正常！",
                                Toast.LENGTH_SHORT);
                    }
                }
            } catch (JSONException e) {
                Logger.error(this, e);

                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理
            progressDialog = DialogUtil.getWaitDialog(RegisterActivity.this,
                    "请稍候");
            progressDialog.show();
        }
    }

    /**
     * 判断验证码是否正确
     * 
     * @param result
     */
    private void CheckCode(String result) {
        try {
            JSONObject object = new JSONObject(result);
            rt = object.getInt("rt");
            if (rt == 1) {
                rGroup.setView(reg3);
                page++;
                // Utils.popUp(this);
                mHandler.removeMessages(0);
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
     * 判断密码是否设置成功
     * 
     * @param result
     */
    private void SetPassword(String result) {
        try {
            JSONObject object = new JSONObject(result);
            rt = object.getInt("rt");
            if (rt == 1) {
                token = object.getString("token");
                uid = object.getString("uid");
                SharedUtils.setString("token", token);
                SharedUtils.setString("uid", uid);
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,
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

    /**
     * 重新获取验证码
     * 
     * @param result
     */
    private void getAgainCode(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt != 1) {
                Utils.showToast("啊哦，验证码获取没有成功，请查看下您的网络是否正常！",
                        Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void taskFinish(String result) {
        progressDialog.dismiss();
        if (type.equals("1")) {
            CheckCode(result);
        } else if (type.equals("2")) {
            SetPassword(result);
        } else if (type.equals("3")) {
            getAgainCode(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                rGroup.setView(reg3);
                page++;
            }
        }
    }

    @Override
    public void onSizeChange(boolean paramBoolean, int w, int h) {
        if (paramBoolean) {// 键盘弹出时
            parent.setPadding(0, -150, 0, 0);
            layButtom.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.GONE);
            txtShowNum.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            txtShowNum2.setVisibility(View.GONE);
            btnEmail.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            layButtom.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            txtShowNum.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            txtShowNum2.setVisibility(View.VISIBLE);
            btnEmail.setVisibility(View.VISIBLE);
        }
    }
}
