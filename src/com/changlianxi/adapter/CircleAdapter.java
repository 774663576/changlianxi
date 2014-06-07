package com.changlianxi.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Circle;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.StringUtils;
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

    private FinalBitmap fb;

    public CircleAdapter(Context context, List<Circle> listModle) {
        this.circleLists = listModle;
        this.mcontext = context;
        // fb = CLXApplication.getFb();
        // fb.configLoadfailImage(R.drawable.pic_bg_no);
        // fb.configLoadingImage(R.drawable.pic_bg_no);
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
        String circleLogo = circleLists.get(position).getLogo();
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
        if (circleLogo.equals("addroot")) {
            holder.circleBg.setImageResource(R.drawable.pic_add);
            holder.circleImg.setVisibility(View.GONE);
        } else if (circleLogo == null || circleLogo.equals("")
                || !circleLogo.startsWith("http")) {
            holder.circleBg.setImageResource(R.drawable.pic_bg_no);
            holder.circleImg.setVisibility(View.GONE);
        } else {
            // fb.display(holder.circleImg, circleLogo);
            FinalBitmapLoadTool.display(circleLogo, holder.circleImg,
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
            int newMyDetailEditCnt = circleLists.get(position)
                    .getNewMyDetailEditCnt();
            int count = newDynamicCount + newGrothCommentCount + newGrothCount
                    + newMyDetailEditCnt;
            if (count > 0) {
                holder.prompt.setVisibility(View.VISIBLE);
                holder.prompt.setText(count + "");
            } else {
                holder.prompt.setVisibility(View.GONE);
            }
        }
        holder.circleName.setText(StringUtils.ToDBC(circleName));
        return convertView;
    }

    class ViewHolder {
        CircularImage circleImg;
        ImageView circleBg;
        TextView circleName;
        TextView prompt;
    }

}
