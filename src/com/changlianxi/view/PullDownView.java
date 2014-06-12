package com.changlianxi.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.view.ScrollOverListView.OnScrollOverListener;

/**
 * 下拉刷新控件 真正实现下拉刷新的是这个控件， ScrollOverListView只是提供触摸的事件等
 * 
 */

public class PullDownView extends LinearLayout implements OnScrollOverListener {

    private static final int START_PULL_DEVIATION = 50; // 移动误差
    private static final int WHAT_DID_MORE = 5; // Handler what 已经获取完更多
    private static final int WHAT_DID_REFRESH = 3; // Handler what 已经刷新完
    /** 底部更多的按键 **/
    private LinearLayout mFooterView;
    /** 底部更多的按键 **/
    private TextView mFooterTextView;
    /** 底部更多的按键 **/
    private ProgressBar mFooterLoadingView;
    /** 已经含有 下拉刷新功能的列表 **/
    private ScrollOverListView mListView;
    /** 刷新和更多的事件接口 **/
    private OnPullDownListener mOnPullDownListener;
    private float mMotionDownLastY; // 按下时候的Y轴坐标
    private boolean mIsFetchMoreing; // 是否获取更多中
    private boolean mIsPullUpDone; // 是否回推完成
    private boolean mEnableAutoFetchMore; // 是否允许自动获取更多
    private String footerTextStr = "";

