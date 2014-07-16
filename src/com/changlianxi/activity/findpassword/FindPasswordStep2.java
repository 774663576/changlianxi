package com.changlianxi.activity.findpassword;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.MyEditTextWatcher;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.FindPswdGetAuthCodeAgainTask;
import com.changlianxi.task.FindPswdVerifyAuthCodeTask;
import com.changlianxi.util.Utils;

public class FindPasswordStep2 extends FindPasswordStep implements
        OnClickListener, OnTextLengthChange {
    private TextView txtShow;
    private TextView txtNum;
    private EditText editAuthCode;
    private Button btnNext;
    private Button btnGetAuthCode;

    public FindPasswordStep2(FindPasswordActivity activity, View contentRootView) {
        super(activity, contentRootView);
    }

    @Override
    public void initView() {
        txtNum = (TextView) findViewById(R.id.text_num);
        txtShow = (TextView) findViewById(R.id.text_show);
        editAuthCode = (EditText) findViewById(R.id.edit_authCode);
        btnGetAuthCode = (Button) findViewById(R.id.btn_get_authCode);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    @Override
    public void setListener() {
        editAuthCode.addTextChangedListener(new MyEditTextWatcher(editAuthCode,
                mContext, this));
        editAuthCode.setOnFocusChangeListener(new OnEditFocusChangeListener(
                editAuthCode, mContext));
        btnNext.setOnClickListener(this);
        btnGetAuthCode.setOnClickListener(this);
    }

    protected void setValue() {
        if (Utils.isEmail(mActivity.getNum())) {
            txtShow.setText("我们已经向您的电子邮箱发送验证码 ");
        } else {
            txtShow.setText("我们已经向您的手机号码发送验证码 ");
        }
        txtNum.setText(mActivity.getNum());
    }

    protected void setSecond(int second) {
        btnGetAuthCode.setText("重新获取验证码（" + second + "秒）");
    }

    protected void setEnable(boolean flag) {
        if (flag) {
            btnGetAuthCode.setText("重新获取验证码");
            btnGetAuthCode.setEnabled(true);
            btnGetAuthCode.setBackgroundResource(R.drawable.button_get_code);
        } else {
            btnGetAuthCode.setEnabled(false);
            btnGetAuthCode.setBackgroundResource(R.drawable.button_hui_new);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_authCode:
                Utils.getFocus(v);
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                getAuthCode();
                break;
            case R.id.btnNext:
                Utils.getFocus(v);
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                verifyAuthCode();
                break;
            default:
                break;
        }
    }

    private void getAuthCode() {
        showDialog("请稍候");

        FindPswdGetAuthCodeAgainTask task = new FindPswdGetAuthCodeAgainTask();
        task.isPrompt = false;
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();

                if (result != RetError.NONE) {
                    Utils.showToast("啊哦，验证码获取没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                    return;
                }
                setEnable(false);
                mActivity.postHandler();
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(mFindPswd);
    }

    /**
     * 验证
     */
    private void verifyAuthCode() {
        showDialog("请稍候");
        FindPswdVerifyAuthCodeTask task = new FindPswdVerifyAuthCodeTask(
                editAuthCode.getText().toString());
        task.isPrompt = false;
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();
                if (result != RetError.NONE) {
                    Utils.showToast("啊哦，验证码不对\n验证码为6个数字，请再确认输入一次",
                            Toast.LENGTH_SHORT);
                    return;
                }
                mOnNextListener.next();
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
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.button_new);
            return;
        }
        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.button_hui_new);
    }
}
