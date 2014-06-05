package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class RegisterFinishActivity extends BaseActivity implements
        OnClickListener, PostCallBack {
    private Button btStartUse;
    private EditText editNC;
    private Dialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);
        btStartUse = (Button) findViewById(R.id.startUse);
        btStartUse.setOnClickListener(this);
        editNC = (EditText) findViewById(R.id.editNC);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startUse:
                String name = editNC.getText().toString();
                if (name.replace(" ", "").length() == 0) {
                    Utils.showToast("姓名是必须的哦！", Toast.LENGTH_SHORT);
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("f", "name");
                map.put("token", SharedUtils.getString("token", ""));
                map.put("v", name);
                PostAsyncTask task = new PostAsyncTask(this, map,
                        "/users/isetUserInfo");
                task.setTaskCallBack(this);
                task.execute();
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                break;

            default:
                break;
        }
    }

    @Override
    public void taskFinish(String result) {
        progressDialog.dismiss();
        try {
            JSONObject object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                SharedUtils.setInt("loginType", 1);// 登录方式标记 1 注册登录 2 正常登录
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Utils.showToast("啊哦，设置姓名没有成功，请查看下您的网络是否正常！", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return false;

    }
}
