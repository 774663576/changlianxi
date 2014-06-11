package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;

public class HomeSearchAdapter extends BaseAdapter {
    private Context context;
    private List<CircleMember> circleMembers;

    public HomeSearchAdapter(Context context, List<CircleMember> circleMembers) {
        this.circleMembers = circleMembers;
        this.context = context;

    }

    @Override
    public int getCount() {
        return circleMembers.size();
    }

    public void setData(List<CircleMember> list) {
        this.circleMembers = list;
        notifyDataSetChanged();
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
        ViewHolder holder;
        CircleMember member = circleMembers.get(position);
        Circle c = new Circle(member.getCid());
        c.getCircleName(DBUtils.getDBsa(1));
        String circleName = c.getName();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.home_search_listview_item, null);
            holder.img = (CircularImage) convertView.findViewById(R.id.userimg);
            holder.name = (TextView) convertView.findViewById(R.id.username);
            holder.changeBg = (LinearLayout) convertView.findViewById(R.id.bg);
            holder.lastName = (TextView) convertView
                    .findViewById(R.id.lastName);
            holder.cellPhone = (TextView) convertView
                    .findViewById(R.id.cellPhone);
            holder.circleName = (TextView) convertView
                    .findViewById(R.id.circleName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = StringUtils.cutEight(member.getName());
        holder.name.setText(name);
        holder.lastName.setText(name.substring(name.length() - 1));
        holder.cellPhone.setText(member.getCellphone());
        holder.circleName.setText(circleName);
        String path = member.getAvatar();
        if (path == null || "".equals(path)) {
            holder.img.setImageResource(R.drawable.head_bg);
            holder.img.setVisibility(View.GONE);

        } else {
            holder.img.setVisibility(View.VISIBLE);
            UniversalImageLoadTool.disPlay(path, new RotateImageViewAware(
                    holder.img, path), R.drawable.head_bg);
        }
        return convertView;
    }

    class ViewHolder {
        CircularImage img;
        TextView cellPhone;
        TextView circleName;
        TextView name;
        TextView lastName;
        LinearLayout changeBg;

    }
}
