package com.changlianxi.tab.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.ResolutionPushJson;
import com.changlianxi.util.Utils;

public class MainTabActivity extends FragmentActivity implements
        OnTabChangeListener {
    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private Class fragmentArray[] = { MemberFragment.class,
            GrowthAndAlbumFragment.class, DynamicFragment.class,
            CircleInfoFragement.class };
    private String mTextviewArray[] = { "成员", "成长", "动态", "更多" };
    private int mImageViewArray[] = { R.drawable.tab_member,
            R.drawable.tab_growth, R.drawable.tab_dynamic,
            R.drawable.tab_setting };
    private String ciecleName;// 圈子名称
    private boolean isNewCircle;
    private int inviterID;
    private int cid;
    private int newGrowthCount = 0;// 新成长数、
    private int newDynamicCount = 0;// 新动态数、
    private int newCommentCount = 0;// 新评论数。
    private int newMemberCount = 0;// 新成员数
    private int newMyDetailEditCount = 0;// 我的资料修改数

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        CLXApplication.addActivity(this);
        getActivityData();
        initView();
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
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        addTab();
        // mTabHost.setOnTabChangedListener(this);

    }

    private void addTab() {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("inviterID", inviterID);
        bundle.putBoolean("isNewCircle", isNewCircle);
        bundle.putString("circleName", ciecleName);
        bundle.putInt("newMemberCount", newMemberCount);
        bundle.putInt("newMyDetailEditCount", newMyDetailEditCount);
        TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[0]).setIndicator(
                getTabItemView(0));
        mTabHost.addTab(tabSpec, fragmentArray[0], bundle);
        bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("newGrowthCount", newGrowthCount);
        bundle.putInt("newCommentCount", newCommentCount);
        tabSpec = mTabHost.newTabSpec(mTextviewArray[1]).setIndicator(
                getTabItemView(1));
        mTabHost.addTab(tabSpec, fragmentArray[1], bundle);
        bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("newDynamicCount", newDynamicCount);
        bundle.putString("circleName", ciecleName);
        tabSpec = mTabHost.newTabSpec(mTextviewArray[2]).setIndicator(
                getTabItemView(2));
        mTabHost.addTab(tabSpec, fragmentArray[2], bundle);
        bundle = new Bundle();
        bundle.putInt("cid", cid);
        tabSpec = mTabHost.newTabSpec(mTextviewArray[3]).setIndicator(
                getTabItemView(3));
        mTabHost.addTab(tabSpec, fragmentArray[3], bundle);
        mTabHost.setOnTabChangedListener(this);
    }

    /**
    * 给Tab按钮设置图标和文字
    */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        TextView num = (TextView) view.findViewById(R.id.un_read_num);
        switch (index) {
            case 0:
                break;
            case 1:
                int nu = newCommentCount + newGrowthCount;
                if (nu > 0) {
                    num.setText(nu + "");
                    num.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (newDynamicCount > 0) {
                    num.setText(newDynamicCount + "");
                    num.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        View v = mTabHost.getCurrentTabView();
        TextView numTxt = (TextView) v.findViewById(R.id.un_read_num);
        numTxt.setVisibility(View.INVISIBLE);
        int currentItem = mTabHost.getCurrentTab();
        if (currentItem == 1) {
            Intent intent = new Intent(Constants.REMOVE_CIRCLE_PROMPT_COUNT);
            intent.putExtra("type", ResolutionPushJson.GROWTH_TYPE);
            intent.putExtra("cid", cid);
            BroadCast.sendBroadCast(this, intent);
        } else if (currentItem == 2) {
            Intent intent = new Intent(Constants.REMOVE_CIRCLE_PROMPT_COUNT);
            intent.putExtra("type", ResolutionPushJson.NEW_TYPE);
            intent.putExtra("cid", cid);
            BroadCast.sendBroadCast(this, intent);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Utils.rightOut(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
