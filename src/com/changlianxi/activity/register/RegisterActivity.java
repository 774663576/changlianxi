package com.changlianxi.activity.register;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.changlianxi.BaseActivity;
import com.changlianxi.R;
import com.changlianxi.activity.register.RegisterStep.onNextListener;
import com.changlianxi.data.Register;
import com.changlianxi.util.DialogUtil;

public class RegisterActivity extends BaseActivity implements OnClickListener,
        onNextListener {
    private ViewFlipper mVfFlipper;
    private ImageView back;
    private TextView title;
    private TextView txtPage;
    private RegisterStep step;
    private RegisterStep1 step1;
    private RegisterStep2 step2;
    private RegisterStep3 step3;
    private RegisterStep4 step4;
    private Register mRegiser;
    private int mCurrentStepIndex = 1;
    private Dialog dialog;
    private int second = 60;// 用于重新获取验证码时间倒计时
    private int requestCount = 0;
    private long startRequestTime;
    private int countDown = 60 * 10;
    private int countDownRequestTime = countDown;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    step2.setSecond(second);
                    second--;
                    if (second < 0) {
                        step2.setEnable(true);
                        return;
                    }
                    this.sendEmptyMessageDelayed(0, 1000);
                    break;
                case 1:
                    // 一分钟之内不允许发起超过三次获取验证码请求
                    countDownRequestTime--;
                    if (countDownRequestTime <= 0) {
                        this.removeMessages(1);
                        countDownRequestTime = countDown;
                        requestCount = 0;
                        startRequestTime = 0;
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
        setContentView(R.layout.activity_register_activity1);
        initView();
    }

    private void initView() {
        mVfFlipper = (ViewFlipper) findViewById(R.id.reg_vf_viewflipper);
        mVfFlipper.setDisplayedChild(0);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.txt_title);
        txtPage = (TextView) findViewById(R.id.txt_page);
        step = initStep();
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        step.setmOnNextListener(this);
        setValue();
    }

    private void setValue() {
        title.setText("注册新用户");
    }

    private RegisterStep initStep() {
        if (mCurrentStepIndex != 4) {
            back.setVisibility(View.VISIBLE);
        }
        switch (mCurrentStepIndex) {
            case 1:
                if (step1 == null) {
                    step1 = new RegisterStep1(this, mVfFlipper.getChildAt(0));
                }
                txtPage.setText("1/4");
                return step1;
            case 2:
                if (step2 == null) {
                    step2 = new RegisterStep2(this, mVfFlipper.getChildAt(1));
                }
                txtPage.setText("2/4");
                mHandler.sendEmptyMessage(0);
                step2.setValue();
                return step2;
            case 3:
                if (step3 == null) {
                    step3 = new RegisterStep3(this, mVfFlipper.getChildAt(2));
                }
                txtPage.setText("3/4");
                return step3;
            case 4:
                if (step4 == null) {
                    step4 = new RegisterStep4(this, mVfFlipper.getChildAt(3));
                }
                back.setVisibility(View.INVISIBLE);
                txtPage.setText("4/4");
                return step4;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBack();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
        }
        return false;
    }

    private void onBack() {
        if (mCurrentStepIndex <= 1) {
            exit();
        } else if (mCurrentStepIndex == 2) {
            second = 60;
            mHandler.removeMessages(0);
            previous();
        } else {
            previous();
        }
    }

    private void previous() {
        mCurrentStepIndex--;
        step = initStep();
        mVfFlipper.showPrevious();
    }

    @Override
    public void next() {
        mCurrentStepIndex++;
        step = initStep();
        step.setmOnNextListener(this);
        mVfFlipper.showNext();
    }

    protected Register getRegister() {
        if (mRegiser == null) {
            mRegiser = new Register();
        }
        return mRegiser;

    }

    protected void showDialog(String str) {
        dialog = DialogUtil.getWaitDialog(this, str);
        dialog.show();

    }

    protected void cancleDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected String getNum() {
        return step1.getNum();
    }

    protected void postHandler() {
        second = 60;
        mHandler.sendEmptyMessage(0);
    }

    protected int getRequestCount() {
        return requestCount;
    }

    protected void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    protected long getStartRequestTime() {
        return startRequestTime;
    }

    protected void setStartRequestTime(long startRequestTime) {
        this.startRequestTime = startRequestTime;
    }

    protected void startCountDown() {
        if (countDownRequestTime < countDown) {
            return;
        }
        mHandler.sendEmptyMessage(1);

    }
}
