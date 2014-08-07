package com.changlianxi.adapter;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.CircleGroup;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.inteface.DelGroupListener;
import com.changlianxi.util.DialogUtil;

public class CircleGroupAdapter extends BaseAdapter {
    private List<CircleGroup> lists;
    private Context mContext;
    private DelGroupListener mDelGroup;

    public CircleGroupAdapter(List<CircleGroup> lists, Context context,
            DelGroupListener delGroup) {
        this.lists = lists;
        this.mContext = context;
        this.mDelGroup = delGroup;
    }

    @Override
    public int getCount() {
        if (lists == null) {
            return 0;
        }
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
        ViewHolder holer = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.circle_group_item, null);
            holer = new ViewHolder();
            holer.imgDel = (ImageView) convertView.findViewById(R.id.img_del);
            holer.txt = (TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(holer);
        } else {
            holer = (ViewHolder) convertView.getTag();
        }
        holer.txt.setText(lists.get(position).getGroupsName());
        holer.imgDel.setOnClickListener(new DelListener(position));
        return convertView;
    }

    class ViewHolder {
        ImageView imgDel;
        TextView txt;
    }

    private void delPrompt(final int position) {
        Dialog dialog = DialogUtil.confirmDialog(mContext, "确定要删除吗", "确定",
                "取消", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        mDelGroup.delGroup(position);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    private class DelListener implements OnClickListener {
        private int position;

        public DelListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            delPrompt(position);
        }

    }

}
