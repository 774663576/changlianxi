package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 意见反馈界面
 * 
 * @author teeker_bin
 * 
 */
public class AdviceFeedBackActivity extends BaseActivity implements
        OnClickListener, PostCallBack {
    private ImageView back;
    private TextView titleTxt;
    private EditText editAdvice;
    private Button submit;
    private Dialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice_feed_back);
        findViewByID();
        setListener();
    }

    /**设置页面统计
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

    private void findViewByID() {
        back = (ImageView) findViewById(R.id.back);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("意见反馈");
        editAdvice = (EditText) findViewById(R.id.editAdvice);
        submit = (Button) findViewById(R.id.submit);

    }

    private void setListener() {
        submit.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    /**
     * 提交建议
     */
    private void PostSubmit() {
        String strAdvice = editAdvice.getText().toString();
        if (strAdvice.length() == 0) {
            Utils.showToast("软件用得爽不爽？有什么要吐槽的？有什么你想要的功能？赶快写在这里告诉我们！快！快！快！！",
                    Toast.LENGTH_LONG);
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("token", SharedUtils.getString("token", ""));
        map.put("content", strAdvice);
        map.put("device", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, "/feedbacks/icommit");
        task.setTaskCallBack(this);
        task.execute();
        pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);

                break;
            case R.id.submit:
                PostSubmit();
                break;
            default:
                break;
        }
    }

    @Override
    public void taskFinish(String result) {
        pd.dismiss();
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                Utils.showToast("提交成功！攻城师们会尽快根据您的意见和建议改进产品的！谢谢！",
                        Toast.LENGTH_SHORT);
                finish();
                Utils.rightOut(this);

            } else {
                Utils.showToast("啊哦，意见提交没有成功，请查看下您的网络是否正常！", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
