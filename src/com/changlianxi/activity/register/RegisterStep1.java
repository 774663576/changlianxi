package com.changlianxi.activity.register;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changlianxi.NoticesActivity;
import com.changlianxi.R;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.MyEditTextWatcher;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.RegisterTask;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyCheckBox;
import com.changlianxi.view.MyCheckBox.CheckListener;

public class RegisterStep1 extends RegisterStep implements OnClickListener,
        OnTextLengthChange {

    private EditText editCellPhone;
    private EditText editEmail;
    private Button btnAgree;
    private Button btnNext;
    private Button registerBy;
    private MyCheckBox mCheckBox;
    private boolean registerByCellPhone = true;

    public RegisterStep1(RegisterActivity activity, View contentRootView) {
        super(activity, contentRootView);
    }

    @Override
    public void initView() {
        editCellPhone = (EditText) findViewById(R.id.edit_cellPhone);
        editEmail = (EditText) findViewById(R.id.edit_email);
        btnAgree = (Button) findViewById(R.id.btn_agree);
        btnNext = (Button) findViewById(R.id.btnNext);
        mCheckBox = (MyCheckBox) findViewById(R.id.checkBox);
        registerBy = (Button) findViewById(R.id.btn_registerBy);
    }

    @Override
    public void setListener() {
        editCellPhone.addTextChangedListener(new EditWather(editCellPhone,
                mContext));
        editCellPhone.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editCellPhone, mContext));
        editCellPhone.addTextChangedListener(new MyEditTextWatcher(
                editCellPhone, mContext, this));
        editEmail.addTextChangedListener(new MyEditTextWatcher(editEmail,
                mContext, this));
        editEmail.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editEmail, mContext));
        btnAgree.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        registerBy.setOnClickListener(this);
        mCheckBox.setmCheckListener(new CheckListener() {

            @Override
            public void onCheckedChanged(boolean isChecked) {
                if (isChecked) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.button_new);
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundResource(R.drawable.button_hui_new);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_agree:
                Intent intent = new Intent();
                intent.setClass(mContext, NoticesActivity.class);
                mContext.startActivity(intent);
                Utils.leftOutRightIn(mContext);
                break;
            case R.id.btnNext:
                Utils.getFocus(v);

                if (Utils.isFastDoubleClick()) {
                    return;
                }
                if (((System.currentTimeMillis() - mActivity
                        .getStartRequestTime()) / 1000) < 60
                        && mActivity.getRequestCount() > 3) {
                    Utils.showToast("操作频繁，请稍后再试", Toast.LENGTH_SHORT);
                    mActivity.startCountDown();
                    return;
                }
                if (mActivity.getStartRequestTime() == 0) {
                    mActivity.setStartRequestTime(System.currentTimeMillis());
                }
                mActivity.setRequestCount(mActivity.getRequestCount() + 1);
                if (!verify()) {
                    return;
                }
                register();
                break;
            case R.id.btn_registerBy:
                if (registerByCellPhone) {
                    registerByCellPhone = false;
                    editEmail.setVisibility(View.VISIBLE);
                    editCellPhone.setVisibility(View.GONE);
                    registerBy.setText("使用手机号码注册");
                    editEmail.setInputType(InputType.TYPE_CLASS_TEXT);
                    editCellPhone.setText("");
                    mRegister.setCellPhone("");

                } else {
                    registerByCellPhone = true;
                    registerBy.setText("使用电子邮箱注册");
                    editEmail.setVisibility(View.GONE);
                    editCellPhone.setVisibility(View.VISIBLE);
                    editEmail.setText("");
                    mRegister.setEmail("");
                }

                break;
            default:
                break;
        }
    }

    private boolean verify() {
        if (registerByCellPhone) {
            String cellPhone = editCellPhone.getText().toString()
                    .replace("-", "");
            if (cellPhone.length() == 0) {
                Utils.showToast("您貌似没有填手机号码。。", Toast.LENGTH_SHORT);
                return false;
            }
            if (!Utils.isPhoneNum(cellPhone)) {
                Utils.showToast("地球上貌似没有这种格式的手机号码:)", Toast.LENGTH_SHORT);
                return false;
            }
            mRegister.setCellPhone(cellPhone);
        } else {
            String email = editEmail.getText().toString();
            if (email.length() == 0) {
                Utils.showToast("您貌似没有填邮箱地址。。", Toast.LENGTH_SHORT);
                return false;
            }
            if (!Utils.isEmail(email)) {
                Utils.showToast("邮箱地址不正确", Toast.LENGTH_SHORT);
                return false;
            }
            mRegister.setEmail(email);
        }
        return true;
    }

    private void register() {
        showDialog("请稍候");
        RegisterTask reTask = new RegisterTask();
        reTask.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();
                if (result != RetError.NONE) {
                    return;
                }
                SharedUtils.setString("uid", mRegister.getUid());
                mOnNextListener.next();

            }

            @Override
            public void readDBFinish() {

            }
        });
        reTask.executeWithCheckNet(mRegister);
    }

    protected String getNum() {
        if (registerByCellPhone) {
            return editCellPhone.getText().toString();
        }
        return editEmail.getText().toString();

    }

    @Override
    public void onTextLengthChanged(boolean isBlank) {
        if (!isBlank) {
            if ((editCellPhone.getText().toString().length() != 0 || editEmail
                    .getText().toString().length() != 0)
                    && mCheckBox.isChecked) {
                btnNext.setEnabled(true);
                btnNext.setBackgroundResource(R.drawable.button_new);
                return;
            }
        }
        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.button_hui_new);
    }
}
