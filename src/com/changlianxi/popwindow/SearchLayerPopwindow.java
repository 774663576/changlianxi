package com.changlianxi.popwindow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.UserInfoActivity;
import com.changlianxi.adapter.MemberAdapter;
import com.changlianxi.data.CircleMember;
import com.changlianxi.util.Utils;
import com.changlianxi.view.SearchEditText;

/**
 * 搜索层
 * 
 * @author teeker_bin
 * 
 */
public class SearchLayerPopwindow implements OnItemClickListener,
        OnClickListener {
    private PopupWindow popupWindow;
    private Context mContext;
    private View v;
    private View view;
    private LinearLayout searchLayout;
    private ListView searchListView;
    private SearchEditText layerSearch;
    private List<CircleMember> searchListModles = new ArrayList<CircleMember>();// 存储搜索列表
    private List<CircleMember> circleMembersLists = new ArrayList<CircleMember>();
    private MemberAdapter searchAdapter;
    private int cid;
    private TextView cancle;
    private OnCancleClick callBack;

    public interface OnCancleClick {
        void onCancle();
    }

    public void setCallBack(OnCancleClick callBack) {
        this.callBack = callBack;
    }

    public SearchLayerPopwindow(Context context, View v,
            List<CircleMember> circleMembersLists, int cid) {
        this.mContext = context;
        this.v = v;
        this.cid = cid;
        this.circleMembersLists = circleMembersLists;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.search_layer_layout, null);
        initView();
        initPopwindow();
    }

    private void initView() {
        searchLayout = (LinearLayout) view.findViewById(R.id.searchLayout);
        layerSearch = (SearchEditText) view.findViewById(R.id.layer_search);
        layerSearch.addTextChangedListener(new EditWather());
        searchListView = (ListView) view.findViewById(R.id.search_list);
        cancle = (TextView) view.findViewById(R.id.cancle);
        setListener();

    }

    private void setListener() {
        searchListView.setOnItemClickListener(this);
        searchLayout.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    /**
     * 初始化popwindow
     */
    private void initPopwindow() {
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        // 这个是为了点击�?返回Back”也能使其消失，并且并不会影响你的背景（很神奇的�?
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                callBack.onCancle();

            }
        });
    }

    /**
     * popwindow的显�?
     */
    public void show() {
        popupWindow.showAtLocation(v, 0, 0, 0);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状�?
        popupWindow.update();
        Utils.popUp(mContext);
    }

    // 隐藏
    public void dismiss() {
        popupWindow.dismiss();
    }

    class ViewHolder {
        TextView text;
        LinearLayout laybg;
    }

    class EditWather implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            String key = s.toString().toLowerCase();
            if (key.length() == 0) {
                Utils.hideSoftInput(mContext);
                searchLayout.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.search_layer_bg));
                layerSearch.setCompoundDrawables(null, null, null, null);
                searchListModles.clear();
                searchAdapter.setData(searchListModles);
                return;
            }
            Drawable del = mContext.getResources().getDrawable(R.drawable.del);
            del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
            layerSearch.setCompoundDrawables(null, null, del, null);
            searchListModles.clear();
            for (int i = 0; i < circleMembersLists.size(); i++) {
                String name = circleMembersLists.get(i).getName();
                String pinyin = circleMembersLists.get(i).getSortkey()
                        .toLowerCase();
                String pinyinFir = circleMembersLists.get(i).getPinyinFir();
                String mobileNum = circleMembersLists.get(i).getCellphone();
                if (name.contains(key) || pinyin.contains(key)
                        || pinyinFir.contains(key) || mobileNum.contains(key)) {
                    CircleMember modle = circleMembersLists.get(i);
                    searchListModles.add(modle);

                }
            }
            if (searchListModles.size() > 0) {
                searchLayout.setBackgroundColor(Color.WHITE);
            }
            if (searchAdapter == null) {
                searchAdapter = new MemberAdapter(mContext, searchListModles);

                searchListView.setAdapter(searchAdapter);
            } else {
                searchAdapter.setData(searchListModles);

            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {

        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent it = new Intent();
        it.setClass(mContext, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("member", searchListModles.get(position));
        it.putExtras(bundle);
        mContext.startActivity(it);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchLayout:
                if (circleMembersLists.size() > 0) {
                    return;
                }
                dismiss();
                break;
            case R.id.cancle:
                // callBack.onCancle();
                dismiss();
                break;
            default:
                break;
        }
    }
}