    public PullDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderViewAndFooterViewAndListView(context);
    }

    public PullDownView(Context context) {
        super(context);
        initHeaderViewAndFooterViewAndListView(context);
    }

    /*
     * ================================== Public method 外部使用，具体就是用这几个就可以了
     */

    /**
     * 刷新和获取更多事件接口
     */
    public interface OnPullDownListener {
        /** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
        void onRefresh();

        /** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
        void onMore();
    }

    /**
     * 通知已经获取完更多了，要放在Adapter.notifyDataSetChanged后面
     * 当你执行完更多任务之后，调用这个notyfyDidMore() 才会隐藏加载圈等操作
     */
    public void notifyDidMore() {
        mUIHandler.sendEmptyMessage(WHAT_DID_MORE);
    }

    /** 刷新完毕 关闭头部滚动条 **/
    public void RefreshComplete() {
        mUIHandler.sendEmptyMessage(WHAT_DID_REFRESH);
    }

    /**
     * 设置监听器
     * 
     * @param listener
     */
    public void setOnPullDownListener(OnPullDownListener listener) {
        mOnPullDownListener = listener;
    }

    /**
     * 获取内嵌的listview
     * 
     * @return ScrollOverListView
     */
    public ListView getListView() {
        return mListView;
    }

    public void setFooterVisible(boolean flag) {
        if (flag) {
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterView.setVisibility(View.GONE);

        }
    }

    public void setFooterViewFont(int size, String str) {
        mFooterTextView.setTextSize(size);
        footerTextStr = str;
    }

    public void hideLastUpdateTime() {
        mListView.hideLastUpdateTime();
    }

    /**
     * 是否开启自动获取更多 自动获取更多，将会隐藏footer，并在到达底部的时候自动刷新
     * 
     * @param index
     *            倒数第几个触发
     */
    public void enableAutoFetchMore(boolean enable, int index) {
        if (enable) {
            mListView.setBottomPosition(index);
            mFooterLoadingView.setVisibility(View.VISIBLE);
        } else {
            // mFooterTextView.setText("邮件不自动收取");
            mFooterLoadingView.setVisibility(View.GONE);
        }
        mEnableAutoFetchMore = enable;
    }

    /*
     * ================================== Private method 具体实现下拉刷新等操作
     * 
     * ==================================
     */

    /**
     * 初始化界面
     */
    private void initHeaderViewAndFooterViewAndListView(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        /**
         * 自定义脚部文件
         */
        mFooterView = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.pulldown_footer, null);
        mFooterTextView = (TextView) mFooterView
                .findViewById(R.id.pulldown_footer_text);
        mFooterLoadingView = (ProgressBar) mFooterView
                .findViewById(R.id.pulldown_footer_loading);
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsFetchMoreing) {
                    mIsFetchMoreing = true;
                    mFooterTextView.setText("加载更多中...");
                    mFooterLoadingView.setVisibility(View.VISIBLE);
                    mOnPullDownListener.onMore();
                }
            }
        });

        /*
         * ScrollOverListView 同样是考虑到都是使用，所以放在这里 同时因为，需要它的监听事件
         */
        mListView = new ScrollOverListView(context);
        mListView.setOnScrollOverListener(this);
        mListView.setCacheColorHint(0);
        mListView.setDivider(null);
        addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // 空的listener
        mOnPullDownListener = new OnPullDownListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onMore() {
            }
        };
        // mListView.addFooterView(mFooterView);
        // mListView.setAdapter(mListView.getAdapter());

    }

    public void addFooterView() {
        mListView.addFooterView(mFooterView);

    }

    public void removeFoterView() {
        if (mListView == null || mFooterView == null) {
            return;
        }
        mListView.removeFooterView(mFooterView);

    }

    private Handler mUIHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DID_REFRESH: {
                    mListView.onRefreshComplete();
                    return;
                }

                case WHAT_DID_MORE: {
                    mIsFetchMoreing = false;
                    if (!"".equals(footerTextStr)) {
                        mFooterTextView.setText(footerTextStr);
                    } else {
                        mFooterTextView.setText("加载更多");
                    }
                    mFooterLoadingView.setVisibility(View.GONE);
                }
            }
        }

    };

    /*
     * ================================== 实现 OnScrollOverListener接口
     */

    @Override
    public boolean onListViewTopAndPullDown(int delta) {

        return true;
    }

    /**
     * 条目是否填满整个屏幕
     */
    private boolean isFillScreenItem() {
        final int firstVisiblePosition = mListView.getFirstVisiblePosition();
        final int lastVisiblePostion = mListView.getLastVisiblePosition()
                - mListView.getFooterViewsCount();
        final int visibleItemCount = lastVisiblePostion - firstVisiblePosition
                + 1;
        final int totalItemCount = mListView.getCount()
                - mListView.getFooterViewsCount();

        if (visibleItemCount < totalItemCount) return true;
        return false;
    }

    @Override
    public boolean onListViewBottomAndPullUp(int delta) {
        if (!mEnableAutoFetchMore || mIsFetchMoreing) return false;
        // 数量充满屏幕才触发
        if (isFillScreenItem()) {
            // TODO Auto-generated method stub
            mFooterView.setVisibility(View.VISIBLE);
            mIsFetchMoreing = true;
            mFooterTextView.setText("加载更多");
            mFooterLoadingView.setVisibility(View.VISIBLE);
            mOnPullDownListener.onMore();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMotionDown(MotionEvent ev) {
        mIsPullUpDone = false;
        mMotionDownLastY = ev.getRawY();

        return false;
    }

    @Override
    public boolean onMotionMove(MotionEvent ev, int delta) {
        // 当头部文件回推消失的时候，不允许滚动
        if (mIsPullUpDone) return true;

        // 如果开始按下到滑动距离不超过误差值，则不滑动
        final int absMotionY = (int) Math.abs(ev.getRawY() - mMotionDownLastY);
        if (absMotionY < START_PULL_DEVIATION) return true;

        return false;
    }

    @Override
    public boolean onMotionUp(MotionEvent ev) {

        if (ScrollOverListView.canRefleash) {
            ScrollOverListView.canRefleash = false;
            mOnPullDownListener.onRefresh();
        }
        return false;
    }

    public void Refresh() {
        if (ScrollOverListView.canRefleash) {
            ScrollOverListView.canRefleash = false;
            mOnPullDownListener.onRefresh();
        }
        mListView.setRefush();
    }

    /** 隐藏头部 禁用下拉更新 **/
    public void setHideHeader() {
        mListView.showRefresh = false;
    }

    /** 显示头部 使用下拉更新 **/
    public void setShowHeader() {
        mListView.showRefresh = true;
    }

    /** 隐藏底部 禁用上拉更多 **/
    public void setHideFooter() {
        mFooterView.setVisibility(View.GONE);
        mFooterTextView.setVisibility(View.GONE);
        mFooterLoadingView.setVisibility(View.GONE);
        enableAutoFetchMore(false, 1);
    }

    /** 显示底部 使用上拉更多 **/
    public void setShowFooter() {
        // mFooterView.setVisibility(View.VISIBLE);
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterLoadingView.setVisibility(View.VISIBLE);
        enableAutoFetchMore(true, 1);
    }

}
