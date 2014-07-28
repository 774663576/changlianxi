package com.changlianxi.activity.register;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changlianxi.CircleHomeActivity;
import com.changlianxi.R;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.MyEditTextWatcher.OnTextLengthChange;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.RegisterSetNameTask;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

public class RegisterStep4 extends RegisterStep implements OnClickListener,
        OnTextLengthChange {
    private EditText editName;
    private Button btnNext;

    public RegisterStep4(RegisterActivity activity, View contentRootView) {
        super(activity, contentRootView);
    }

    @Override
    public void initView() {
        editName = (EditText) findViewById(R.id.edit_name);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    @Override
    public void setListener() {
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
                String name = editName.getText().toString();
                if (name.length() == 0) {
                    Utils.showToast("姓名不能为空", Toast.LENGTH_SHORT);
                    return;
                }

                setName(name);
                break;

            default:
                break;
        }
    }

    private void setName(String name) {
        showDialog("请稍候");
        RegisterSetNameTask task = new RegisterSetNameTask(name);
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                cancleDialog();
                if (result != RetError.NONE) {
                    return;
                }
                SharedUtils.setInt("loginType", 1);// 登录方式标记 1 注册登录 2 正常登录
                Intent intent = new Intent();
                intent.setClass(mContext, CircleHomeActivity.class);
                mContext.startActivity(intent);
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(mRegister);
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
