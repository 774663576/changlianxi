package com.changlianxi;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.NewsCommentsListAdapter;
import com.changlianxi.modle.NewsComments;
import com.changlianxi.task.GetCommentsForMeTask;
import com.changlianxi.task.GetCommentsForMeTask.GetCommentsForMeCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 获取圈子成长中跟我相关的评论列表内容
 * 
 * @author teeker_bin
 * 
 */
public class CommentsListActivity extends BaseActivity implements
        OnClickListener, GetCommentsForMeCallBack, OnItemClickListener {
    private ImageView back;
    private ListView listView;
    private TextView title;
    private int cid;
    private NewsCommentsListAdapter adapter;
    private List<NewsComments> listModles = new ArrayList<NewsComments>();
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);
        cid = getIntent().getIntExtra("cid", 0);
        initView();
        setListener();
        setAdapter();
        getComments();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listView);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("消息列表");

    }

    private void setListener() {
        back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    public void setAdapter() {
        adapter = new NewsCommentsListAdapter(this, listModles);
        listView.setAdapter(adapter);
    }

    private void getComments() {
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        GetCommentsForMeTask task = new GetCommentsForMeTask(this, cid,
                SharedUtils.getString("exitGrowthListTime", "0"),
                DateUtils.phpTime(System.currentTimeMillis()));
        task.setTaskCallBack(this);
        task.execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            default:
                break;
        }
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
    public void getCommentList(List<NewsComments> listModle) {
        if (dialog != null) {
            dialog.dismiss();
        }
        listModles.addAll(listModle);
        adapter.setDate(listModles);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent intent = new Intent();
        intent.putExtra("gid", listModles.get(position).getGid());
        intent.putExtra("cid", cid);
        intent.setClass(this, CommentsListItemActivity.class);
        startActivity(intent);
        finish();
        Utils.leftOutRightIn(this);

    }

    @Override
    protected void onDestroy() {
        SharedUtils.setString("exitGrowthListTime",
                DateUtils.phpTime(System.currentTimeMillis()));
        super.onDestroy();
    }
}
