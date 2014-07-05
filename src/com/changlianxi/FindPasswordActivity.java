package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.text.InputType;
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
import com.changlianxi.util.EditWather;
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
 * 找回密码界面包含多个子view
 * 
 * @author teeker_bin
 * 
 */
@SuppressWarnings("deprecation")
public class FindPasswordActivity extends BaseActivity implements
        OnClickListener, PostCallBack, OnSizeChangedListenner {
    private MyViewGroup group;
    private LayoutInflater flater;
    private View find1, find2, find3, find4;// 找回密码的三个界面
    private Button btnext;// 界面1的下一步按钮
    private Button btfinishYz, btfinishYz2;// 界面2的完成验证按钮
    private Button btfinish;// 界面3的完成按钮
    private LayoutParams params;
    private ImageView btback;
    private SearchEditText ediNum;// 手机号输入框
    private EditText ediCode, ediCode2;// 验证码输入框
    private SearchEditText ediPasswd;// 密码输入框
    private String uid;
    private String type = "";// 1 找回密码回调接口处理 2 验证码接口回调处理 3 设置密码接口回调处理4
                             // 重新发送验证码回调处理
    private Dialog pd;
    private Button btGetCode;// 注册界面重新获取验证码按钮
    private Button btSendMessage;// 发送验证短信按钮
    private TextView showNum, showNum2; // 用于显示手机号码
    private TextView txtPrompt1, txtPrompt2;
    private int second = 60;// 用于重新获取验证码时间倒计时
    private String phoneNum = ""; // 手机号码
    private String txtNumShow = ""; // 手机号码显示格式
    private String messageString = "";// 验证短信的内容
    private TextView title;
    private TextView tv_second;// 界面4倒计时
    private LinearLayout layButtom;
    private InputMethodRelativeLayout parent;
    private int page = 0;
    private Button findByEmail;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_second.setText(second + "秒后您可以");
                    second--;
                    if (second < 0) {
                        group.setView(find2);
                        page++;
                        showNum.setText(txtNumShow);
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
        setContentView(R.layout.activity_find_password);
        group = (MyViewGroup) findViewById(R.id.myGroup);
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
        flater = LayoutInflater.from(this);
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        find1 = flater.inflate(R.layout.find_word1, null);
        find4 = flater.inflate(R.layout.find_word4, null);
        find2 = flater.inflate(R.layout.find_word2, null);
        find3 = flater.inflate(R.layout.find_word3, null);
        group.addView(find1, params);
        btback = (ImageView) findViewById(R.id.back);
        btback.setOnClickListener(this);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("找回密码");
        initFind1View();
        initFind2View();
        initFind3View();
        initFind4View();
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
     * 初始化找回密码界面1的控件
     */
    private void initFind1View() {
        btnext = (Button) find1.findViewById(R.id.btnext);
        btnext.setOnClickListener(this);
        ediNum = (SearchEditText) find1.findViewById(R.id.editnum);
        ediNum.addTextChangedListener(new EditWather(ediNum, this));
        ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        findByEmail = (Button) find1.findViewById(R.id.btnFindByEmail);
        findByEmail.setOnClickListener(this);

    }

    /**
     * 初始化找回密码界面2的控件
     */
    private void initFind4View() {
        tv_second = (TextView) find4.findViewById(R.id.tv_num);
        btfinishYz2 = (Button) find4.findViewById(R.id.btfinish_yz2);
        btfinishYz2.setOnClickListener(this);
        ediCode2 = (EditText) find4.findViewById(R.id.editCode2);
        showNum2 = (TextView) find4.findViewById(R.id.txt_show_num2);
        txtPrompt2 = (TextView) find4.findViewById(R.id.textrompt2);
        ediCode2.setInputType(InputType.TYPE_CLASS_NUMBER);
        ediCode2.addTextChangedListener(new PasswordEditTextWatcher(ediCode2,
                this, true));
    }

    /**
     * 初始化找回密码界面2的控件
     */
    private void initFind2View() {
        btfinishYz = (Button) find2.findViewById(R.id.btfinish_yz);
        btfinishYz.setOnClickListener(this);
        btSendMessage = (Button) find2.findViewById(R.id.bt_sendmessage);
        btSendMessage.setOnClickListener(this);
        showNum = (TextView) find2.findViewById(R.id.txt_show_num);
        txtPrompt1 = (TextView) find2.findViewById(R.id.textrompt1);
        ediCode = (EditText) find2.findViewById(R.id.editCode);
        ediCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        btGetCode = (Button) find2.findViewById(R.id.bt_get_code);
        btGetCode.setOnClickListener(this);
        layButtom = (LinearLayout) find2.findViewById(R.id.layBottom);
        ediCode.addTextChangedListener(new PasswordEditTextWatcher(ediCode,
                this, true));
    }

    /**
     * 初始化找回密码界面3的控件
     */
    private void initFind3View() {
        btfinish = (Button) find3.findViewById(R.id.btfinish);
        btfinish.setOnClickListener(this);
        ediPasswd = (SearchEditText) find3.findViewById(R.id.editPassword);
        ediPasswd.addTextChangedListener(new PasswordEditTextWatcher(ediPasswd,
                this, true));
    }

    @Override
    public void onClick(View v) {
        PostAsyncTask task = null;
        Map<String, Object> map = null;
        switch (v.getId()) {

            case R.id.btnext:
                ediNum.clearFocus();
                // 找回密码之验证手机号是否存在
                map = new HashMap<String, Object>();
                phoneNum = ediNum.getText().toString().replace("-", "");
                if (!Utils.isPhoneNum(phoneNum)) {
                    Utils.showToast("地球上貌似没有这种格式的手机号码:p", Toast.LENGTH_SHORT);
                    return;
                }
                txtNumShow = ediNum.getText().toString();
                map.put("cellphone", phoneNum);
                map.put("version",
                        Utils.getVersionName(FindPasswordActivity.this));
                map.put("tag", MD5.MD5_32(StringUtils.reverseSort(phoneNum)
                        + Utils.getVersionName(FindPasswordActivity.this)
                        + phoneNum
                        + Utils.getVersionName(FindPasswordActivity.this)));

                task = new PostAsyncTask(this, map, "/users/iretrievePassword");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "请稍候");
                pd.show();
                type = "1";
                break;

            case R.id.btfinish_yz:
                if ("".equals(ediCode.getText().toString())) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", ediCode.getText().toString());
                map.put("type", "retrievePasswd");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "请稍候");
                pd.show();
                type = "2";
                break;
            case R.id.btfinish_yz2:
                if ("".equals(ediCode2.getText().toString())) {
                    Utils.showToast("请输入验证码！", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", ediCode2.getText().toString());
                map.put("type", "retrievePasswd");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "请稍候");
                pd.show();
                type = "2";
                break;
            case R.id.back:
                if (page > 0) {
                    group.setView(find1);
                    second = 60;
                    mHandler.removeMessages(0);
                    page = 0;
                } else {
                    finish();
                    Utils.rightOut(this);
                }
                break;
            case R.id.btfinish:
                ediPasswd.clearFocus();
                String pswd = ediPasswd.getText().toString();
                if (pswd.length() < 6) {
                    Utils.showToast("密码至少6个字符，出门在外，安全第一:)", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("type", "retrievePasswd");
                map.put("cellphone",
                        ediNum.getText().toString().replace("-", ""));
                map.put("passwd", pswd);
                task = new PostAsyncTask(this, map, "/users/isetPasswd");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "请稍候");
                pd.show();
                type = "3";
                break;
            case R.id.bt_get_code:
                second = 60;
                group.setView(find4);
                page++;
                mHandler.sendEmptyMessage(0);
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("type", "retrievePasswd");
                map.put("cellphone",
                        ediNum.getText().toString().replace("-", ""));
                task = new PostAsyncTask(this, map, "/users/isendAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "请稍候");
                pd.show();
                type = "4";
                break;
            case R.id.bt_sendmessage:
                messageString = "常联系#找回密码";
                SmsManager smsManager = SmsManager.getDefault();
                PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent(), 0);
                smsManager.sendTextMessage("12114", null, messageString,
                        pIntent, null);
                Intent intent = new Intent();
                intent.setClass(FindPasswordActivity.this,
                        VerifyIntentActivity.class);
                intent.putExtra("rid", SharedUtils.getString("uid", ""));
                intent.putExtra("cellphone", phoneNum);
                intent.putExtra("num", 3);
                startActivityForResult(intent, 2000);
                break;
            case R.id.btnFindByEmail:
                startActivity(new Intent(this, EmailFindPasswordActivity.class));
                Utils.leftOutRightIn(this);
                // finish();
                break;
            default:
                break;
        }
    }

    private void FindPassword(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendEmptyMessage(0);
                uid = object.getString("uid");
                SharedUtils.setString("uid", uid);
                group.setView(find4);
                page++;
                showNum2.setText(txtNumShow);
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

    /**
     * 判断验证码是否正确
     * 
     * @param result
     */
    private void CheckCode(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                group.setView(find3);
                page++;
                ediPasswd.requestFocus();
                // 弹出软键盘
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
            int rt = object.getInt("rt");
            if (rt == 1) {
                Utils.showToast("密码设置成功！", Toast.LENGTH_SHORT);
                CLXApplication.exit(false);
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Utils.showToast("啊哦，密码设置没有成功，请查看下您的网络是否正常！", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            Logger.error(this, e);

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

    /**
     * 接口回调函数
     */
    @Override
    public void taskFinish(String result) {
        pd.dismiss();
        if (type.equals("1")) {
            FindPassword(result);
        } else if (type.equals("2")) {
            CheckCode(result);
        } else if (type.equals("3")) {
            SetPassword(result);
        } else if (type.equals("4")) {
            getAgainCode(result);
        }

    }

    /**
     * 在Activity中实现OnSizeChangedListener，原理是设置该布局的paddingTop属性来控制子View的偏移
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        if (flag) {// 键盘弹出时
            parent.setPadding(0, -100, 0, 0);
            layButtom.setVisibility(View.GONE);
            txtPrompt1.setVisibility(View.GONE);
            txtPrompt2.setVisibility(View.GONE);
            showNum.setVisibility(View.GONE);
            showNum2.setVisibility(View.GONE);
            findByEmail.setVisibility(View.GONE);

        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            layButtom.setVisibility(View.VISIBLE);
            txtPrompt1.setVisibility(View.VISIBLE);
            txtPrompt2.setVisibility(View.VISIBLE);
            showNum.setVisibility(View.VISIBLE);
            showNum2.setVisibility(View.VISIBLE);
            findByEmail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (page > 0) {
                group.setView(find1);
                second = 60;
                mHandler.removeMessages(0);
                page = 0;
            } else {
                finish();
                Utils.rightOut(this);
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (resultCode == RESULT_OK) {
                group.setView(find3);
                page++;
            }
        }
    }
}
