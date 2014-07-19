package com.changlianxi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.MessageActivity;
import com.changlianxi.PublicMessageActivity;
import com.changlianxi.R;
import com.changlianxi.adapter.MessageListAdapter;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.ChatPartner;
import com.changlianxi.data.ChatPartnerList;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.SetLeftMenuPrompt;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.MessageListTask;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;

@SuppressLint("NewApi")
public class MessageListFragMent extends Fragment implements OnClickListener,
        OnItemClickListener {
    private ListView listview;
    private MessageListAdapter adapter;
    private int messageCount;
    private TextView txtMessageCount;
    private ChatPartnerList list = new ChatPartnerList();
    private Dialog progressDialog;
    private TextView noMessage;
    private ImageView menu;
    private TextView title;
    private List<ChatPartner> partnersList = new ArrayList<ChatPartner>();
    private SetLeftMenuPrompt callBack;

    private Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (callBack == null) {
            callBack = (SetLeftMenuPrompt) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, null);
     }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        txtMessageCount = (TextView) getView().findViewById(R.id.messageCount);
        menu = (ImageView) getView().findViewById(R.id.back);
        menu.setImageResource(R.drawable.icon_list);
        title = (TextView) getView().findViewById(R.id.titleTxt);
        listview = (ListView) getView().findViewById(R.id.listView);
        noMessage = (TextView) getView().findViewById(R.id.noMessage);
        setListtener();
    }

    private void setListtener() {
        menu.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        listview.setCacheColorHint(0);
        setValue();
    }

    private void setValue() {
        title.setText("私信");
        adapter = new MessageListAdapter(getActivity(), list.getPartners());
        listview.setAdapter(adapter);
        mHander.sendEmptyMessageDelayed(1, 0);
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
            progressDialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
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
            callBack.setMenuPrompt(2, false);
        }
        Intent intent = new Intent();
        intent.putExtra("ruid", partnersList.get(position).getPartner());// 要读私信者的id
        intent.putExtra("cid", partnersList.get(position).getCid());// 私信所属圈子ID
        intent.putExtra("name", partnersList.get(position).getPartnerName());
        if (partnersList.get(position).getCid() == 0) {
            intent.setClass(getActivity(), PublicMessageActivity.class);
            intent.putExtra("uavatar", partnersList.get(position).getuAvatar());
        } else {
            intent.setClass(getActivity(), MessageActivity.class);

        }
        getActivity().startActivity(intent);
        Utils.leftOutRightIn(getActivity());
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
        switch (v.getId()) {
            case R.id.back:
                ((SlidingActivity) getActivity()).getSlidingMenu().toggle();

                break;

            default:
                break;
        }
    }
}