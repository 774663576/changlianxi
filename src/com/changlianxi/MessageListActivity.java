package com.changlianxi;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.utils.Escaper;

import com.changlianxi.adapter.MessageListAdapter;
import com.changlianxi.data.ChatPartner;
import com.changlianxi.data.ChatPartnerList;
import com.changlianxi.data.Global;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.task.MessageListTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageListActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    private ListView listview;
    private MessageListAdapter adapter;
    private int messageCount;
    private TextView txtMessageCount;
    private ChatPartnerList list = new ChatPartnerList();
    private Dialog progressDialog;
    private TextView noMessage;
    private ImageView back;
    private TextView title;
    private List<ChatPartner> partnersList = new ArrayList<ChatPartner>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initView();
    }

    private void initView() {
        txtMessageCount = (TextView) findViewById(R.id.messageCount);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        listview = (ListView) findViewById(R.id.listView);
        noMessage = (TextView) findViewById(R.id.noMessage);
        setListtener();
    }

    private void setListtener() {
        back.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        listview.setCacheColorHint(0);
        setValue();
    }

    private void setValue() {
        title.setText("私信");
        adapter = new MessageListAdapter(this, list.getPartners());
        listview.setAdapter(adapter);
        getMessageList();
    }

    private void notifyAdapter() {
        noMessage.setVisibility(View.GONE);
        adapter.setData(partnersList);
        for (ChatPartner cp : partnersList) {
            messageCount = +cp.getUnReadCnt();
        }
    }

    private void getMessageList() {
        if (list.getPartners().size() == 0) {
            progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
            progressDialog.show();
        }
        MessageListTask task = new MessageListTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                partnersList = list.getPartners();
                if (partnersList.size() == 0) {
                    noMessage.setVisibility(View.VISIBLE);
                    return;
                }
                notifyAdapter();
            }

            @Override
            public void readDBFinish() {
            }
        });
        task.executeWithCheckNet(list);
    }

    public void refush() {
        getMessageList();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        messageCount -= partnersList.get(position).getUnReadCnt();
        if (messageCount > 0) {
            txtMessageCount.setText(messageCount + "");
            txtMessageCount.setVisibility(View.VISIBLE);
        } else {
            txtMessageCount.setVisibility(View.INVISIBLE);
            Global g = new Global();
            g.read(DBUtils.getDBsa(1));
            g.setNewPersonChatNum(0);
            g.write(DBUtils.getDBsa(2));
        }
        Intent intent = new Intent();
        intent.putExtra("ruid", partnersList.get(position).getPartner());// 要读私信者的id
        intent.putExtra("cid", partnersList.get(position).getCid());// 私信所属圈子ID
        intent.putExtra("name", partnersList.get(position).getPartnerName());
        if (partnersList.get(position).getCid() == 0) {
            intent.setClass(this, PublicMessageActivity.class);
            intent.putExtra("uavatar", partnersList.get(position).getuAvatar());
        } else {
            intent.setClass(this, MessageActivity.class);

        }
        this.startActivity(intent);
        Utils.leftOutRightIn(this);
        ChatPartner c = partnersList.get(position);
        if (c.getUnReadCnt() > 0) {
            c.setUnReadCnt(0);
            c.setStatus(Status.UPDATE);
            c.write(DBUtils.getDBsa(2));
        }
        partnersList.get(position).setUnReadCnt(0);
        adapter.setData(partnersList);
    }

    @Override
    public void onClick(View v) {
        exit();
    }

}
