package com.changlianxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.PushOnBind;
import com.changlianxi.popwindow.CircleHomeMenuPopWindow;
import com.changlianxi.tab.fragment.MainTabActivity;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.CircleListTask;
import com.changlianxi.task.MyCardTask;
import com.changlianxi.task.SetCirclesSequenceTask;
import com.changlianxi.task.SetClientInfoTask;
import com.changlianxi.task.SetClientInfoTask.ClientCallBack;
import com.changlianxi.task.UpDateNewVersionTask;
import com.changlianxi.task.UpDateNewVersionTask.UpDateVersion;
import com.changlianxi.util.BaiDuPushUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.MyPushMessageReceiver;
import com.changlianxi.util.ResolutionPushJson;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.DragGridView;
import com.changlianxi.view.DragGridView.OnChanageListener;

public class CircleHomeActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, PushOnBind, OnChanageListener {
    private ImageView imgSearch;
    private ImageView imgAdd;
    private ImageView imgMenu;
    private DragGridView circleGridView;
    private List<Circle> circleslists = new ArrayList<Circle>();
    private CircleList circleList = null;
    private CircleListTask circleListTask;
    private Dialog dialog;
    private CircleAdapter adapter;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_home);
        initView();
        setValue();
        registerBoradcastReceiver();
        bindBaiDuPush();
        checkVersion();
        getMyCard();
    }

    private void initView() {
        circleGridView = (DragGridView) findViewById(R.id.circleGridView);
        circleGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        imgMenu = (ImageView) findViewById(R.id.imgMenu);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        setListener();
    }

    private void setListener() {
        imgAdd.setOnClickListener(this);
        imgMenu.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        circleGridView.setOnItemClickListener(this);
        circleGridView.setOnChangeListener(this);

    }

    private void setValue() {
        circleList = new CircleList(circleslists);
        adapter = new CircleAdapter(this, null);
        circleGridView.setAdapter(adapter);
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        getCircleLists(true, true, true);
    }

    private void bindBaiDuPush() {
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                BaiDuPushUtils.getMetaValue(this, "api_key"));
        MyPushMessageReceiver.setPushOnBind(this);

    }

    @SuppressLint("NewApi")
    private void checkVersion() {
        UpDateNewVersionTask task = new UpDateNewVersionTask(this, false);
        task.setCallBack(new UpDateVersion() {
            @Override
            public void getNewVersion(String rt, String versionCode, String link) {
                if (!"1".equals(rt)) {
                    return;
                }
                DialogUtil.newVersion(CircleHomeActivity.this, versionCode,
                        link);
            }
        });
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            task.execute();
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
    }

    public void getMyCard() {
        MyCard myCard = new MyCard(0, Global.getIntUid());
        MyCardTask task = new MyCardTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(myCard);

    }

    private void getCircleLists(boolean readDB, boolean refushNet,
            boolean refushANotify) {
        circleListTask = new CircleListTask(readDB, refushNet, refushANotify);
        circleListTask.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                adapter.setData(circleslists);
            }

            @Override
            public void readDBFinish() {
                mHandler.sendEmptyMessage(0);
            }

        });
        circleListTask.executeWithCheckNet(circleList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAdd:
                Intent intent = new Intent();
                intent.setClass(this, AddCircleMemberActivity.class);
                intent.putExtra("type", "create");
                startActivity(intent);
                break;
            case R.id.imgMenu:
                new CircleHomeMenuPopWindow(this, v).show();
                break;
            case R.id.imgSearch:
                startActivity(new Intent(this, CircleHomeSearchActivity.class));
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedUtils.setInt("loginType", 2);
            unregisterReceiver(mBroadcastReceiver);
            CLXApplication.exit(true);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedUtils.setInt("loginType", 2);
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent intent;
        if (circleslists.get(position).getId() < 0) {
            intent = new Intent();
            intent.putExtra("cid", circleslists.get(position).getId());
            intent.setClass(this, CircleGuideActivity.class);
            this.startActivity(intent);
            Utils.leftOutRightIn(this);

            return;
        }
        intent = new Intent();
        intent.setClass(this, MainTabActivity.class);
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
        this.startActivity(intent);
        Utils.leftOutRightIn(this);
        if (circleslists.get(position).getNewMyDetailEditCnt() > 0) {
            // removeCirclePrompt(circleslists.get(position).getId(),
            // ResolutionPushJson.TYPE_MY_EDIT);
        }
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
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 定义广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.REFRESH_CIRCLE_LIST)) {// 更新圈子列表
                getCircleLists(false, true, false);
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

    /**
    * 百度推送绑定回调
    */
    @Override
    public void onBind(String channel_id, String user_id) {
        SharedUtils.setString(SharedUtils.SP_BPUSH_CHANNEL_ID, channel_id);
        SharedUtils.setString(SharedUtils.SP_BPUSH_USER_ID, user_id);
        if (SharedUtils.getBoolean("isLogin", false)) {
            return;
        }
        SetClientInfoTask task = new SetClientInfoTask(new ClientCallBack() {//
                    // 设置属性接口回调
                    public void afterLogin(String result) {
                        isSetSuccess(result);
                    }
                });
        task.execute();
    }

    /**
    * 属性是否设置成功
    */
    private void isSetSuccess(String result) {
        try {
            JSONObject json = new JSONObject(result);
            int rt = json.getInt("rt");
            if (rt != 1) {
                String err = json.getString("err");
                String errorString = ErrorCodeUtil.convertToChines(err);
                Utils.showToast(errorString, Toast.LENGTH_SHORT);
                return;
            }
            SharedUtils.setBoolean("isLogin", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onChange(int from, int to) {
        Circle temp = circleslists.get(from);
        // 这里的处理需要注意下
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(circleslists, i, i + 1);
            }
        } else if (from > to) {
            for (int i = from; i > to; i--) {
                Collections.swap(circleslists, i, i - 1);
            }
        }
        circleslists.set(to, temp);
        // 设置新到的item隐藏，
        adapter.setItemHide(to);
         StringBuffer ids = new StringBuffer();
         for (Circle c : circleslists) {
         ids.append(c.getId() + ",");
         }
         setSequences(ids.toString());
    }

    private void setSequences(String sequence) {
        SetCirclesSequenceTask task = new SetCirclesSequenceTask(sequence);
        task.isPrompt = false;
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(circleList);
    }
}
