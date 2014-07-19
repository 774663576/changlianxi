package com.changlianxi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.changlianxi.AddCircleMemberActivity;
import com.changlianxi.CircleGuideActivity;
import com.changlianxi.R;
import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.popwindow.HomeSearchLayerPopwindow;
import com.changlianxi.popwindow.HomeSearchLayerPopwindow.OnCancleClick;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.tab.fragment.MainTabActivity;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.CircleListTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ResolutionPushJson;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.BounceScrollView;
import com.changlianxi.view.BounceScrollView.OnRefreshComplete;
import com.changlianxi.view.GrowthImgGridView;

@SuppressLint("NewApi")
public class HomeFragMent extends Fragment implements OnClickListener,
        OnRefreshComplete, OnItemClickListener {
    private ImageView mMenu;
    private List<Circle> circleslists = new ArrayList<Circle>();
    private CircleList circleList = null;
    private GrowthImgGridView gView;
    private CircleAdapter adapter;
    private BounceScrollView scrollView;
    private EditText search;
    private ImageView imgPromte;
    private CircleListTask circleListTask;
    private Dialog dialog;
    private LinearLayout searchLayout;
    private RelativeLayout titleLayout;
    private RelativeLayout parentLayout;
    private HomeSearchLayerPopwindow popWindow;
    private OpenMyCardFragment callBack;
    private boolean isMoveing = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (circleslists.size() > 1) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                    adapter.setData(circleslists);
                    break;
                default:
                    break;
            }
        };
    };

    public interface OpenMyCardFragment {
        public void openMyCard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (callBack == null) {
            callBack = (OpenMyCardFragment) activity;
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
        parentLayout = (RelativeLayout) getView().findViewById(
                R.id.home_fragment);
        titleLayout = (RelativeLayout) getView().findViewById(R.id.title);
        mMenu = (ImageView) getActivity().findViewById(R.id.back);
        gView = (GrowthImgGridView) getView().findViewById(R.id.gridView1);
        gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        scrollView = (BounceScrollView) getView().findViewById(
                R.id.bounceScrollView);
        searchLayout = (LinearLayout) getView().findViewById(R.id.searchLayout);
        search = (EditText) getView().findViewById(R.id.search);
        imgPromte = (ImageView) getView().findViewById(R.id.imgNews);
        if (SharedUtils.getInt("loginType", 0) == 1) {
            imgPromte.setVisibility(View.VISIBLE);
        }
        getPrompt();
        setListener();

    }

    private void getPrompt() {
        Global g = new Global();
        g.read(DBUtils.getDBsa(1));
        if (g.getNewPersonChatNum() > 0) {
            setPromptVisible(View.VISIBLE);
        }
    }

    private void setListener() {
        scrollView.setOnRefreshComplete(this);
        mMenu.setOnClickListener(this);
        search.setOnClickListener(this);
        gView.setOnItemClickListener(this);
        gView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // return MotionEvent.ACTION_MOVE == event.getAction() ? true
                // : false;
                return false;
            }
        });
        setValue();
    }

    private void setValue() {
        circleList = new CircleList(circleslists);
        adapter = new CircleAdapter(getActivity(), null);
        gView.setAdapter(adapter);
        dialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        dialog.show();
        registerBoradcastReceiver();
        // SharedUtils.setInt("loginType", 1);
        filldata(true, true, true);

    }

    public void setPromptVisible(int visible) {
        imgPromte.setVisibility(visible);
    }

    private void showSearchPopWindow() {
        titleLayout.setVisibility(View.GONE);
        titleLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.up_out));
        popWindow = new HomeSearchLayerPopwindow(getActivity(), parentLayout);
        popWindow.setCallBack(new OnCancleClick() {

            @Override
            public void onCancle() {
                titleLayout.setVisibility(View.VISIBLE);
            }
        });
        popWindow.show();

    }

    /**
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFRESH_CIRCLE_LIST);
        myIntentFilter.addAction(Constants.EXIT_CIRCLE);
        myIntentFilter.addAction(Constants.UPDETE_CIRCLE_PROMPT_COUNT);
        myIntentFilter.addAction(Constants.KICKOUT_CIRCLE);
        myIntentFilter.addAction(Constants.REMOVE_CIRCLE_PROMPT_COUNT);
        myIntentFilter.addAction(Constants.ACCEPT_CIRCLE_INVITATE);
        myIntentFilter.addAction(Constants.REMOVE_INIT_CIRCLE);
        myIntentFilter.addAction(Constants.ADD_NEW_CIRCLE);

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
            if (action.equals(Constants.REFRESH_CIRCLE_LIST)) {// 更新圈子列表
                filldata(false, true, false);
            } else if (action.equals(Constants.EXIT_CIRCLE)) {// 退出圈子
                int cid = intent.getIntExtra("cid", 0);
                exitCircle(cid);
            } else if (action.equals(Constants.UPDETE_CIRCLE_PROMPT_COUNT)) { // 更新圈子提示数量
                int cid = intent.getIntExtra("cid", 0);
                String unread = intent.getStringExtra("unread");
                refushCirclePrompt(cid, unread);
            } else if (action.equals(Constants.KICKOUT_CIRCLE)) { // 踢出圈子
                int cid = intent.getIntExtra("cid", 0);
                kickOutCircle(cid);
            } else if (action.equals(Constants.REMOVE_CIRCLE_PROMPT_COUNT)) { // 减少圈子提示数量
                int cid = intent.getIntExtra("cid", 0);
                String type = intent.getStringExtra("type");
                removeCirclePrompt(cid, type);
            } else if (action.equals(Constants.ACCEPT_CIRCLE_INVITATE)) { // 接受圈子邀请
                int cid = intent.getIntExtra("cid", 0);
                acceptCircleInvitation(cid);
            } else if (action.equals(Constants.REMOVE_INIT_CIRCLE)) {
                int cid = intent.getIntExtra("cid", 0);
                remoreInitCircle(cid);
            } else if (action.equals(Constants.ADD_NEW_CIRCLE)) {
                Circle circle = (Circle) intent.getSerializableExtra("circle");
                circle.setNewGrowthCnt(1);
                circleslists.add(0, circle);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void remoreInitCircle(int cid) {
        for (int i = circleslists.size() - 1; i >= 0; i--) {
            if (circleslists.get(i).getId() == cid) {
                circleslists.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新提示数量
     * @param cid
     * @param type
     */
    private void refushCirclePrompt(int cid, String unread) {
        String[] arrayUnread = unread.split(",");
        for (Circle c : circleslists) {
            if (cid == c.getId()) {
                c.setNewMyDetailEditCnt(Integer.valueOf(arrayUnread[0]));
                c.setNewMemberCnt(Integer.valueOf(arrayUnread[1]));
                c.setNewGrowthCnt(Integer.valueOf(arrayUnread[2]));
                c.setNewGrowthCommentCnt(Integer.valueOf(arrayUnread[3]));
                c.setNewDynamicCnt(Integer.valueOf(arrayUnread[4]));
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     *  清除圈子提示数量
     * @param cid
     * @param type
     */
    private void removeCirclePrompt(int cid, String type) {
        for (Circle c : circleslists) {
            if (cid == c.getId()) {
                if (type.equals(ResolutionPushJson.GROWTH_TYPE)) {
                    c.setNewGrowthCnt(0);
                    c.setNewGrowthCommentCnt(0);
                } else if (type.equals(ResolutionPushJson.NEW_TYPE)) {
                    c.setNewDynamicCnt(0);
                } else if (type.equals(ResolutionPushJson.TYPE_MY_EDIT)) {
                    c.setNewMyDetailEditCnt(0);
                }
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filldata(boolean readDB, boolean refushNet,
            boolean refushANotify) {
        circleListTask = new CircleListTask(readDB, refushNet, refushANotify);
        circleListTask.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                addNewCircle();
                adapter.setData(circleslists);
            }

            @Override
            public void readDBFinish() {
                addNewCircle();
                mHandler.sendEmptyMessage(0);

            }

        });
        circleListTask.executeWithCheckNet(circleList);

    }

    private void addNewCircle() {
        int index = circleslists.size() - 1;
        if (index >= 0) {
            Circle circle = circleslists.get(index);
            if (circle.getId() != 0) {
                circleslists.add(getNewCircle());
            }
            return;
        } else {
            circleslists.add(getNewCircle());
        }
    }

    /**
     * 新建圈子
     * 
     * @return
     */
    private Circle getNewCircle() {
        Circle circle = new Circle(0);
        circle.setLogo("addroot");
        // circle.setJoinTime("1970-01-11 14:29:34");
        circle.setNew(false);
        circle.setName("添加圈子");
        return circle;
    }

    /***
     * 退出圈子
     * 
     * @param cirID
     */
    public void exitCircle(int cid) {
        for (int i = 0; i < circleslists.size(); i++) {
            if (circleslists.get(i).getId() == cid) {
                circleslists.remove(i);
                break;
            }
        }
        adapter.setData(circleslists);
    }

    private void acceptCircleInvitation(int cid) {
        for (Circle c : circleslists) {
            if (c.getId() == cid) {
                c.setNew(false);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 被踢出圈子
     * @param cid
     */
    public void kickOutCircle(int cid) {
        Circle c = new Circle(cid);
        c.setStatus(Status.DEL);
        c.write(DBUtils.getDBsa(2));
        for (int i = 0; i < circleslists.size(); i++) {
            if (circleslists.get(i).getId() == cid) {
                circleslists.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        System.out.println("onItemClickonItemClick");
        if (isMoveing) {
            return;
        }
        Intent intent;
        if (position == circleslists.size() - 1) {
            intent = new Intent();
            intent.setClass(getActivity(), AddCircleMemberActivity.class);
            intent.putExtra("type", "create");
            getActivity().startActivity(intent);
            Utils.leftOutRightIn(getActivity());
            return;
        }
        if (circleslists.get(position).getId() < 0) {
            intent = new Intent();
            intent.putExtra("cid", circleslists.get(position).getId());
            intent.setClass(getActivity(), CircleGuideActivity.class);
            getActivity().startActivity(intent);
            Utils.leftOutRightIn(getActivity());

            return;
        }
        intent = new Intent();
        intent.setClass(getActivity(), MainTabActivity.class);
        intent.putExtra("circleName", circleslists.get(position).getName());
        intent.putExtra("isNewCircle", circleslists.get(position).isNew());
        intent.putExtra("inviterID", circleslists.get(position).getMyInvitor());
        intent.putExtra("cid", circleslists.get(position).getId());
        intent.putExtra("newGrowthCount", circleslists.get(position)
                .getNewGrowthCnt());
        intent.putExtra("newCommentCount", circleslists.get(position)
                .getNewGrowthCommentCnt());
        intent.putExtra("newDynamicCount", circleslists.get(position)
                .getNewDynamicCnt());
        intent.putExtra("newMemberCount", circleslists.get(position)
                .getNewMemberCnt());
        intent.putExtra("newMyDetailEditCount", circleslists.get(position)
                .getNewMyDetailEditCnt());
        getActivity().startActivity(intent);
        Utils.leftOutRightIn(getActivity());
        if (circleslists.get(position).getNewMyDetailEditCnt() > 0) {
            removeCirclePrompt(circleslists.get(position).getId(),
                    ResolutionPushJson.TYPE_MY_EDIT);
        }
    }

    @Override
    public void onComplete() {
        searchLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((SlidingActivity) getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.search:
                showSearchPopWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
