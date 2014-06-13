package com.changlianxi;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Global;
import com.changlianxi.db.DBUtils;
import com.changlianxi.fragment.HomeFragMent;
import com.changlianxi.fragment.HomeFragMent.OpenMyCardFragment;
import com.changlianxi.fragment.LeftMenuFragMent;
import com.changlianxi.fragment.LeftMenuFragMent.ChangeFragMentListener;
import com.changlianxi.fragment.MessageListFragMent;
import com.changlianxi.fragment.MyCardFragMent;
import com.changlianxi.fragment.SettingFragMent;
import com.changlianxi.inteface.PushOnBind;
import com.changlianxi.inteface.SetLeftMenuPrompt;
import com.changlianxi.slidingmenu.lib.SlidingMenu;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.task.SetClientInfoTask;
import com.changlianxi.task.SetClientInfoTask.ClientCallBack;
import com.changlianxi.task.UpDateNewVersionTask;
import com.changlianxi.task.UpDateNewVersionTask.UpDateVersion;
import com.changlianxi.util.BaiDuPushUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.MyPushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends SlidingActivity implements
        ChangeFragMentListener, SetLeftMenuPrompt, PushOnBind,
        OpenMyCardFragment {
    private LeftMenuFragMent menuFragMent = null;
    private HomeFragMent homeFragMent = null;
    private MyCardFragMent myCardFragMent = null;
    private MessageListFragMent messageListFragMent = null;
    private SettingFragMent setFragMent = null;
    private FragmentManager manager;
    private int currentFramentIndex;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    changeFrag((Integer) msg.obj);
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        CLXApplication.addActivity(this);
        setContentView(R.layout.activity_main);
        initSlidingMenu();
        initFragmentTransaction();
        registerBoradcastReceiver();
        bindBaiDuPush();
        checkVersion();
    }

    private void initSlidingMenu() {
        setBehindContentView(R.layout.left_menu); // 设置菜单页
        SlidingMenu sm = getSlidingMenu(); // 滑动菜单
        sm.setBehindOffset(Utils.getSecreenWidth(this) / 2); // 菜单与边框的距离
        sm.setFadeDegree(0.35f); // 色度
        sm.setShadowDrawable(R.drawable.shadow); // 滑动菜单渐变
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); // 边缘滑动菜单
    }

    private void initFragmentTransaction() {
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        menuFragMent = new LeftMenuFragMent();
        homeFragMent = new HomeFragMent();
        transaction.add(R.id.left_menu, menuFragMent);
        transaction.add(R.id.main_rl, homeFragMent);
        transaction.commit();
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
                DialogUtil.newVersion(MainActivity.this, versionCode, link);
            }
        });
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            task.execute();
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
    }

    @Override
    public void onChangeFragMent(int position) {
        toggle();
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        msg.obj = position;
        mHandler.sendMessageDelayed(msg, 300);
    }

    private void changeFrag(int position) {
        FragmentTransaction transaction = manager.beginTransaction();
        showCurrentFragment(position, transaction);
        hideOtherFragment(position, transaction);
        transaction.commit();
        currentFramentIndex = position;

    }

    private void showCurrentFragment(int position,
            FragmentTransaction transaction) {
        switch (position) {
            case 0:
                transaction.show(homeFragMent);
                break;
            case 1:
                if (myCardFragMent == null) {
                    myCardFragMent = new MyCardFragMent();
                    transaction.add(R.id.main_rl, myCardFragMent);
                } else {
                    transaction.show(myCardFragMent);
                }
                break;
            case 2:
                if (messageListFragMent == null) {
                    messageListFragMent = new MessageListFragMent();
                    transaction.add(R.id.main_rl, messageListFragMent);
                } else {
                    transaction.show(messageListFragMent);
                    messageListFragMent.refush();

                }
                break;
            case 3:
                if (setFragMent == null) {
                    setFragMent = new SettingFragMent();
                    transaction.add(R.id.main_rl, setFragMent);
                } else {
                    transaction.show(setFragMent);
                }
                break;
            default:
                break;
        }
    }

    private void hideOtherFragment(int position, FragmentTransaction transaction) {
        switch (position) {
            case 0:
                if (myCardFragMent != null) {
                    transaction.hide(myCardFragMent);
                }
                if (setFragMent != null) {
                    transaction.hide(setFragMent);
                }
                if (messageListFragMent != null) {
                    transaction.hide(messageListFragMent);
                }
                break;
            case 1:
                if (homeFragMent != null) {
                    transaction.hide(homeFragMent);
                }
                if (setFragMent != null) {
                    transaction.hide(setFragMent);
                }
                if (messageListFragMent != null) {
                    transaction.hide(messageListFragMent);
                }
                break;
            case 2:
                if (homeFragMent != null) {
                    transaction.hide(homeFragMent);
                }
                if (setFragMent != null) {
                    transaction.hide(setFragMent);
                }
                if (myCardFragMent != null) {
                    transaction.hide(myCardFragMent);
                }
                break;
            case 3:
                if (homeFragMent != null) {
                    transaction.hide(homeFragMent);
                }
                if (messageListFragMent != null) {
                    transaction.hide(messageListFragMent);
                }
                if (myCardFragMent != null) {
                    transaction.hide(myCardFragMent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFramentIndex != 0) {
            if (!getSlidingMenu().isMenuShowing()) {
                toggle();
            } else {
                changeFrag(0);
                toggle();
                menuFragMent.setCurrentMenu(0);
            }
            return;
        }
        if (getSlidingMenu().isMenuShowing()) {
            toggle();
            return;
        }
        SharedUtils.setInt("loginType", 2);
        unregisterReceiver(mBroadcastReceiver);
        CLXApplication.exit(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        SharedUtils.setInt("loginType", 2);
        super.onDestroy();
    }

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

    /**
    * 注册该广播
    */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.LEFT_MENU_MESSAGE_PROMPT);
        myIntentFilter.addAction(Constants.REFUSH_MYCARD_FRMO_NET);

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
            if (action.equals(Constants.LEFT_MENU_MESSAGE_PROMPT)) {
                menuFragMent.setMenuPrompt(2, true);
                homeFragMent.setPromptVisible(View.VISIBLE);
            } else if (action.equals(Constants.REFUSH_MYCARD_FRMO_NET)) {
                boolean isFefushMycardFragment = intent.getBooleanExtra(
                        "isFefushMycardFragment", false);
                menuFragMent.getMyCard(isFefushMycardFragment);
            }
        }
    };

    @Override
    public void setMenuPrompt(int positon, boolean flag) {
        menuFragMent.setMenuPrompt(positon, flag);
        if (!flag) {
            Global g = new Global();
            g.read(DBUtils.getDBsa(1));
            if (g.getNewPersonChatNum() == 0) {
                homeFragMent.setPromptVisible(View.INVISIBLE);
            }
        }
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 百度推送绑定回调
     */
    @Override
    public void onBind(String channel_id, String user_id) {
        SharedUtils.setString(SharedUtils.SP_BPUSH_CHANNEL_ID, channel_id);
        SharedUtils.setString(SharedUtils.SP_BPUSH_USER_ID, user_id);
        SetClientInfoTask task = new SetClientInfoTask(new ClientCallBack() {//
                    // 设置属性接口回调
                    public void afterLogin(String result) {
                        isSetSuccess(result);
                    }
                });
        task.execute();
    }

    @Override
    public void openMyCard() {
        menuFragMent.setCurrentMenu(1);
        FragmentTransaction transaction = manager.beginTransaction();
        showCurrentFragment(1, transaction);
        hideOtherFragment(1, transaction);
        transaction.commit();
        currentFramentIndex = 1;
    }
}
