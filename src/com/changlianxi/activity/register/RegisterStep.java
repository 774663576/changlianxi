package com.changlianxi.activity.register;

import android.content.Context;
import android.view.View;

import com.changlianxi.data.Register;

public abstract class RegisterStep {
    private View contentRootView;
    protected RegisterActivity mActivity;
    protected onNextListener mOnNextListener;
    protected Context mContext;
    protected Register mRegister;

    public RegisterStep(RegisterActivity activity, View contentRootView) {
        this.contentRootView = contentRootView;
        this.mActivity = activity;
        this.mContext = (Context) activity;
        initView();
        setListener();
        mRegister = mActivity.getRegister();
    }

    public abstract void initView();

    public abstract void setListener();

    public View findViewById(int id) {
        return contentRootView.findViewById(id);

    }

    public void setmOnNextListener(onNextListener mOnNextListener) {
        this.mOnNextListener = mOnNextListener;
    }

    public interface onNextListener {
        void next();
    }

    protected void showDialog(String str) {
        mActivity.showDialog(str);

    }

    protected void cancleDialog() {
        mActivity.cancleDialog();
    }

}
