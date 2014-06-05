package com.changlianxi.chooseImage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.changlianxi.R;
import com.changlianxi.util.Utils;

/**
 * 相片适配器
 * @author teeker_bin
 *
 */
public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PhotoInfo> list;
    private ViewHolder viewHolder;
    private GridView gridView;
    private int width;

    public PhotoAdapter(Context context, List<PhotoInfo> list, GridView gridView) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.gridView = gridView;
        width = Utils.getSecreenWidth(context) / 3;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 刷新view
     * @param index
     */
    public void refreshView(int index) {
        int visiblePos = gridView.getFirstVisiblePosition();
        View view = gridView.getChildAt(index - visiblePos);
        ViewHolder holder = (ViewHolder) view.getTag();

        if (list.get(index).isChoose()) {
            holder.selectImage.setImageResource(R.drawable.icon_sel);
        } else {
            holder.selectImage.setImageResource(R.drawable.icon_sel_off);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_selectphoto, null);
            viewHolder.image = (ImageView) convertView
                    .findViewById(R.id.imageView);
            viewHolder.selectImage = (ImageView) convertView
                    .findViewById(R.id.selectImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).isChoose()) {
            viewHolder.selectImage.setImageResource(R.drawable.icon_sel);
        } else {
            viewHolder.selectImage.setImageResource(R.drawable.icon_sel_off);
        }
        LayoutParams layoutParams = viewHolder.image.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        viewHolder.image.setLayoutParams(layoutParams);
        final PhotoInfo photoInfo = list.get(position);
        if (photoInfo != null) {
            UniversalImageLoadTool.disPlay(
                    photoInfo.getPath_file(),
                    new RotateImageViewAware(viewHolder.image, photoInfo
                            .getPath_absolute()),
                    R.drawable.friends_sends_pictures_no);

            // UniversalImageLoadTool.disPlay(ThumbnailsUtil.MapgetHashValue(photoInfo.getImage_id(),photoInfo.getPath_file()),
            // viewHolder.image, R.drawable.common_defalt_bg);
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public ImageView selectImage;
    }
}
