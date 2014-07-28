package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;

/**
 * 圈子显示的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class CircleAdapter extends BaseAdapter {
    private List<Circle> circleLists;
    private Context mcontext;
    private int mHidePosition = -1;

    public CircleAdapter(Context context, List<Circle> listModle) {
        this.circleLists = listModle;
        this.mcontext = context;
    }

    @Override
    public int getCount() {
        if (circleLists == null) {
            return 0;
        }
        return circleLists.size();
    }

    public void setData(List<Circle> modle) {
        this.circleLists = modle;
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
        ViewHolder holder = null;
        String circleName = circleLists.get(position).getName();
        String circleLogo = circleLists.get(position).getOriginalLogo();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mcontext).inflate(
                    R.layout.circle_item, null);
            holder.circleImg = (CircularImage) convertView
                    .findViewById(R.id.circleImg);
            holder.circleName = (TextView) convertView
                    .findViewById(R.id.circleName);
            holder.circleBg = (ImageView) convertView
                    .findViewById(R.id.circleBg);
            holder.prompt = (TextView) convertView.findViewById(R.id.txtPrompt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.circleImg.setVisibility(View.VISIBLE);
        if (circleLogo == null || circleLogo.equals("")
                || !circleLogo.startsWith("http")) {
            if (circleLists.get(position).getId() == -1) {
                holder.circleImg
                        .setImageResource(R.drawable.circle_logo_wokemate);
            } else if (circleLists.get(position).getId() == -2) {
                holder.circleImg
                        .setImageResource(R.drawable.circle_logo_classmate);
            } else if (circleLists.get(position).getId() == -3) {
                holder.circleImg
                        .setImageResource(R.drawable.circle_logo_family);
            } else {
                holder.circleBg.setImageResource(R.drawable.pic_bg_no);
                holder.circleImg.setVisibility(View.GONE);

            }
        } else {
            circleLogo = circleLists.get(position).getLogo();
            UniversalImageLoadTool.disPlay(circleLogo, holder.circleImg,
                    R.drawable.pic_bg_no);
        }
        boolean isnew = circleLists.get(position).isNew();
        if (isnew) {
            holder.prompt.setText("new");
            holder.prompt.setVisibility(View.VISIBLE);
        } else {
            int newGrothCount = circleLists.get(position).getNewGrowthCnt();
            int newGrothCommentCount = circleLists.get(position)
                    .getNewGrowthCommentCnt();
            int newDynamicCount = circleLists.get(position).getNewDynamicCnt();
            int count = newDynamicCount + newGrothCommentCount + newGrothCount;
            if (count > 0) {
                holder.prompt.setVisibility(View.VISIBLE);
                holder.prompt.setText(count + "");
            } else {
                holder.prompt.setVisibility(View.GONE);
            }
        }
        holder.circleName.setText(StringUtils.ToDBC(circleName));
        if (position == mHidePosition) {
            // convertView.setVisibility(View.INVISIBLE);
            mHidePosition = -1;
        }
        return convertView;
    }

    class ViewHolder {
        CircularImage circleImg;
        ImageView circleBg;
        TextView circleName;
        TextView prompt;
    }

    /**
     * 设置某项隐藏
     * @param position
     */
    public void setItemHide(int position) {
        this.mHidePosition = position;
        notifyDataSetChanged();
    }

}
