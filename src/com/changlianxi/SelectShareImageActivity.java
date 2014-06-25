package com.changlianxi;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.changlianxi.data.GrowthImage;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.umeng.analytics.MobclickAgent;

public class SelectShareImageActivity extends BaseActivity implements
        OnItemClickListener {
    private GridView gridView;
    private List<GrowthImage> images;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_share_image);
        images = (List<GrowthImage>) getIntent().getExtras().getSerializable(
                "images");
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setOnItemClickListener(this);
        if (images.size() < 3) {
            gridView.setNumColumns(2);
        }
        gridView.setAdapter(new MyAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
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
            String path = images.get(position).getImg();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater
                        .from(SelectShareImageActivity.this).inflate(
                                R.layout.select_share_img_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setTag(path);
            path = images.get(position).getImg(200);
            UniversalImageLoadTool.disPlay(path, 
                    holder.img, R.drawable.empty_photo);
            return convertView;
        }

        class ViewHolder {
            ImageView img;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent intent = new Intent();
        intent.putExtra("imgs", images.get(position).getImg());
        setResult(2, intent);
        finish();

    }
}
