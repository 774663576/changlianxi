package com.changlianxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 选择添加成员方式界面
 * 
 * @author teeker_bin
 * 
 */
public class AddCircleMemberActivity extends BaseActivity implements
        OnClickListener {
    private Button add;
    private Button input;
    private ImageView back;
    private String type;
    private int cid;
    private String cirName;
    private TextView titleTxt;
    private TextView btnPage;
    private TextView btnExcel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_circle_member);
        CLXApplication.addInviteActivity(this);
        back = (ImageView) findViewById(R.id.back);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("添加第一批成员");
        add = (Button) findViewById(R.id.addFromAddBook);
        input = (Button) findViewById(R.id.inputContact);
        add.setOnClickListener(this);
        input.setOnClickListener(this);
        back.setOnClickListener(this);
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);
        if (type.equals("add")) {
            titleTxt.setText("添加成员");
            cirName = getIntent().getStringExtra("cirName");
        }
        btnPage = (TextView) findViewById(R.id.btnPage);
        btnPage.setOnClickListener(this);
        btnExcel = (TextView) findViewById(R.id.btnExcel);
        btnExcel.setOnClickListener(this);
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
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.addFromAddBook:
                intent.setClass(this, SelectContactsActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("cid", cid);
                intent.putExtra("cirName", cirName);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.inputContact:
                intent.setClass(this, AddOneMemberActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("cirName", cirName);
                intent.putExtra("type", type);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.btnPage:
                intent.setClass(this, PageActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btnExcel:
                intent.setClass(this, ExcelActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            default:
                break;
        }

    }
}
