package com.changlianxi.showBigPic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.GrowthCommentActivity;
import com.changlianxi.R;
import com.changlianxi.ShareActivity;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.showBigPic.ImageDetailFragment.OnBack;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.HackyViewPager;
import com.umeng.analytics.MobclickAgent;

public class GrowthImagePagerActivity extends FragmentActivity implements
        OnClickListener, OnBack {
    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private List<GrowthAlbumImages> lists = new ArrayList<GrowthAlbumImages>();
    private ImagePagerAdapter mAdapter;
    private TextView time;
    private TextView location;
    private TextView praise;
    private TextView comment;
    private Growth growth;
    private TextView share;
    private int cid;
    private boolean tasking = false;
    private boolean isPraise = false;
    private ImageView back;
    private LinearLayout title;
    private LinearLayout layPraise;
    private boolean isAuth = true;
    private LinearLayout layComments;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_detail_pager);
        pagerPosition = getIntent().getIntExtra(Constants.EXTRA_IMAGE_INDEX, 0);
        lists = (List<GrowthAlbumImages>) getIntent().getExtras()
                .getSerializable(Constants.EXTRA_IMAGE_URLS);
        growth = (Growth) getIntent().getExtras().get("growth");
        cid = getIntent().getIntExtra("cid", 0);
        initView();
        mPager = (HackyViewPager) findViewById(R.id.pager);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), lists);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        setValue();

        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                pagerPosition = arg0;
                setValue();

            }
        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(Constants.STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
        CircleMember m = new CircleMember(cid, 0, Global.getIntUid());
        if (!m.isAuth(DBUtils.getDBsa(1))) {
            isAuth = false;
        }
    }

    private void initView() {
        time = (TextView) findViewById(R.id.time);
        location = (TextView) findViewById(R.id.location);
        praise = (TextView) findViewById(R.id.praise);
        comment = (TextView) findViewById(R.id.comment);
        share = (TextView) findViewById(R.id.share);
        share.setOnClickListener(this);
        praise.setOnClickListener(this);
        layPraise = (LinearLayout) findViewById(R.id.layPraise);
        title = (LinearLayout) findViewById(R.id.layTitle);
        back = (ImageView) findViewById(R.id.imgBack);
        back.setOnClickListener(this);
        layComments = (LinearLayout) findViewById(R.id.layoutComments);
        layComments.setOnClickListener(this);

    }

    private void setValue() {
        time.setText(DateUtils.getGrowthShowTime(growth.getHappened()));
        location.setText(growth.getLocation());
        if ("".equals(growth.getLocation())) {
            location.setVisibility(View.INVISIBLE);
        } else {
            location.setVisibility(View.VISIBLE);
        }
        praise.setText(growth.getPraiseCnt() + "");
        comment.setText(growth.getCommentCnt() + "");
        Drawable drawable = null;
        if (growth.isPraised()) {
            drawable = getResources().getDrawable(R.drawable.icon_praise1);
        } else {
            drawable = getResources().getDrawable(R.drawable.icon_praise);
        } // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        praise.setCompoundDrawables(drawable, null, null, null);
    }

    private void btnShare() {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra("content", growth.getContent());
        intent.putExtra("gid", growth.getId());
        intent.putExtra("from", cid);
        List<GrowthImage> listImages = new ArrayList<GrowthImage>();
        GrowthImage gImg = null;
        for (GrowthAlbumImages imgs : lists) {
            gImg = new GrowthImage(cid, growth.getId(), imgs.getPicID(),
                    imgs.getPicPath());
            listImages.add(gImg);

        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("shareImages", (Serializable) listImages);
        intent.putExtras(bundle);
        startActivity(intent);
        Utils.leftOutRightIn(this);
    }

    /**
     * 设置页面统计
     * 
     */
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public List<GrowthAlbumImages> fileList;
        private FragmentManager fm;

        public ImagePagerAdapter(FragmentManager fm,
                List<GrowthAlbumImages> fileList) {
            super(fm);
            this.fileList = fileList;
            this.fm = fm;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fm.beginTransaction();

        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position).getPicPath();
            ImageDetailFragment fra = ImageDetailFragment.newInstance(url);
            fra.setCallBack(GrowthImagePagerActivity.this);
            return fra;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                btnShare();
                break;
            case R.id.praise:
                PraiseAndCancle();
                break;
            case R.id.layoutComments:
                intentGrowthCommentActivity();
                break;
            case R.id.imgBack:
                finish();
                break;
            default:
                break;
        }
    }

    private void intentGrowthCommentActivity() {
        Intent intent = new Intent(this, GrowthCommentActivity.class);
        intent.putExtra("from", "bigImage");
        Bundle bundle = new Bundle();
        bundle.putSerializable("growth", (Serializable) growth);
        intent.putExtras(bundle);
        startActivity(intent);
        Utils.leftOutRightIn(this);
    }

    private void setPraiseDrable(boolean ispraise, int count) {
        Drawable drawable = null;
        if (ispraise) {
            drawable = getResources().getDrawable(R.drawable.icon_praise1);
        } else {
            drawable = getResources().getDrawable(R.drawable.icon_praise);

        } // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        praise.setCompoundDrawables(drawable, null, null, null);
        praise.setText(growth.getPraiseCnt() + "");

    }

    /**
     * 点赞
     */
    private void PraiseAndCancle() {
        if (growth.isPraising()) {
            return;
        }
        final boolean isPraise = growth.isPraised();
        if (growth.isPraised()) {
            growth.setPraiseCnt(growth.getPraiseCnt() - 1);
        } else {
            growth.setPraiseCnt(growth.getPraiseCnt() + 1);
        }
        int count = growth.getPraiseCnt();
        setPraiseDrable(!isPraise, count);
        BaseAsyncTask<Void, Void, RetError> taks = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                growth.setPraising(true);
                RetError ret = growth.uploadMyPraise(isPraise);
                return ret;
            }
        };
        taks.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                growth.setPraising(false);
                if (result != RetError.NONE) {
                    return;
                }
                growth.write(DBUtils.getDBsa(2));
                // setPraiseDrable(growth.isPraised(), growth.getCommentCnt());
                Bundle b = new Bundle();
                b.putSerializable("growth", growth);
                Intent intent = new Intent(Constants.REFUSH_GROWTH_PRAISE_COUNT);
                intent.putExtras(b);
                BroadCast.sendBroadCast(GrowthImagePagerActivity.this, intent);
            }

            @Override
            public void readDBFinish() {

            }
        });
        taks.executeWithCheckNet();

    }

    class UpLoadEditTimeAndLocationTask extends
            AsyncTask<Object, Void, RetError> {
        private Dialog dialog;
        private String location = "";
        private long happened;
        private String timeHappened = "";

        public UpLoadEditTimeAndLocationTask(String location, long happened,
                String strTime) {
            this.location = location;
            this.happened = happened;
            this.timeHappened = strTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getWaitDialog(GrowthImagePagerActivity.this,
                    "请稍候");
            dialog.show();
        }

        @Override
        protected RetError doInBackground(Object... params) {
            GrowthAlbumImages imgs = lists.get(pagerPosition);
            RetError ret = imgs.editTimeAndLocation(cid, happened, location);
            return ret;
        }

        @Override
        protected void onPostExecute(RetError result) {
            super.onPostExecute(result);
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result != RetError.NONE) {
                return;
            }
            growth.setLocation(location);
            growth.setHappened(timeHappened);
            setValue();
        }
    }

    private void showTitleLayout() {
        if (title.getVisibility() != View.VISIBLE) {
            title.setVisibility(View.VISIBLE);
            title.setAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.down_in));

        } else {
            title.setVisibility(View.INVISIBLE);
            title.setAnimation(AnimationUtils
                    .loadAnimation(this, R.anim.up_out));
        }
    }

    @Override
    public void onBackClick() {
        if (growth.getId() == 0) {
            return;
        }
        if (!isAuth) {
            showTitleLayout();
            return;

        }
        if (layPraise.getVisibility() != View.VISIBLE) {
            layPraise.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            title.setAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.down_in));
            layPraise.setAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.up_in));
        } else {
            layPraise.setVisibility(View.GONE);
            title.setVisibility(View.INVISIBLE);
            title.setAnimation(AnimationUtils
                    .loadAnimation(this, R.anim.up_out));
            layPraise.setAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.down_out));
        }
    }
}