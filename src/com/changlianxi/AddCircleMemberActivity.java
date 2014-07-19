package com.changlianxi;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddCircleMemberActivity extends BaseActivity implements
        OnClickListener {
    private ImageView back;
    private TextView txtTitle;
    private LinearLayout layByContacts;
    private LinearLayout layByInput;
    private LinearLayout layByClx;
    private LinearLayout layPage;
    private LinearLayout layExcel;
    private String type;
    private int cid;
    private String cirName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_circle_member_activity1);
        CLXApplication.addInviteActivity(this);
        initView();
        getDataFromActivity();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        txtTitle = (TextView) findViewById(R.id.titleTxt);
        txtTitle.setText("添加第一批成员");
        layByClx = (LinearLayout) findViewById(R.id.addByClx);
        layByContacts = (LinearLayout) findViewById(R.id.addByContacts);
        layByInput = (LinearLayout) findViewById(R.id.addByInput);
        layExcel = (LinearLayout) findViewById(R.id.btnExcel);
        layPage = (LinearLayout) findViewById(R.id.btnPage);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        layByClx.setOnClickListener(this);
        layByContacts.setOnClickListener(this);
        layByInput.setOnClickListener(this);
        layExcel.setOnClickListener(this);
        layPage.setOnClickListener(this);

    }

    private void getDataFromActivity() {
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);
        if (type.equals("add")) {
            txtTitle.setText("添加成员");
            cirName = getIntent().getStringExtra("cirName");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.addByContacts:
                intent.setClass(this, SelectContactsActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("cid", cid);
                intent.putExtra("cirName", cirName);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.addByInput:
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
