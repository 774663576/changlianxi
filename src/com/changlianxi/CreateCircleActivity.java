package com.changlianxi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CreateNewCircleTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;

/**
 * 创建圈子界面
 * 
 * @author teeker_bin
 * 
 */
public class CreateCircleActivity extends BaseActivity implements
        OnClickListener, ConfirmDialog {
    private ImageView btnBack;
    private EditText editCirName;
    private Button btnFinish;
    private Dialog progressDialog;
    private TextView titleTxt;
    private Circle circle;
    private boolean isCallBack = false;
    private int initCircleID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ciecle);
        CLXApplication.addInviteActivity(this);
        initView();
    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.back);
        editCirName = (EditText) findViewById(R.id.editCircleName);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("创建圈子");
        setListener();
    }

    private void setListener() {
        btnBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

    }

    private void promptDialog(String str) {
        Dialog dialog = DialogUtil.promptDialog(this, str, "确定", this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.btnFinish:
                String str = editCirName.getText().toString();
                if (str.length() == 0) {
                    promptDialog("圈子名称是必须的哦！");
                    return;
                }
                if (StringUtils.calculatePlaces(str) > 12) {
                    promptDialog("圈子名称长度超过限制，请控制在12个汉字以内，字母和数字两个字符算一个汉字");
                    return;
                }
                createNewCirTask();
                break;
            default:
                break;
        }
    }

    /**
     * 创建圈子
     */
    private void createNewCirTask() {
        progressDialog = DialogUtil.getWaitDialog(CreateCircleActivity.this,
                "请稍候");
        progressDialog.show();
        circle = new Circle(0, editCirName.getText().toString(), "");
        circle.setJoinTime(DateUtils.getCurrDateStr());
        CreateNewCircleTask circleTask = new CreateNewCircleTask();
        circleTask.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                Intent intent;
                if (initCircleID < 0) {
                    intent = new Intent(Constants.REMOVE_INIT_CIRCLE);
                    intent.putExtra("cid", initCircleID);
                    BroadCast.sendBroadCast(CreateCircleActivity.this, intent);
                    Circle c = new Circle(initCircleID);
                    c.setStatus(Status.DEL);
                    c.write(DBUtils.getDBsa(2));
                }
                intent = new Intent(Constants.ADD_NEW_CIRCLE);
                intent.putExtra("circle", circle);
                BroadCast.sendBroadCast(CreateCircleActivity.this, intent);
                promptDialog("圈子创建成功");
                isCallBack = true;

            }

            @Override
            public void readDBFinish() {

            }
        });
        circleTask.executeWithCheckNet(circle);
    }

    @Override
    public void onOKClick() {
        if (isCallBack) {
            Intent intent = new Intent();
            intent.putExtra("cid", circle.getId());
            intent.setClass(CreateCircleActivity.this,
                    AddCircleMemberActivity.class);
            startActivity(intent);
            finish();
        }
        isCallBack = false;
    }

    @Override
    public void onCancleClick() {

    }

}
