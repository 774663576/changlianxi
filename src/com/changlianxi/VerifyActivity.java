package com.changlianxi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.InputMethodRelativeLayout;
import com.changlianxi.view.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 验证手机号
 * @author LG
 *
 */
public class VerifyActivity extends BaseActivity implements OnClickListener,
        PostCallBack, OnSizeChangedListenner {
    private Button yanzhengButton;
    private SearchEditText editText;
    private TextView tiaoguoTextView;
    private TextView textView2, textView3;
    private String cellphone = "";
    private ImageView iv_back;
    private String PATH = "/users/ibindCellphone";
    private InputMethodRelativeLayout parent;
    private String type = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglutiaoguo);
        CLXApplication.addActivity(this);
        type = getIntent().getExtras().getString("type");
        parent = (InputMethodRelativeLayout) findViewById(R.id.Layparent);
        parent.setOnSizeChangedListenner(this);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        yanzhengButton = (Button) findViewById(R.id.yanzheng);
        editText = (SearchEditText) findViewById(R.id.num);
        editText.addTextChangedListener(new EditWather(editText, this));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tiaoguoTextView = (TextView) findViewById(R.id.tiaoguo);
        tiaoguoTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tiaoguoTextView.setOnClickListener(this);
        if (type.equals("laterbind")) {
            tiaoguoTextView.setVisibility(View.GONE);
        }
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
        yanzhengButton.setOnClickListener(this);

    }

    /**
     * 设置页面统计
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
            case R.id.yanzheng:
                cellphone = editText.getText().toString().replace("-", "");
                if (editText.getText().toString().length() == 0) {
                    Utils.showToast("您貌似没有填手机号码。。", Toast.LENGTH_SHORT);
                    return;
                }
                if (!Utils.isPhoneNum(cellphone)) {
                    Utils.showToast("地球上貌似没有这种格式的手机号码:p", Toast.LENGTH_SHORT);
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", SharedUtils.getString("uid", ""));
                map.put("token", SharedUtils.getString("token", ""));
                map.put("cellphone", cellphone);

                PostAsyncTask task = new PostAsyncTask(this, map, PATH);
                task.setTaskCallBack(this);
                task.execute();
                break;

            case R.id.tiaoguo:
                Intent it = new Intent();
                it.setClass(VerifyActivity.this, MainActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.back:
                finish();
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;
        }
    }

    @Override
    public void taskFinish(String result) {
        JSONObject object;
        try {
            object = new JSONObject(result);
            int rt = object.getInt("rt");

            if (rt == 1) {
                String rid = object.getString("rid");
                Intent intent = new Intent();
                intent.putExtra("uid", SharedUtils.getString("uid", ""));
                intent.putExtra("token", SharedUtils.getString("token", ""));
                intent.putExtra("cellphone", editText.getText().toString());
                intent.putExtra("rid", rid);
                intent.setClass(VerifyActivity.this, VerifyWaitActivity.class);
                startActivity(intent);
                // finish();
            } else {
                String errString = object.getString("err");
                if (errString.equals("CELLPHONE_ALREADY_USED")) {
                    Utils.showToast("这个号码已经注册啦！如忘记密码可以用找回密码功能哦。",
                            Toast.LENGTH_SHORT);
                } else {
                    Utils.showToast("啊哦，手机号验证没有成功，请查看下您的网络是否正常！",
                            Toast.LENGTH_SHORT);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        // TODO Auto-generated method stub
        if (flag) {// 键盘弹出时
            parent.setPadding(0, -10, 0, 0);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
        } else { // 键盘隐藏时
            parent.setPadding(0, 0, 0, 0);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
        }
    }
}
