package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.EditData;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

/**
 * 同步个人资料界面
 * @author teeker_bin
 *
 */
public class SynchronizeSelfInfoActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    private ImageView back;
    private ListView listview;
    private List<EditData> listDatas = new ArrayList<EditData>();
    private MyAdapter adapter;
    private Button btnOk;
    private int uid;
    private int cid;
    private String headicon;
    private TextView title;
    private List<EditData> selectLists = new ArrayList<EditData>();

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize_self_info);
        cid = getIntent().getIntExtra("cid", 0);
        uid = getIntent().getIntExtra("uid", 0);
        headicon = getIntent().getExtras().getString("headicon");
        listDatas = (List<EditData>) getIntent().getExtras().getSerializable(
                "editData");
        setChooseFlag(true);
        adapter = new MyAdapter();
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.titleTxt);
        btnOk = (Button) findViewById(R.id.btnOk);
        back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        btnOk.setOnClickListener(this);
        title.setText("同步个人资料");
    }

    /**
     * 设置页面统计
     * 
     */
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
            long arg3) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.checkBox.toggle();
        listDatas.get(position).setChecked(holder.checkBox.isChecked());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btnOk:
                selectLists.clear();
                for (EditData data : listDatas) {
                    if (data.isChecked()) {
                        selectLists.add(data);
                    }
                }
                if (selectLists.size() == 0) {
                    Utils.showToast("至少选择选择一项", Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(this, SyncchronousInformationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("change", (Serializable) selectLists);
                intent.putExtras(bundle);
                intent.putExtra("headicon", headicon);
                intent.putExtra("uid", uid);
                intent.putExtra("cid", cid);
                startActivity(intent);
                finish();
                Utils.leftOutRightIn(this);
                break;
            default:
                break;
        }
    }

    private void setChooseFlag(boolean flag) {
        for (EditData data : listDatas) {
            data.setChecked(flag);
        }

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listDatas.size();
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
                holder = new ViewHolder();
                convertView = LayoutInflater.from(
                        SynchronizeSelfInfoActivity.this).inflate(
                        R.layout.synchronizeselfinfo_item, null);
                holder.key = (TextView) convertView.findViewById(R.id.key);
                holder.value = (TextView) convertView.findViewById(R.id.value);
                holder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.checkBox);
                holder.avatr = (CircularImage) convertView
                        .findViewById(R.id.avatar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.key.setText(listDatas.get(position).getOperation() + ":");
            String value = listDatas.get(position).getDetail();
            if ("MAN".equals(value)) {
                holder.value.setText("男");
            } else if ("WOMAN".equals(value)) {
                holder.value.setText("女");
            } else {
                holder.value.setText(value);

            }
            if (listDatas.get(position).getType()
                    .equals(PersonDetailType.D_AVATAR)) {
                holder.avatr.setVisibility(View.VISIBLE);
                holder.value.setVisibility(View.GONE);
                UniversalImageLoadTool.disPlay(value, new RotateImageViewAware(
                        holder.avatr, value), R.drawable.head_bg);

            } else {
                holder.avatr.setVisibility(View.GONE);
                holder.value.setVisibility(View.VISIBLE);
            }
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(listDatas.get(position).isChecked());
            return convertView;
        }
    }

    class ViewHolder {
        TextView key;
        TextView value;
        CheckBox checkBox;
        CircularImage avatr;
    }

}
