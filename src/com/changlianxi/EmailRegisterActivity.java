package com.changlianxi;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.MyViewGroup;
import com.changlianxi.view.SearchEditText;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;

public class EmailRegisterActivity extends BaseActivity implements
        OnClickListener, OnSizeChangedListenner, PostCallBack {
    private MyViewGroup rGroup;
    private View reg1, reg2, reg3;
    private LayoutParams params;
    private LayoutInflater flater;
    private Button reg1BtnNext;
    private EditText reg1EditEmail;
    private InputMethodRelativeLayout parent;
    private TextView title;
    private ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);
        initView();
    }

    private void initView() {
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
        reg1BtnNext.setOnClickListener(this);
        reg1EditEmail = (SearchEditText) reg1.findViewById(R.id.editEmail);
        reg1EditEmail.requestFocus();

    }

    private void initReg2() {
        reg2 = flater.inflate(R.layout.emailregister2, null);

    }

    private void initReg3() {
        reg3 = flater.inflate(R.layout.emailregister3, null);

    }

    private void setListener() {
        parent.setOnSizeChangedListenner(this);
        back.setOnClickListener(this);
        reg1EditEmail.setOnFocusChangeListener(new OnEditFocusChangeListener(
                reg1EditEmail, this));

    }

    @Override
    public void onSizeChange(boolean paramBoolean, int w, int h) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            default:
                break;
        }
    }

    @Override
    public void taskFinish(String result) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }
}
