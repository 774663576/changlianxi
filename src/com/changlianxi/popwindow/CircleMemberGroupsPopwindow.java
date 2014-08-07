package com.changlianxi.popwindow;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.CircleGroup;

public class CircleMemberGroupsPopwindow implements OnItemClickListener,
        OnClickListener {
    private PopupWindow popupWindow;
    private Context mContext;
    private View v;
    private View view;
    private ListView listview;
    private MyAdapter adapter;
    private OnGroupClick callback;
    private List<CircleGroup> lists;
    private Button btnAll;

    public CircleMemberGroupsPopwindow(Context context, View v,
            List<CircleGroup> lists) {
        this.mContext = context;
        this.v = v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.circle_member_gorups_popwindow_layout,
                null);
        this.lists = lists;
        initView();
        initPopwindow();
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.listView);
        listview.setOnItemClickListener(this);
        btnAll = (Button) view.findViewById(R.id.btn_all);
        btnAll.setOnClickListener(this);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
    }

    /**
     * 初始化popwindow
     */
    @SuppressWarnings("deprecation")
    private void initPopwindow() {
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * popwindow的显示
     */
    public void show() {
        // popupWindow.showAsDropDown(v);
        popupWindow.showAsDropDown(v, 0, 18);
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
            return lists.size();
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
                        R.layout.listview_textview_item, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(lists.get(position).getGroupsName());
            return convertView;
        }
    }

    class ViewHolder {
        TextView text;
    }

    public void setOnlistOnclick(OnGroupClick callback) {
        this.callback = callback;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        callback.onclick(lists.get(position).getGroupsId());
        dismiss();
    }

    public interface OnGroupClick {
        void onclick(int groups_id);

        void onAllClick();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        callback.onAllClick();

    }
}
