package com.changlianxi.activity.findpassword;

import android.content.Context;
import android.view.View;

import com.changlianxi.data.FindPassword;

public abstract class FindPasswordStep {
    private View contentRootView;
    protected FindPasswordActivity mActivity;
    protected onNextListener mOnNextListener;
    protected Context mContext;
    protected FindPassword mFindPswd;

    public FindPasswordStep(FindPasswordActivity activity, View contentRootView) {
        this.contentRootView = contentRootView;
        this.mActivity = activity;
        this.mContext = (Context) activity;
        initView();
        setListener();
        mFindPswd = mActivity.getmFindPswd();
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
