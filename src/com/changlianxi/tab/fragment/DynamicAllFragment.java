package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.adapter.DynamicAllAdapter;
import com.changlianxi.data.CircleDynamic;
import com.changlianxi.data.CircleDynamicList;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.CircleDynamicTask;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

public class DynamicAllFragment extends Fragment implements OnPullDownListener {
    private int cid = 0;
    private int newDynamicCount = 0;
    private DynamicAllAdapter adapter;
    private PullDownView mPullDownView;
    private ListView mListView;
    private CircleDynamicList dynamicList;
    private List<CircleDynamic> dynamics = new ArrayList<CircleDynamic>();
    private TextView txt;
    private ImageView line;
    private boolean isAuth = true;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dynamics = dynamicList.getDynamics();
                    adapter.setData(dynamics);
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
                    break;

                default:
                    break;
            }
        }
    };

    public static DynamicAllFragment newInstance(int cid, int newDynamicCount) {
        DynamicAllFragment f = new DynamicAllFragment();
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
            rootView = inflater.inflate(R.layout.dynamic_all, null);
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
        line = (ImageView) getView().findViewById(R.id.imgLine);
        txt = (TextView) getView().findViewById(R.id.noDynamic);
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.PullDownlistView);
        mPullDownView.addFooterView();
        adapter = new DynamicAllAdapter(getActivity(), dynamics);
        mListView = mPullDownView.getListView();
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if (isAuth()) {
            line.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            isAuth = false;
            return;
        }
        setListener();
    }

    private void setListener() {
        mPullDownView.setOnPullDownListener(this);
        mListView.setAdapter(adapter);
        setValue();
    }

    private void setValue() {
        dynamicList = new CircleDynamicList(cid, false);
        getCircleDynamicList(true, false, newDynamicCount);

    }

    /**
     * 获取动态列表
     */
    private void getCircleDynamicList(boolean readDB, boolean isMore,
            int newDynamicCount) {
        CircleDynamicTask task = new CircleDynamicTask(readDB, isMore,
                newDynamicCount);
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
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

    public boolean isAuth() {
        CircleMember c = new CircleMember(cid, 0, Global.getIntUid());
        c.getMemberState(DBUtils.getDBsa(1));
        if (c.getState() == CircleMemberState.STATUS_ENTER_AND_VERIFYING) {
            return true;
        }
        return false;
    }

    /** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
    @Override
    public void onRefresh() {
        if (!isAuth) {
            mPullDownView.RefreshComplete();
            return;
        }
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