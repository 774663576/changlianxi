package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.changlianxi.R;
import com.changlianxi.adapter.GrowthAlbumAdapter;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbum;
import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.GetGrowthAlbumTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

@SuppressLint("NewApi")
public class AlumYearFragMent extends Fragment implements OnPullDownListener {
    private int cid;
    private GrowthAlbumList albumList;
    private List<GrowthAlbum> album = new ArrayList<GrowthAlbum>();
    private List<Growth> growthList = new ArrayList<Growth>();
    private ListView listview;
    private PullDownView mPullDownView;
    private GrowthAlbumAdapter adapter;
    private Dialog dialog;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    album = albumList.getAlbum();
                    if (album.size() > 0) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (album.size() > 4) {
                            mPullDownView.setFooterVisible(true);
                        }
                    }
                    growthList = albumList.getGrowthList();
                    adapter.setData(album, growthList);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cid = this.getArguments().getInt("cid", 0);
        albumList = new GrowthAlbumList(cid, album);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.alum_layout, null);
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
            initView();
        }
        isOnCreate = true;
    }

    private void getServerData(int startY, int endY) {
        GetGrowthAlbumTask task = new GetGrowthAlbumTask(albumList, startY,
                endY);
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                mPullDownView.notifyDidMore();
                mPullDownView.RefreshComplete();
                mPullDownView.setFooterVisible(false);
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void readDBFinish() {
                mHandler.sendEmptyMessage(0);
            }
        });
        task.executeWithCheckNet();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.albumListView);
        mPullDownView.addFooterView();

        // mPullDownView.hideLastUpdateTime();
        listview = mPullDownView.getListView();
        adapter = new GrowthAlbumAdapter(getActivity(), album, growthList, cid);
        listview.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        mPullDownView.setOnPullDownListener(this);
        mPullDownView.notifyDidMore();
        mPullDownView.setFooterVisible(false);
        mPullDownView.setShowFooter();
        setValue();
    }

    private void setValue() {
        dialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        dialog.show();
        registerBoradcastReceiver();
        getServerData(0, 0);
    }

    public void refush() {
        album.clear();
        growthList.clear();
        getServerData(0, 0);
    }

    /**
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFUSH_ALBUM);
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
            if (action.equals(Constants.REFUSH_ALBUM)) {
                refush();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onRefresh() {
        if (album.size() == 0) {
            mPullDownView.RefreshComplete();
            return;
        }
        String year = DateUtils.getYear(album.get(0).getAlbumDate(), "yyyy");
        if ("".equals(year)) {
            year = "0";
        }

        getServerData(Integer.valueOf(year) + 1, 0);
    }

    @Override
    public void onMore() {
        if (album.size() == 0) {
            mPullDownView.notifyDidMore();
            return;
        }
        String year = DateUtils.getYear(album.get(album.size() - 1)
                .getAlbumDate(), "yyyy");
        if ("".equals(year)) {
            year = "0";
        }

        getServerData(0, Integer.valueOf(year) - 1);

    }

}
