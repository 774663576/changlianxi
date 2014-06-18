package com.changlianxi.tab.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.view.HackyViewPager;

public class DynamicFragment extends Fragment implements OnClickListener {
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private int newDynamicCount = 0;
    private String txtCirName;
    private int cid;
    private HackyViewPager mPager;
    private ImagePagerAdapter mAdapter;
    private ImageView imageView;
    private int bmpW;// 动画图片宽度
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private Button btnAll, btnAboutMe;
    private ImageView back;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        newDynamicCount = getArguments().getInt("newDynamicCount", 0);
        cid = getArguments().getInt("cid", 0);
        txtCirName = getArguments().getString("circleName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.dynamic_fragment, null);
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
        mPager = (HackyViewPager) getView().findViewById(R.id.pager);
        InitImageView();
        InitImageView();
        btnAll = (Button) getView().findViewById(R.id.btnAll);
        btnAboutMe = (Button) getView().findViewById(R.id.btnAboutMe);
        back = (ImageView) getView().findViewById(R.id.back);
        title = (TextView) getView().findViewById(R.id.titleTxt);
        title.setText(txtCirName);
        setListener();
    }

    /**
     * 初始化滑动的view
     */
    private void InitImageView() {
        imageView = (ImageView) getView().findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(),
                R.drawable.tab_selected).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        imageView
                .setLayoutParams(new LinearLayout.LayoutParams(screenW / 2, 5));
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnAboutMe.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        mPager.setOnPageChangeListener(new PageListener());
        setValue();
    }

    private void setValue() {
        mAdapter = new ImagePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
    }

    class PageListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
            Animation animation = new TranslateAnimation(one * currIndex, one
                    * arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }

    }

    class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fm;

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fm.beginTransaction();

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return DynamicAboutMeFragment.newInstance(cid, newDynamicCount);
            }
            return DynamicAllFragment.newInstance(cid, newDynamicCount);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                BroadCast.sendBroadCast(getActivity(), Constants.CHANGE_TAB);
                break;
            case R.id.btnAboutMe:
                mPager.setCurrentItem(0);
                break;
            case R.id.btnAll:
                mPager.setCurrentItem(1);
                break;

            default:
                break;
        }
    }
}