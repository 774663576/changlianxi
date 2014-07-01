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
 * ��¼����
 * 
 * @author teeker_bin
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
        PostCallBack, OnSizeChangedListenner {
    private Button btReg;// ȥ��ע����水ť
    private Button btLogin;// ��¼��ť
    private SearchEditText ediNum;// �ֻ����������
    private SearchEditText ediPassword;// ���������
    private String uid = "";// �ɹ�����У������û�ID
    private String token = "";
    private Button btFindWd;// �һ����밴ť
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
     * ����ͳ��
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
     * ��ʼ���ؼ�
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
     * ��¼ʱ�ж��û����Ƿ����
     * 
     * @param str
     * @return
     */
    private boolean isUserExist(String str) {
        try {
            JSONObject object = new JSONObject(str);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setInt("loginType", 2);// ��¼��ʽ��� 1 ע���¼ 2 ������¼
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
                        Utils.showToast("�����ַ����������", Toast.LENGTH_SHORT);

                    } else {
                        Utils.showToast("�ֻ��Ż���������", Toast.LENGTH_SHORT);

                    }
                } else {
                    Utils.showToast("��Ŷ����½û�гɹ�����鿴�����������Ƿ�������",
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
     * �ؼ��ĵ���¼�
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
                    Utils.showToast("���ߣ����粻ͨ�������°ɣ�", Toast.LENGTH_SHORT);
                    return;
                }
                String num = ediNum.getText().toString().trim();
                String pass = ediPassword.getText().toString().trim();
                if (num.length() == 0 || pass.length() == 0) {
                    Utils.showToast("�������붼���룬���ܵ�¼����ϵ:)", Toast.LENGTH_SHORT);
                    return;
                }
                if (num.contains("@")) {
                    if (!Utils.isEmail(num)) {
                        Utils.showToast("�����ʽ����ȷ", Toast.LENGTH_SHORT);
                        ediNum.setFocusable(true);
                        return;
                    }
                } else {
                    if (!Utils.isPhoneNum(num)) {
                        Utils.showToast("������ò��û�����ָ�ʽ���ֻ�����:)",
                                Toast.LENGTH_SHORT);
                        ediNum.setFocusable(true);
                        return;
                    }
                    if (ediPassword.getText().toString().length() == 0) {
                        Utils.showToast("�������붼���룬���ܵ�¼����ϵ:)", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                Utils.hideSoftInput(this);
                dialog = DialogUtil.getWaitDialog(this, "��¼��");
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
     * ��¼
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
     * ��¼�ӿڴ���ص�
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
     * ��Activity��ʵ��OnSizeChangedListener��ԭ�������øò��ֵ�paddingTop������������View��ƫ��
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        if (flag) {// ���̵���ʱ
            parent.setPadding(0, -150, 0, 0);
            // layButtom.setVisibility(View.GONE);
            buttonTxt.setVisibility(View.GONE);

        } else { // ��������ʱ
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
