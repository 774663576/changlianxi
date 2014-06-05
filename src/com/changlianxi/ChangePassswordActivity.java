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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.inteface.OnEditFocusChangeListener;
import com.changlianxi.inteface.PasswordEditTextWatcher;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改密码
 * 
 * @author teeker_bin
 * 
 */
public class ChangePassswordActivity extends BaseActivity implements
        OnClickListener, PostCallBack {
    private Button ok;
    private SearchEditText nowPasswrod;
    private SearchEditText newPassword;
    private TextView titleTxt;
    private ImageView back;
    private Dialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passsword);
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
        ok = (Button) findViewById(R.id.ok);
        nowPasswrod = (SearchEditText) findViewById(R.id.nowPassword);
        newPassword = (SearchEditText) findViewById(R.id.newPassword);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("修改密码");
        back = (ImageView) findViewById(R.id.back);
        nowPasswrod.addTextChangedListener(new PasswordEditTextWatcher(
                nowPasswrod, this, true));
        newPassword.addTextChangedListener(new PasswordEditTextWatcher(
                newPassword, this, true));
        newPassword.setOnFocusChangeListener(new OnEditFocusChangeListener(
                newPassword, this));
        nowPasswrod.setOnFocusChangeListener(new OnEditFocusChangeListener(
                nowPasswrod, this));

    }

    private void setListener() {
        ok.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void changePassword() {
        nowPasswrod.clearFocus();
        newPassword.clearFocus();
        String oldpswd = nowPasswrod.getText().toString();
        String newpswd = newPassword.getText().toString();
        if (oldpswd.length() == 0) {
            Utils.showToast("老密码要输入，出门在外，安全第一:p", Toast.LENGTH_SHORT);
            return;
        }

        if (newpswd.length() < 6) {
            Utils.showToast("密码至少6个字符，出门在外，安全第一:)", Toast.LENGTH_SHORT);
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("token", SharedUtils.getString("token", ""));
        map.put("old", oldpswd);
        map.put("new", newpswd);
        PostAsyncTask task = new PostAsyncTask(this, map,
                "/users/ichangePasswd");
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
            case R.id.ok:
                changePassword();
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
                Utils.showToast("新密码设成功了，牢记哦！", Toast.LENGTH_SHORT);
                finish();
                Utils.rightOut(this);

            } else {
                String err = object.getString("err");
                if (err.equals("OLD_PASSWD_WRONG")) {
                    Utils.showToast("老密码不对啊。您是怎么登录进来的？认真想想哦。",
                            Toast.LENGTH_SHORT);
                } else {
                    Utils.showToast("啊哦，密码设置没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
