package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.changlianxi.R;
import com.changlianxi.view.PullDownView;

public class ListFragMent extends Fragment {

    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private PullDownView mPullDownView;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.growth_fragment_layout, null);
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

    /**
     * 初始化控件
     */
    private void initView() {
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.PullDownlistView);
        mListView = mPullDownView.getListView();
        mListView.setCacheColorHint(0);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        setListener();
    }

    private void setListener() {
        mPullDownView.notifyDidMore();
        setValue();
    }

    private void setValue() {
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, getData()));
    }

    private List<String> getData() {

        List<String> data = new ArrayList<String>();
        for (int i = 1; i < 50; i++) {
            data.add("测试数据" + i);

        }

        return data;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
