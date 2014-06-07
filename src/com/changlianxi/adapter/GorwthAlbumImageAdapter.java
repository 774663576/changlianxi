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
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.MyImageView;

public class GorwthAlbumImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<GrowthAlbumImages> pics;

    // private FinalBitmap fb;

    public GorwthAlbumImageAdapter(Context context, List<GrowthAlbumImages> pics) {
        this.mContext = context;
        this.pics = pics;
        // fb = CLXApplication.getFb();
        // fb.configLoadfailImage(R.drawable.empty_photo);
        // fb.configLoadingImage(R.drawable.empty_photo);
    }

    @Override
    public int getCount() {
        return pics.size();
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
        String path = pics.get(position).getPicPath();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grow_img_gridview_item, null);
            holder.img = (MyImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        path = StringUtils.JoinString(path, "_200x200");
        // fb.display(holder.img, StringUtils.JoinString(path, "_200x200"));
        // FinalBitmapLoadTool.display(StringUtils.JoinString(path, "_200x200"),
        // holder.img, R.drawable.empty_photo);
        UniversalImageLoadTool.disPlay(path, new RotateImageViewAware(
                holder.img, path), R.drawable.empty_photo);
        return convertView;
    }

    class ViewHolder {
        MyImageView img;
    }
}
