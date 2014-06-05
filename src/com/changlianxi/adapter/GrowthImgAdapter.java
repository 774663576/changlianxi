package com.changlianxi.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.MyImageView;

public class GrowthImgAdapter extends BaseAdapter {
    private Context mContext;
    private List<GrowthImage> listData;

    private FinalBitmap fb;

    public GrowthImgAdapter(Context context, List<GrowthImage> data) {
        this.mContext = context;
        this.listData = data;
        fb = CLXApplication.getFb();
        fb.configLoadingImage(R.drawable.empty_photo);
        fb.configLoadfailImage(R.drawable.empty_photo);
    }

    @Override
    public int getCount() {
        return listData.size();
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
        String path = listData.get(position).getImg();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grow_img_gridview_item, null);
            holder.img = (MyImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setTag(path);
        if (!path.startsWith("http")) {
            UniversalImageLoadTool.disPlay("file://" + path,
                    new RotateImageViewAware(holder.img, path),
                    R.drawable.empty_photo);
        } else {
            path = StringUtils.JoinString(path, "_200x200");
            fb.display(holder.img, path);
            // FinalBitmapLoadTool.display(path, holder.img,
            // R.drawable.empty_photo);
        }
        return convertView;
    }

    class ViewHolder {
        MyImageView img;
    }
}
