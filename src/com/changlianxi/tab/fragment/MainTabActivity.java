package com.changlianxi.tab.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.ResolutionPushJson;
import com.changlianxi.util.Utils;

public class MainTabActivity extends FragmentActivity implements
        OnClickListener {
    private FragmentManager manager;
    private MemberFragment tabMember;
    private GrowthAndAlbumFragment tabGrowth;
    private DynamicFragment tabDynamic;
    private NomalCircleInfoFragment tabCircleInfo;
    private String ciecleName;// 圈子名称
    private boolean isNewCircle;
    private int inviterID;
    private int cid;
    private int newGrowthCount = 0;// 新成长数、
    private int newDynamicCount = 0;// 新动态数、
    private int newCommentCount = 0;// 新评论数。
    private int newMemberCount = 0;// 新成员数
    private int newMyDetailEditCount = 0;// 我的资料修改数
    private RelativeLayout layoutMember;
    private RelativeLayout layoutGrowth;
    private RelativeLayout layoutDynamic;
    private RelativeLayout layoutCircleInfo;
    private ImageView imgMember;
    private ImageView imgGrowth;
    private ImageView imgDynamic;
    private ImageView imgCircleInfo;
    private TextView num_no_read_growth;
    private TextView num_no_read_dynamic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab_activity1);
        getActivityData();
        initFragmentTransaction();
        initView();
        registerBoradcastReceiver();
    }

    private void getActivityData() {
        cid = getIntent().getIntExtra("cid", 0);
        newGrowthCount = getIntent().getIntExtra("newGrowthCount", 0);
        newDynamicCount = getIntent().getIntExtra("newDynamicCount", 0);
        newCommentCount = getIntent().getIntExtra("newCommentCount", 0);
        newMemberCount = getIntent().getIntExtra("newMemberCount", 0);
        newMyDetailEditCount = getIntent().getIntExtra("newMyDetailEditCount",
                0);
        ciecleName = getIntent().getStringExtra("circleName");
        isNewCircle = getIntent().getBooleanExtra("isNewCircle", false);
        inviterID = getIntent().getIntExtra("inviterID", 0);
    }

    private void initView() {
        layoutMember = (RelativeLayout) findViewById(R.id.tab_member);
        layoutGrowth = (RelativeLayout) findViewById(R.id.tab_growth);
        layoutCircleInfo = (RelativeLayout) findViewById(R.id.tab_circleInfo);
        layoutDynamic = (RelativeLayout) findViewById(R.id.tab_Dynamic);
        imgMember = (ImageView) findViewById(R.id.imgMember);
        imgGrowth = (ImageView) findViewById(R.id.imgGrowth);
        imgDynamic = (ImageView) findViewById(R.id.imgDynamic);
        imgCircleInfo = (ImageView) findViewById(R.id.imgCircleInfo);
        num_no_read_dynamic = (TextView) findViewById(R.id.un_read_num_dynamic);
        num_no_read_growth = (TextView) findViewById(R.id.un_read_num_growth);
        setListener();
    }

    private void setListener() {
        layoutGrowth.setOnClickListener(this);
        layoutMember.setOnClickListener(this);
        layoutCircleInfo.setOnClickListener(this);
        layoutDynamic.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        imgCircleInfo.setEnabled(false);
        imgGrowth.setEnabled(false);
        imgDynamic.setEnabled(false);
        int nu = newCommentCount + newGrowthCount;
        if (nu > 0) {
            num_no_read_growth.setText(nu + "");
            num_no_read_growth.setVisibility(View.VISIBLE);
        }
        if (newDynamicCount > 0) {
            num_no_read_dynamic.setText(newDynamicCount + "");
            num_no_read_dynamic.setVisibility(View.VISIBLE);
        }

    }

    private void initFragmentTransaction() {
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        tabMember = new MemberFragment();
        tabMember.setArguments(getMemberBundle());
        transaction.add(R.id.main_layout, tabMember);
        transaction.commit();
    }

    private Bundle getMemberBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("inviterID", inviterID);
        bundle.putBoolean("isNewCircle", isNewCircle);
        bundle.putString("circleName", ciecleName);
        bundle.putInt("newMemberCount", newMemberCount);
        bundle.putInt("newMyDetailEditCount", newMyDetailEditCount);
        return bundle;
    }

    private Bundle getGrowthBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("newGrowthCount", newGrowthCount);
        bundle.putInt("newCommentCount", newCommentCount);
        return bundle;
    }

    private Bundle getDynamicBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("newDynamicCount", newDynamicCount);
        bundle.putString("circleName", ciecleName);
        return bundle;
    }

    private Bundle getCircleInfoBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        return bundle;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.tab_member) {
            CircleMember self = new CircleMember(cid, 0, Global.getIntUid());
            self.getMemberState(DBUtils.getDBsa(1));
            if (self.getState().equals(CircleMemberState.STATUS_INVITING)) {
                Utils.showToast("亲，加入圈子以后才能看到这些精彩内容哦！", Toast.LENGTH_SHORT);
                return;
            }
        }
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(v.getId(), transaction);
        showCurrentFragment(v.getId(), transaction);
        transaction.commit();
    }

    private void showCurrentFragment(int id, FragmentTransaction transaction) {
        switch (id) {
            case R.id.tab_member:
                transaction.show(tabMember);
                imgMember.setEnabled(true);
                imgCircleInfo.setEnabled(false);
                imgGrowth.setEnabled(false);
                imgDynamic.setEnabled(false);
                break;
            case R.id.tab_growth:
                if (tabGrowth == null) {
                    tabGrowth = new GrowthAndAlbumFragment();
                    tabGrowth.setArguments(getGrowthBundle());
                    transaction.add(R.id.main_layout, tabGrowth);
                } else {
                    transaction.show(tabGrowth);
                }
                imgMember.setEnabled(false);
                imgCircleInfo.setEnabled(false);
                imgGrowth.setEnabled(true);
                imgDynamic.setEnabled(false);
                num_no_read_growth.setVisibility(View.INVISIBLE);
                sendBroadcast(ResolutionPushJson.GROWTH_TYPE);
                break;
            case R.id.tab_Dynamic:
                if (tabDynamic == null) {
                    tabDynamic = new DynamicFragment();
                    tabDynamic.setArguments(getDynamicBundle());
                    transaction.add(R.id.main_layout, tabDynamic);
                } else {
                    transaction.show(tabDynamic);
                }
                imgMember.setEnabled(false);
                imgCircleInfo.setEnabled(false);
                imgGrowth.setEnabled(false);
                imgDynamic.setEnabled(true);
                num_no_read_dynamic.setVisibility(View.INVISIBLE);
                sendBroadcast(ResolutionPushJson.NEW_TYPE);
                break;
            case R.id.tab_circleInfo:
                if (tabCircleInfo == null) {
                    tabCircleInfo = new NomalCircleInfoFragment();
                    tabCircleInfo.setArguments(getCircleInfoBundle());
                    transaction.add(R.id.main_layout, tabCircleInfo);
                } else {
                    transaction.show(tabCircleInfo);
                }
                imgMember.setEnabled(false);
                imgCircleInfo.setEnabled(true);
                imgGrowth.setEnabled(false);
                imgDynamic.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void hideFragment(int id, FragmentTransaction transaction) {
        switch (id) {
            case R.id.tab_member:
                if (tabGrowth != null) {
                    transaction.hide(tabGrowth);
                }
                if (tabDynamic != null) {
                    transaction.hide(tabDynamic);
                }
                if (tabCircleInfo != null) {
                    transaction.hide(tabCircleInfo);
                }
                break;
            case R.id.tab_growth:
                if (tabMember != null) {
                    transaction.hide(tabMember);
                }
                if (tabDynamic != null) {
                    transaction.hide(tabDynamic);
                }
                if (tabCircleInfo != null) {
                    transaction.hide(tabCircleInfo);
                }
                break;
            case R.id.tab_Dynamic:
                if (tabMember != null) {
                    transaction.hide(tabMember);
                }
                if (tabGrowth != null) {
                    transaction.hide(tabGrowth);
                }
                if (tabCircleInfo != null) {
                    transaction.hide(tabCircleInfo);
                }
                break;
            case R.id.tab_circleInfo:
                if (tabMember != null) {
                    transaction.hide(tabMember);
                }
                if (tabDynamic != null) {
                    transaction.hide(tabDynamic);
                }
                if (tabGrowth != null) {
                    transaction.hide(tabGrowth);
                }
                break;
            default:
                break;
        }
    }

    public void sendBroadcast(String type) {
        Intent intent = new Intent(Constants.REMOVE_CIRCLE_PROMPT_COUNT);
        intent.putExtra("type", type);
        intent.putExtra("cid", cid);
        BroadCast.sendBroadCast(this, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!imgMember.isEnabled()) {
                FragmentTransaction transaction = manager.beginTransaction();
                hideFragment(R.id.tab_member, transaction);
                showCurrentFragment(R.id.tab_member, transaction);
                transaction.commit();
            } else {
                finish();
                Utils.rightOut(this);
            }
        }
        return false;
    }

    /**
     * 注册该广播
    */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.CHANGE_TAB);

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
            if (action.equals(Constants.CHANGE_TAB)) {
                FragmentTransaction transaction = manager.beginTransaction();
                hideFragment(R.id.tab_member, transaction);
                showCurrentFragment(R.id.tab_member, transaction);
                transaction.commit();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
                || requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA
                || requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
                || requestCode == Constants.EDIT_CIRCL) {
            if (tabCircleInfo != null) {
                tabCircleInfo.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
