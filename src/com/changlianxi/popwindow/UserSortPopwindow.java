package com.changlianxi.popwindow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;

/**
 * 成员排序
 * 
 * @author teeker_bin
 * 
 */
public class UserSortPopwindow implements OnItemClickListener {
    private PopupWindow popupWindow;
    private Context mContext;
    private View v;
    private View view;
    private ListView listview;
    private MyAdapter adapter;
    private OnlistOnclick callback;
    private List<String> listdata = new ArrayList<String>();

    public UserSortPopwindow(Context context, View v, List<String> listdata) {
        this.mContext = context;
        this.v = v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.user_sort_listview, null);
        this.listdata = listdata;
        initView();
        initPopwindow();
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.listView1);
        listview.setOnItemClickListener(this);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
    }

    /**
     * 初始化popwindow
     */
    @SuppressWarnings("deprecation")
    private void initPopwindow() {
        popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * popwindow的显示
     */
    public void show() {
        popupWindow.showAsDropDown(v);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }

    // 隐藏
    public void dismiss() {
        popupWindow.dismiss();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.user_sort_item, null);
                holder = new ViewHolder();
                holder.laybg = (LinearLayout) convertView
                        .findViewById(R.id.laybg);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(listdata.get(position));
            return convertView;
        }
    }

    class ViewHolder {
        TextView text;
        LinearLayout laybg;
    }

    public void setOnlistOnclick(OnlistOnclick callback) {
        this.callback = callback;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        callback.onclick(position);
        dismiss();
    }

    public interface OnlistOnclick {
        void onclick(int position);
    }
}
