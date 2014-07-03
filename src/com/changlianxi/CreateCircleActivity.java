package com.changlianxi;

import java.util.ArrayList;
import java.util.List;

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
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.tab.fragment.MainTabActivity;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CreateNewCircleTask;
import com.changlianxi.task.IinviteCircleMemberTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 创建圈子界面
 * 
 * @author teeker_bin
 * 
 */
public class CreateCircleActivity extends BaseActivity implements
        OnClickListener, ConfirmDialog {
    private List<CircleMember> contactsList = new ArrayList<CircleMember>();
    private ImageView btnBack;
    private EditText editCirName;
    private Button createCir;
    private Dialog progressDialog;
    private int cid;// 创建圈子返回的cid 邀请成员和上传 logo用
    private TextView titleTxt;
    private CircleMember member;
    private Circle circle;
    private boolean isCallBack = false;
    private int initCircleID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ciecle);
        CLXApplication.addInviteActivity(this);
        btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(this);
        editCirName = (EditText) findViewById(R.id.circleName);
        createCir = (Button) findViewById(R.id.createCircle);
        createCir.setOnClickListener(this);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("创建圈子");
        getActivityValue();
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

    /**
     * 得到上一个activi传过来的值
     */
    @SuppressWarnings("unchecked")
    private void getActivityValue() {
        Bundle bundle = getIntent().getExtras();
        contactsList = (List<CircleMember>) bundle
                .getSerializable("contactsList");
        initCircleID = getIntent().getIntExtra("cid", 0);
        if (initCircleID != 0) {
            Circle c = new Circle(initCircleID);
            c.getCircleName(DBUtils.getDBsa(1));
            editCirName.setText(c.getName());
        }
    }

    private void inviteFInish() {
        Intent it = new Intent();
        it.setClass(this, MainTabActivity.class);
        it.putExtra("circleName", editCirName.getText().toString());
        it.putExtra("cid", cid);
        it.putExtra("newGrowthCount", 1);
        startActivity(it);
        CLXApplication.exitSmsInvite();
        Intent intent = new Intent();
        intent.setClass(this, SetingPublicInfomationActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("type", "createCircle");
        startActivity(intent);
        Utils.leftOutRightIn(this);

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
            case R.id.createCircle:
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
                if (initCircleID < 0) {
                    Intent intent = new Intent(Constants.REMOVE_INIT_CIRCLE);
                    intent.putExtra("cid", initCircleID);
                    BroadCast.sendBroadCast(CreateCircleActivity.this, intent);
                    Circle c = new Circle(initCircleID);
                    c.setStatus(Status.DEL);
                    c.write(DBUtils.getDBsa(2));
                }
                BroadCast.sendBroadCast(CreateCircleActivity.this,
                        Constants.REFRESH_CIRCLE_LIST);
                cid = circle.getId();
                if (contactsList.size() == 0) {
                    promptDialog("圈子创建成功");
                    isCallBack = true;
                    return;
                }
                isCallBack = true;
                InviteMember(circle.getId());

            }

            @Override
            public void readDBFinish() {

            }
        });
        circleTask.executeWithCheckNet(circle);
    }

    /**
     * 邀请 成员
     */
    private void InviteMember(int cid) {
        for (CircleMember m : contactsList) {
            m.setCid(cid);
        }
        member = new CircleMember(cid, 0, Integer.valueOf(Global.getUid()));
        IinviteCircleMemberTask task = new IinviteCircleMemberTask(contactsList);
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (result != RetError.NONE) {
                    promptDialog("圈子创建成功");
                    return;
                }
                promptDialog("圈子创建成功");

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.isPrompt = false;
        task.executeWithCheckNet(member);
    }

    @Override
    public void onOKClick() {
        if (isCallBack) {
            inviteFInish();
        }
        isCallBack = false;
    }

    @Override
    public void onCancleClick() {

    }

}
