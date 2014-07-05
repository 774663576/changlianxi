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
 * �һ����������������view
 * 
 * @author teeker_bin
 * 
 */
@SuppressWarnings("deprecation")
public class FindPasswordActivity extends BaseActivity implements
        OnClickListener, PostCallBack, OnSizeChangedListenner {
    private MyViewGroup group;
    private LayoutInflater flater;
    private View find1, find2, find3, find4;// �һ��������������
    private Button btnext;// ����1����һ����ť
    private Button btfinishYz, btfinishYz2;// ����2�������֤��ť
    private Button btfinish;// ����3����ɰ�ť
    private LayoutParams params;
    private ImageView btback;
    private SearchEditText ediNum;// �ֻ��������
    private EditText ediCode, ediCode2;// ��֤�������
    private SearchEditText ediPasswd;// ���������
    private String uid;
    private String type = "";// 1 �һ�����ص��ӿڴ��� 2 ��֤��ӿڻص����� 3 ��������ӿڻص�����4
                             // ���·�����֤��ص�����
    private Dialog pd;
    private Button btGetCode;// ע��������»�ȡ��֤�밴ť
    private Button btSendMessage;// ������֤���Ű�ť
    private TextView showNum, showNum2; // ������ʾ�ֻ�����
    private TextView txtPrompt1, txtPrompt2;
    private int second = 60;// �������»�ȡ��֤��ʱ�䵹��ʱ
    private String phoneNum = ""; // �ֻ�����
    private String txtNumShow = ""; // �ֻ�������ʾ��ʽ
    private String messageString = "";// ��֤���ŵ�����
    private TextView title;
    private TextView tv_second;// ����4����ʱ
    private LinearLayout layButtom;
    private InputMethodRelativeLayout parent;
    private int page = 0;
    private Button findByEmail;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_second.setText(second + "���������");
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
        title.setText("�һ�����");
        initFind1View();
        initFind2View();
        initFind3View();
        initFind4View();
    }

    /**����ҳ��ͳ��
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
     * ��ʼ���һ��������1�Ŀؼ�
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
     * ��ʼ���һ��������2�Ŀؼ�
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
     * ��ʼ���һ��������2�Ŀؼ�
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
     * ��ʼ���һ��������3�Ŀؼ�
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
                // �һ�����֮��֤�ֻ����Ƿ����
                map = new HashMap<String, Object>();
                phoneNum = ediNum.getText().toString().replace("-", "");
                if (!Utils.isPhoneNum(phoneNum)) {
                    Utils.showToast("������ò��û�����ָ�ʽ���ֻ�����:p", Toast.LENGTH_SHORT);
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
                pd = DialogUtil.getWaitDialog(this, "���Ժ�");
                pd.show();
                type = "1";
                break;

            case R.id.btfinish_yz:
                if ("".equals(ediCode.getText().toString())) {
                    Utils.showToast("��������֤�룡", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", ediCode.getText().toString());
                map.put("type", "retrievePasswd");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "���Ժ�");
                pd.show();
                type = "2";
                break;
            case R.id.btfinish_yz2:
                if ("".equals(ediCode2.getText().toString())) {
                    Utils.showToast("��������֤�룡", Toast.LENGTH_SHORT);
                    return;
                }
                map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("auth_code", ediCode2.getText().toString());
                map.put("type", "retrievePasswd");
                task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
                task.setTaskCallBack(this);
                task.execute();
                pd = DialogUtil.getWaitDialog(this, "���Ժ�");
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
                    Utils.showToast("��������6���ַ����������⣬��ȫ��һ:)", Toast.LENGTH_SHORT);
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
                pd = DialogUtil.getWaitDialog(this, "���Ժ�");
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
                pd = DialogUtil.getWaitDialog(this, "���Ժ�");
                pd.show();
                type = "4";
                break;
            case R.id.bt_sendmessage:
                messageString = "����ϵ#�һ�����";
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
                    Utils.showToast("������뻹ûע��Ŷ��", Toast.LENGTH_SHORT);
                } else {
                    Utils.showToast("��Ŷ���ֻ�����֤û�гɹ�����鿴�����������Ƿ�������",
                            Toast.LENGTH_SHORT);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * �ж���֤���Ƿ���ȷ
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
                // ���������
                // Utils.popUp(this);
                mHandler.removeMessages(0);
            } else {
                Utils.showToast("��Ŷ����֤�벻��\n��֤��Ϊ6�����֣�����ȷ������һ��",
                        Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            Logger.error(this, e);

            e.printStackTrace();
        }
    }

    /**
     * �ж������Ƿ����óɹ�
     * 
     * @param result
     */
    private void SetPassword(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                Utils.showToast("�������óɹ���", Toast.LENGTH_SHORT);
                CLXApplication.exit(false);
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Utils.showToast("��Ŷ����������û�гɹ�����鿴�����������Ƿ�������", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            Logger.error(this, e);

            e.printStackTrace();
        }
    }

    /**
     * ���»�ȡ��֤��
     * 
     * @param result
     */
    private void getAgainCode(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt != 1) {
                Utils.showToast("��Ŷ����֤���ȡû�гɹ�����鿴�����������Ƿ�������",
                        Toast.LENGTH_SHORT);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * �ӿڻص�����
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
     * ��Activity��ʵ��OnSizeChangedListener��ԭ�������øò��ֵ�paddingTop������������View��ƫ��
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        if (flag) {// ���̵���ʱ
            parent.setPadding(0, -100, 0, 0);
            layButtom.setVisibility(View.GONE);
            txtPrompt1.setVisibility(View.GONE);
            txtPrompt2.setVisibility(View.GONE);
            showNum.setVisibility(View.GONE);
            showNum2.setVisibility(View.GONE);
            findByEmail.setVisibility(View.GONE);

        } else { // ��������ʱ
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
