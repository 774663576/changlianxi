package com.changlianxi.adapter;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.AlumMonthActivity;
import com.changlianxi.R;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbum;
import com.changlianxi.db.DBUtils;
import com.changlianxi.showBigPic.AlbumImagePagerActivity;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.view.GrowthImgGridView;

public class GrowthYearAlbumAdapter extends BaseAdapter {
    private Context mContext;
    private List<GrowthAlbum> album;
    private List<Growth> growthList;
    private int cid;

    public GrowthYearAlbumAdapter(Context mContext, List<GrowthAlbum> album,
            List<Growth> growthList, int cid) {
        this.mContext = mContext;
        this.album = album;
        this.growthList = growthList;
        this.cid = cid;

    }

    @Override
    public int getCount() {
        return album.size();
    }

    public void setData(List<GrowthAlbum> album, List<Growth> growthList) {
        this.album = album;
        this.growthList = growthList;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.album_year_item, null);
            holder.gridView = (GrowthImgGridView) convertView
                    .findViewById(R.id.imgGridview);
            holder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.total = (TextView) convertView.findViewById(R.id.total);
            holder.contributors = (TextView) convertView
                    .findViewById(R.id.contributors);
            holder.btnAllYear = (Button) convertView
                    .findViewById(R.id.btnAllYear);
            holder.line = (View) convertView.findViewById(R.id.line);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setVisibility(View.GONE);
        holder.gridView.setVisibility(View.VISIBLE);
        holder.gridView.setNumColumns(5);
        holder.gridView.setAdapter(new GorwthAlbumImageAdapter(mContext, album
                .get(position).getPics()));
        holder.gridView
                .setOnItemClickListener(new GridViewOnItemClick(position) {
                });
        String str = DateUtils.getMonth(album.get(position).getAlbumDate(),
                "MM");
        String strYear = DateUtils.getYear(album.get(position).getAlbumDate(),
                "yyyy");
        if ("".equals(strYear)) {
            strYear = "0";
        }
        if (str.startsWith("0")) {
            str = str.substring(1);
        }
        holder.date.setText(album.get(position).getAlbumName()
                .replace("-", "年")
                + "月");
        holder.btnAllYear.setOnClickListener(new BtnOnClick("".equals(str) ? 0
                : Integer.valueOf(str), Integer.valueOf(strYear)));
        holder.date.setOnClickListener(new BtnOnClick("".equals(str) ? 0
                : Integer.valueOf(str), Integer.valueOf(strYear)));
        holder.total.setOnClickListener(new BtnOnClick("".equals(str) ? 0
                : Integer.valueOf(str), Integer.valueOf(strYear)));
        holder.total.setText("大家已经上传" + album.get(position).getAlbumTotal()
                + "张照片>");
        getContributors(position, holder.contributors);
        if (position == album.size() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void getContributors(int position, TextView textview) {
        String name = "";
        int index = 0;
        for (int i = 0; i < album.get(position).getPics().size(); i++) {
            int previous_growth_id = 0;
            int current_growth_id = album.get(position).getPics().get(i)
                    .getPicGrowthID();
            if (i - 1 >= 0) {
                previous_growth_id = album.get(position).getPics().get(i - 1)
                        .getPicGrowthID();
            }
            if (current_growth_id == previous_growth_id) {
                continue;
            }
            CircleMember m = new CircleMember(cid, 0,
                    getUserIDByGrowthID(current_growth_id));
            m.getNameAndAvatar(DBUtils.getDBsa(1));
            if (name.contains(m.getName())) {
                continue;
            }
            if (index > 2) {
                break;
            }
            name += m.getName() + " ";
            index++;

        }
        String str = "";
        if (index > 1) {
            str = "贡献者：" + name + "等"
                    + album.get(position).getAlbumContributors() + "人";
        } else {
            str = "贡献者：" + name + +album.get(position).getAlbumContributors()
                    + "人";
        }
        textview.setText(str);
    }

    private int getUserIDByGrowthID(int growth_id) {
        for (int i = 0; i < growthList.size(); i++) {
            if (growth_id == growthList.get(i).getId()) {
                return growthList.get(i).getPublisher();
            }
        }
        return 0;

    }

    class ImgOnClick implements OnClickListener {
        int position;

        public ImgOnClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, AlbumImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                    (Serializable) album.get(position).getPics());
            bundle.putSerializable("growthList", (Serializable) growthList);
            intent.putExtras(bundle);
            intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 0);
            intent.putExtra("cid", cid);
            mContext.startActivity(intent);
        }

    }

    class GridViewOnItemClick implements OnItemClickListener {
        int position;

        public GridViewOnItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posit,
                long arg3) {
            Intent intent = new Intent(mContext, AlbumImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                    (Serializable) album.get(position).getPics());
            bundle.putSerializable("growthList", (Serializable) growthList);
            intent.putExtras(bundle);
            intent.putExtra(Constants.EXTRA_IMAGE_INDEX, posit);
            intent.putExtra("cid", cid);
            mContext.startActivity(intent);
        }

    }

    class BtnOnClick implements OnClickListener {
        int startM;
        int startY;

        public BtnOnClick(int startM, int startY) {
            this.startM = startM;
            this.startY = startY;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, AlumMonthActivity.class);
            intent.putExtra("cid", cid);
            intent.putExtra("startM", startM);
            intent.putExtra("startY", startY);

            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    class ViewHolder {
        GrowthImgGridView gridView;
        TextView date;
        TextView total;
        ImageView img;
        View line;
        TextView contributors;
        Button btnAllYear;

    }
}
