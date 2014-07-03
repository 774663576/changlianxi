package com.changlianxi;

import java.io.Serializable;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.GrowthMonthAlbumAdapter;
import com.changlianxi.chooseImage.PhotoInfo;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbum;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.GetGrowthAlbumMonthTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.PullDownViewGrowth;
import com.changlianxi.view.PullDownViewGrowth.OnPullDownListener;
import com.umeng.analytics.MobclickAgent;

public class AlumMonthActivity extends BaseActivity implements OnClickListener,
        OnPullDownListener, OnScrollListener {
    private ImageView back;
    private TextView title;
    private ImageView rightImg;
    private int cid;
    private GrowthAlbumList albumList;
    private List<GrowthAlbum> album = new ArrayList<GrowthAlbum>();
    private List<Growth> growthList = new ArrayList<Growth>();
    private ListView listview;
    private PullDownViewGrowth mPullDownView;
    private GrowthMonthAlbumAdapter adapter;
    private Dialog dialog;
    private int startY;
    private int endY;
    private int startM;
    private int endM;
    private String releaseImgPath = "";
    private SelectPicPopwindow pop = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    album = albumList.getAlbum();
                    growthList = albumList.getGrowthList();
                    if (album.size() > 0) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
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
        setContentView(R.layout.activity_alum_month);
        cid = getIntent().getIntExtra("cid", 0);
        startY = getIntent().getIntExtra("startY", 0);
        startM = getIntent().getIntExtra("startM", 0);
        endY = startY;
        endM = startM;
        albumList = new GrowthAlbumList(cid, album);
        adapter = new GrowthMonthAlbumAdapter(this, album, growthList, cid);
        initView();
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        getServerData(1, 31, 0, 0, false);
        registerBoradcastReceiver();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        rightImg = (ImageView) findViewById(R.id.rightImg);
        back.setOnClickListener(this);
        rightImg.setOnClickListener(this);
        title.setText("按日浏览");
        title.setGravity(Gravity.CENTER);
        rightImg.setImageResource(R.drawable.icon_camera);
        rightImg.setVisibility(View.VISIBLE);
        mPullDownView = (PullDownViewGrowth) findViewById(R.id.PullDownlistView);
        listview = mPullDownView.getListView();
        listview.setAdapter(adapter);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mPullDownView.setOnPullDownListener(this);
        mPullDownView.notifyDidMore();
        mPullDownView.setShowFooter();
        mPullDownView.setFooterVisible(false);
        listview.setOnScrollListener(this);
    }

    private void getServerData(int startDay, int endDay, int startTime,
            int endTime, boolean isMore) {
        GetGrowthAlbumMonthTask task = new GetGrowthAlbumMonthTask(albumList,
                startY, endY, startM, endM, startDay, endDay, startTime,
                endTime, isMore);
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
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.FINISH_DAY_ALBUM);
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
            if (action.equals(Constants.FINISH_DAY_ALBUM)) {
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    };

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.rightImg:
                pop = new SelectPicPopwindow(this, v, cid, 0);
                pop.show();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<PhotoInfo> list = new ArrayList<PhotoInfo>();
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (resultCode != RESULT_OK) {
                return;
            }
            releaseImgPath = pop.getTakePhotoPath();
            PhotoInfo m = new PhotoInfo();
            m.setPath_absolute(releaseImgPath);
            list.add(m);
            intentReleaseGrowthActivity(list);

        } else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD_MORE) {
            if (resultCode != RESULT_OK) {
                return;
            }
            list = (List<PhotoInfo>) data.getExtras()
                    .getSerializable("imgPath");
            intentReleaseGrowthActivity(list);
        }

    }

    private void intentReleaseGrowthActivity(List<PhotoInfo> list) {
        Intent it = new Intent();
        it.setClass(this, ReleaseGrowthActivity.class);
        it.putExtra("cid", cid);
        Bundle bundle = new Bundle();
        bundle.putSerializable("imgPath", (Serializable) list);
        it.putExtras(bundle);
        startActivityForResult(it, 2);
        Utils.leftOutRightIn(this);
    }

    @Override
    public void onRefresh() {
        if (album.size() == 0) {
            mPullDownView.RefreshComplete();
            return;
        }
        String month = DateUtils.getYear(album.get(0).getAlbumDate(), "MM");
        if ("".equals(month)) {
            month = "0";
        }
        int intMonth = Integer.valueOf(month);
        if (startM == intMonth) {
            startM = startM + 1;
            endM = startM;
        }
        getServerData(1, 31, 0, 0, false);
    }

    @Override
    public void onMore() {
        String month = DateUtils.getYear(album.get(album.size() - 1)
                .getAlbumDate(), "MM");
        if ("".equals(month)) {
            month = "0";
        }
        int intMonth = Integer.valueOf(month);
        if (startM != intMonth) {
            startM = getIntent().getIntExtra("startM", 0);
            endM = startM;
        }
        String day = DateUtils.getYear(album.get(album.size() - 1)
                .getAlbumDate(), "dd");
        if ("".equals(day)) {
            day = "0";
        }
        int intDay = Integer.valueOf(day);
        List<GrowthAlbumImages> pic = album.get(album.size() - 1).getPics();
        int endTime = Integer.valueOf(DateUtils.phpTime(DateUtils
                .convertToDate(pic.get(pic.size() - 1).getPicHappened())));
        if (intDay - 1 == 0) {
            startM = startM - 1;
            endM = startM;
            intDay = 32;
        }
        getServerData(1, intDay - 1, 0, endTime - 1, true);

    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        mPullDownView.setFirstItemIndex(firstVisibleItem);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    };

}
