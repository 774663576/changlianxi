package com.changlianxi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;

public class CircleGuideActivity extends BaseActivity implements
        OnClickListener {
    private ImageView img;
    private ImageView back;
    private TextView title;
    private int cid;
    private TextView txtShow;
    private Button btnAdd;
    private TextView notWarn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_guide);
        CLXApplication.addInviteActivity(this);
        cid = getIntent().getIntExtra("cid", 0);
        initView();

    }

    private void initView() {
        img = (ImageView) findViewById(R.id.img);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        txtShow = (TextView) findViewById(R.id.showTxt);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        notWarn = (TextView) findViewById(R.id.notWarn);
        notWarn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        notWarn.getPaint().setAntiAlias(true);// 抗锯齿
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        notWarn.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        if (cid == -3) {
            title.setText("创建家人圈子");
            img.setImageResource(R.drawable.family);
            btnAdd.setText("马上添加家人");
            txtShow.setText("记录家人的生日和重要纪念日\n永久保存温馨时刻的每张照片");
        } else if (cid == -1) {
            title.setText("创建同事圈子");
            img.setImageResource(R.drawable.workmate);
            btnAdd.setText("马上添加同事");
            txtShow.setText("最快认识了解每一位新老同事\n永久记录分享每一次齐心协力");

        } else if (cid == -2) {
            title.setText("创建同学圈子");
            img.setImageResource(R.drawable.classmate);
            btnAdd.setText("马上添加老同学");
            txtShow.setText("多年同窗挚友新动向尽在掌握\n新老照片让深情厚谊永久镌刻");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btnAdd:
                Intent intent = new Intent();
                intent.setClass(this, AddCircleMemberActivity.class);
                intent.putExtra("type", "create");
                intent.putExtra("cid", cid);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.notWarn:
                confirmDialog();
                break;
            default:
                break;
        }
    }

    private void confirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(this, "确定要关闭提示吗？", "确定", "取消",
                new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        Intent intent = new Intent(Constants.REMOVE_INIT_CIRCLE);
                        intent.putExtra("cid", cid);
                        BroadCast.sendBroadCast(CircleGuideActivity.this,
                                intent);
                        Circle c = new Circle(cid);
                        c.setStatus(Status.DEL);
                        c.write(DBUtils.getDBsa(2));
                        exit();
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }
}
