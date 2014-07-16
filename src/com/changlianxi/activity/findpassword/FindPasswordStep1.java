package com.changlianxi.activity.findpassword;

import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.MyEditTextWatcher;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.FindPasswordTask;
import com.changlianxi.util.Utils;

public class FindPasswordStep1 extends FindPasswordStep implements
        OnClickListener, OnTextLengthChange {

    private EditText editEmailOrCellPhone;
    private Button btnNext;

    public FindPasswordStep1(FindPasswordActivity activity, View contentRootView) {
        super(activity, contentRootView);
    }

    @Override
    public void initView() {
        editEmailOrCellPhone = (EditText) findViewById(R.id.edit_cellPhoneOrEmail);
        btnNext = (Button) findViewById(R.id.btn_find_next);
        editEmailOrCellPhone.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public void setListener() {
        editEmailOrCellPhone.addTextChangedListener(new MyEditTextWatcher(
                editEmailOrCellPhone, mContext, this));
        editEmailOrCellPhone
                .setOnFocusChangeListener(new OnEditFocusChangeListener(
                        editEmailOrCellPhone, mContext));
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_next:
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
                findPassword();
                break;
            default:
                break;
        }
    }

    private boolean verify() {
        String str = editEmailOrCellPhone.getText().toString().replace(" ", "");
        if (str.length() == 0) {
            Utils.showToast("请输入手机号码或邮箱地址", Toast.LENGTH_SHORT);
            return false;
        }
        if (str.contains("@")) {
            if (!Utils.isEmail(str)) {
                Utils.showToast("邮箱格式不正确", Toast.LENGTH_SHORT);
                return false;
            }
            mFindPswd.setEmail(str);
        } else {
            if (!Utils.isPhoneNum(str)) {
                Utils.showToast("地球上貌似没有这种格式的手机号码:)", Toast.LENGTH_SHORT);
                return false;
            }
            mFindPswd.setCellPhone(str);
        }
        return true;
    }

    private void findPassword() {
        showDialog("请稍候");
        FindPasswordTask reTask = new FindPasswordTask();
        reTask.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();
                if (result != RetError.NONE) {
                    return;
                }
                mOnNextListener.next();

            }

            @Override
            public void readDBFinish() {

            }
        });
        reTask.executeWithCheckNet(mFindPswd);
    }

    protected String getNum() {
        return editEmailOrCellPhone.getText().toString();

    }

    @Override
    public void onTextLengthChanged(boolean isBlank) {
        if (!isBlank) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.button_new);
            return;
        }
        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.button_hui_new);
    }
}
