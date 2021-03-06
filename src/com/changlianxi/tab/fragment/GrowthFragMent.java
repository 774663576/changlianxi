package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.CommentsListActivity;
import com.changlianxi.R;
import com.changlianxi.adapter.GrowthAdapter;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.GrowthListTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

public class GrowthFragMent extends Fragment implements OnClickListener,
        OnItemClickListener, OnPullDownListener {
    private PullDownView mPullDownView;
    private GrowthAdapter adapter;
    private TextView promptCount;
    private LinearLayout layPrompt;
    private GrowthListTask growthListTask;
    private ListView mListView;
    private List<Growth> listData = new ArrayList<Growth>();
    private long start = 0l;
    private long end = 0l;
    private GrowthList growthList;
    private Dialog dialog = null;
    private boolean isAuth = true;
    private int newGrowthCount = 0;
    private int newCommentCount = 0;
    private int cid = 0;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private View footerView;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (listData.size() > 0) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                    adapter.setData(listData);
                    if (footerView != null && listData.size() >= 3) {
                        footerView.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newGrowthCount = getArguments().getInt("newGrowthCount", 0);
        newCommentCount = getArguments().getInt("newCommentCount", 0);
        isAuth = getArguments().getBoolean("isAuth");
        cid = getArguments().getInt("cid", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.growth_fragment_layout, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isOnCreate) {
            // CheckImageLoaderConfiguration
            // .checkImageLoaderConfiguration(getActivity());
            initView();
        }
        isOnCreate = true;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        layPrompt = (LinearLayout) getView().findViewById(R.id.prompt);
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.PullDownlistView);
        mListView = mPullDownView.getListView();
        mListView.setCacheColorHint(0);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        promptCount = (TextView) getView().findViewById(R.id.promptCount);
        if (!isAuth) {
            footerView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.auth_foot_view, null, false);
            TextView t = (TextView) footerView.findViewById(R.id.txtFoot);
            t.setText("亲，您还未通过认证，暂时只能看到3条圈子成长，赶快找人帮你认证吧。");
            mListView.addFooterView(footerView);
            footerView.setVisibility(View.GONE);
        }
        setListener();
        visivlePromptCount();
    }

    private void setListener() {
        adapter = new GrowthAdapter(getActivity(), listData);
        mListView.setAdapter(adapter);
        mPullDownView.setOnPullDownListener(this);
        mPullDownView.notifyDidMore();
        mListView.setOnItemClickListener(this);
        layPrompt.setOnClickListener(this);
        mPullDownView.addFooterView();
        mPullDownView.setShowFooter();
        setValue();
    }

    private void setValue() {
        dialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        dialog.show();
        growthList = new GrowthList(cid);
        registerBoradcastReceiver();
        filldata(true, false, newGrowthCount);

    }

    private void visivlePromptCount() {
        if (newCommentCount > 0) {
            layPrompt.setVisibility(View.VISIBLE);
            promptCount.setText(newCommentCount + "条回复消息");
        }
    }

    private void filldata(boolean isReadDB, boolean isMore, int newGrowthCount) {
        System.out.println("filldatafilldatafilldata");
        growthListTask = new GrowthListTask(growthList, isReadDB, start, end,
                isMore, newGrowthCount);
        growthListTask.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                mPullDownView.notifyDidMore();
                mPullDownView.RefreshComplete();
                if (result != RetError.NONE) {
                    return;
                }
                listData = growthList.getGrowths();
                if (listData.size() == 0) {
                    return;
                }
                adapter.setData(listData);
                if (growthList.getServerCount() > 19) {
                    mPullDownView.setFooterVisible(true);
                } else {
                    mPullDownView.setFooterVisible(false);
                    if (growthList.getServerCount() < 0) {
                        if (listData.size() > 19) {
                            mPullDownView.setFooterVisible(true);
                        }
                    }
                }
                if (footerView != null && listData.size() >= 3) {
                    footerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void readDBFinish() {
                listData = growthList.getGrowths();
                mHandler.sendEmptyMessage(0);
            }
        });
        growthListTask.executeWithCheckNet();
    }

    @Override
    public void onRefresh() {
        if (listData.size() > 0) {
            if (listData.get(0).isUpLoading()) {
                mPullDownView.RefreshComplete();
                return;
            }
        }
        end = Long.valueOf(DateUtils.phpTime(System.currentTimeMillis()));
        newGrowthCount = 1;
        filldata(false, false, newGrowthCount);

    }

    @Override
    public void onMore() {
        if (listData.size() > 0) {
            if (listData.get(0).isUpLoading()) {
                mPullDownView.notifyDidMore();
                return;
            }
        }
        start = 0l;
        end = Long.valueOf(DateUtils.phpTime(DateUtils.convertToDate(listData
                .get(listData.size() - 1).getPublished())));
        newGrowthCount = 1;
        filldata(false, true, newGrowthCount);
    }

    public void addLoacalGrowth(Growth g) {
        listData.add(0, g);
        adapter.setData(listData);
        mListView.setSelection(0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prompt:
                removoCirclePromptCount();
                Intent it = new Intent();
                it.setClass(getActivity(), CommentsListActivity.class);
                it.putExtra("cid", cid);
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                layPrompt.setVisibility(View.GONE);

                break;
            case R.id.back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    /**
     * 减少圈子首页的当前圈子提示数量
     */
    private void removoCirclePromptCount() {
        Circle c = new Circle(cid);
        c.read(DBUtils.getDBsa(1));
        c.setStatus(Status.UPDATE);
        c.setNewGrowthCommentCnt(0);
        c.write(DBUtils.getDBsa(2));
    }

    /**
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFUSH_GROWTH);
        myIntentFilter.addAction(Constants.REFUSH_GROWTH_PRAISE_COUNT);
        myIntentFilter.addAction(Constants.ADD_LOCAL_GROWTH);
        myIntentFilter.addAction(Constants.GROWTH_UPLOADING_SUCCESS);
        myIntentFilter.addAction(Constants.GROWTH_UPLOADING_FAIL);
        myIntentFilter.addAction(Constants.DEL_GROWTH);

        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 定义广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.REFUSH_GROWTH)) {
                end = Long
                        .valueOf(DateUtils.phpTime(System.currentTimeMillis()));
                newGrowthCount = 1;
                filldata(true, false, newGrowthCount);
            } else if (action.equals(Constants.REFUSH_GROWTH_PRAISE_COUNT)) {
                Growth g = (Growth) intent.getExtras()
                        .getSerializable("growth");
                for (Growth growth : listData) {
                    if (growth.getId() == g.getId()) {
                        growth.setPraiseCnt(g.getPraiseCnt());
                        growth.setPraised(g.isPraised());
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            } else if (action.equals(Constants.ADD_LOCAL_GROWTH)) {
                if (intent.getExtras() == null) {
                    return;
                }
                Growth g = (Growth) intent.getExtras()
                        .getSerializable("growth");
                addLoacalGrowth(g);
            } else if (action.equals(Constants.GROWTH_UPLOADING_SUCCESS)) {
                Growth g = (Growth) intent.getExtras()
                        .getSerializable("growth");
                for (Growth growth : listData) {
                    if (growth.getId() == 0) {
                        growth.setId(g.getId());
                        growth.setImages(g.getImages());
                        growth.setUpLoading(false);
                        adapter.notifyDataSetChanged();
                        break;
                    }

                }

            } else if (action.equals(Constants.GROWTH_UPLOADING_FAIL)) {
                for (Growth g : listData) {
                    if (g.getId() == 0) {
                        g.setLoadingFail(true);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            } else if (action.equals(Constants.DEL_GROWTH)) {
                int cid = intent.getIntExtra("cid", 0);
                for (int i = 0; i < listData.size(); i++) {
                    if (cid == listData.get(i).getCid()) {
                        listData.remove(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.clearCache();
        getActivity().unregisterReceiver(mBroadcastReceiver);

    }

}
