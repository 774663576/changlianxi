package com.changlianxi.activity.findpassword;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changlianxi.LoginActivity;
import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.MyEditTextWatcher;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.FindSetPasswordTask;
import com.changlianxi.util.Utils;

public class FindPasswordStep3 extends FindPasswordStep implements
        OnClickListener, OnTextLengthChange {
    private EditText editPassword;
    private EditText editPassword1;
    private Button btnNext;

    public FindPasswordStep3(FindPasswordActivity activity, View contentRootView) {
        super(activity, contentRootView);
    }

    @Override
    public void initView() {
        editPassword = (EditText) findViewById(R.id.edit_password);
        editPassword1 = (EditText) findViewById(R.id.edit_password1);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    @Override
    public void setListener() {
        editPassword1.addTextChangedListener(new MyEditTextWatcher(
                editPassword1, mContext, this));
        editPassword1.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editPassword1, mContext));
        editPassword.addTextChangedListener(new MyEditTextWatcher(editPassword,
                mContext, this));
        editPassword.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editPassword, mContext));
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                Utils.getFocus(v);
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                setPassword();
                break;
            default:
                break;
        }
    }

    private void setPassword() {
        String pswd = editPassword.getText().toString().replace(" ", "");
        if (pswd.length() < 6 || pswd.length() > 16) {
            Utils.showToast("请设置6-16个字符之间的密码", Toast.LENGTH_SHORT);
            return;
        }
        String pawd1 = editPassword1.getText().toString();
        if (!pawd1.equals(pswd)) {
            Utils.showToast("两次输入的密码不一致", Toast.LENGTH_SHORT);
            return;
        }
        showDialog("请稍候");
        FindSetPasswordTask task = new FindSetPasswordTask(pswd);
        task.isPrompt = false;
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();
                if (result != RetError.NONE) {
                    Utils.showToast("啊哦，密码设置没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);

                    return;
                }
                Utils.showToast("密码设置成功！", Toast.LENGTH_SHORT);
                CLXApplication.exit(false);
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(mFindPswd);
    }

    @Override
    public void onTextLengthChanged(boolean isBlank) {
        if (!isBlank) {
            if (editPassword.getText().toString().length() != 0
                    && editPassword1.getText().toString().length() != 0) {
                btnNext.setEnabled(true);
                btnNext.setBackgroundResource(R.drawable.button_new);
                return;
            }
        }
        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.button_hui_new);
    }
}
