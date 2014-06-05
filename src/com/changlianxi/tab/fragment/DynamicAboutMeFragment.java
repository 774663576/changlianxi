package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.changlianxi.R;
import com.changlianxi.adapter.DynamicAdoutMeAdapter;
import com.changlianxi.data.CircleDynamic;
import com.changlianxi.data.CircleDynamicList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.task.CircleDynamicTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

public class DynamicAboutMeFragment extends Fragment implements
        OnPullDownListener {
    private int newDynamicCount = 0;
    private int cid;
    private Dialog pd = null;
    private DynamicAdoutMeAdapter adapter;
    private PullDownView mPullDownView;
    private ListView mListView;
    private CircleDynamicList dynamicList;
    private List<CircleDynamic> dynamics = new ArrayList<CircleDynamic>();
    private CircleDynamicTask task;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dynamics = dynamicList.getDynamics();
                    if (dynamics.size() > 0) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                    if (dynamicList.getServerCount() > 19) {
                        mPullDownView.setFooterVisible(true);
                    } else {
                        mPullDownView.setFooterVisible(false);
                        if (dynamicList.getServerCount() < 0) {
                            if (dynamics.size() > 19) {
                                mPullDownView.setFooterVisible(true);
                            }
                        }
                    }
                    adapter.setData(dynamics);
                    break;
                default:
                    break;
            }
        }
    };

    public static DynamicAboutMeFragment newInstance(int cid,
            int newDynamicCount) {
        DynamicAboutMeFragment f = new DynamicAboutMeFragment();
        Bundle args = new Bundle();
        args.putInt("cid", cid);
        args.putInt("newDynamicCount", newDynamicCount);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cid = getArguments().getInt("cid", 0);
        newDynamicCount = getArguments().getInt("newDynamicCount", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.dyamic_about_me, null);
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

    private void initView() {
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.PullDownlistView);
        adapter = new DynamicAdoutMeAdapter(getActivity(), dynamics);
        mListView = mPullDownView.getListView();
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        setListener();
    }

    private void setListener() {
        mPullDownView.setOnPullDownListener(this);
        setValue();
    }

    private void setValue() {
        mListView.setAdapter(adapter);
        dynamicList = new CircleDynamicList(cid, true);
        pd = DialogUtil.getWaitDialog(getActivity(),
                getString(R.string.dialogTxt));
        pd.show();
        getCircleDynamicList(true, false, newDynamicCount);
    }

    /**
     * 获取动态列表
     */
    private void getCircleDynamicList(boolean readDB, boolean isMore,
            int newDynamicCount) {
        task = new CircleDynamicTask(readDB, isMore, newDynamicCount);
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (pd != null) {
                    pd.dismiss();
                }
                mPullDownView.notifyDidMore();
                mPullDownView.RefreshComplete();
                if (result != RetError.NONE) {
                    return;
                }
                if (dynamicList.getServerCount() > 19) {
                    mPullDownView.setFooterVisible(true);
                } else {
                    mPullDownView.setFooterVisible(false);
                    if (dynamicList.getServerCount() < 0) {
                        if (dynamics.size() > 19) {
                            mPullDownView.setFooterVisible(true);
                        }
                    }
                }
                dynamics = dynamicList.getDynamics();
                adapter.setData(dynamics);
            }

            @Override
            public void readDBFinish() {
                mHandler.sendEmptyMessage(0);

            }
        });
        task.executeWithCheckNet(dynamicList);
    }

    /** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
    @Override
    public void onRefresh() {
        newDynamicCount = 1;
        getCircleDynamicList(false, false, newDynamicCount);
    }

    /** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
    @Override
    public void onMore() {
        newDynamicCount = 1;
        getCircleDynamicList(false, true, newDynamicCount);

    }
}