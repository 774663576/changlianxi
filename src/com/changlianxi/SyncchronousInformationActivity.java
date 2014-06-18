package com.changlianxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.EditData;
import com.changlianxi.db.DBUtils;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.ScrollViewWithListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 同步个人资料选择圈子界面
 * @author LG
 *
 */
public class SyncchronousInformationActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    private ListView firstListView;
    private ScrollViewWithListView secondlListView;
    private int uid;
    private String token = "";
    private int cid;
    private List<Circle> circles = new ArrayList<Circle>();
    private CircleList circleList = null;
    private ShowAdapter sAdapter;
    private ChooseAdapter cAdapter;
    private List<EditData> lists = new ArrayList<EditData>();
    private Button button;
    private String PATH = "circles/itransModify";
    private ImageView back;
    private String headicon;
    private TextView title;
    private CheckBox checkAll;

    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_public_info_show_info);
        cid = getIntent().getIntExtra("cid", 0);
        uid = getIntent().getIntExtra("uid", 0);
        headicon = getIntent().getExtras().getString("headicon");
        token = SharedUtils.getString("token", "");
        lists = (List<EditData>) getIntent().getExtras().getSerializable(
                "change");
        initView();

    }

    private void initView() {
        title = (TextView) findViewById(R.id.titleTxt);
        firstListView = (ListView) findViewById(R.id.list_first);
        secondlListView = (ScrollViewWithListView) findViewById(R.id.list_second);
        checkAll = (CheckBox) findViewById(R.id.check_all);
        back = (ImageView) findViewById(R.id.back);
        button = (Button) findViewById(R.id.btn_save);
        setListener();
    }

    private void setListener() {
        firstListView.setDivider(null);
        firstListView.setVerticalScrollBarEnabled(true);
        secondlListView.setVerticalScrollBarEnabled(true);
        secondlListView.setOnItemClickListener(this);
        back.setOnClickListener(this);
        button.setOnClickListener(this);
        checkAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                for (int i = 0; i < circles.size(); i++) {
                    circles.get(i).setNew(isChecked);
                }
                cAdapter.notifyDataSetChanged();
            }
        });
        setValue();
    }

    private void setValue() {
        title.setText("同步个人资料");
        setAdapter();
    }

    private void setAdapter() {
        circleList = new CircleList(circles);
        circleList.read(DBUtils.getDBsa(1));
        for (int i = circles.size() - 1; i >= 0; i--) {
            if (circles.get(i).isNew() || cid == circles.get(i).getId()) {
                circles.remove(i);
            }
        }
        if (cid != 0) {
            Circle circle = new Circle(-1, "我的名片");
            circle.setNew(false);
            circle.setLogo(headicon);
            circles.add(circle);
        }
        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).setNew(true);
        }
        sAdapter = new ShowAdapter(this);
        firstListView.setAdapter(sAdapter);
        cAdapter = new ChooseAdapter(this);
        secondlListView.setAdapter(cAdapter);
    }

    class ViewHolderTop {
        CircularImage headImg;
        TextView type;
        TextView detail;
    }

    class ViewHolder {
        CircularImage circleImg;
        TextView circleName;
        CheckBox checkBox;
    }

    class ShowAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ShowAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
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
            ViewHolderTop holder = null;
            String typeString = UserInfoUtils.convertToChines2(lists
                    .get(position).getType().toString());
            String detail = lists.get(position).getDetail();
            String oprition = lists.get(position).getOperation();
            if (convertView == null) {
                holder = new ViewHolderTop();
                convertView = inflater.inflate(R.layout.first_list_item, null);
                holder.type = (TextView) convertView
                        .findViewById(R.id.txt_type);
                holder.detail = (TextView) convertView
                        .findViewById(R.id.txt_detail);
                holder.headImg = (CircularImage) convertView
                        .findViewById(R.id.img_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderTop) convertView.getTag();
            }
            if (typeString.equals("头像")) {
                holder.detail.setVisibility(View.GONE);
                holder.headImg.setVisibility(View.VISIBLE);
                if (detail == null || detail.equals("")
                        || !detail.startsWith("http")) {
                    holder.headImg.setImageResource(R.drawable.head_bg);
                } else {
                    UniversalImageLoadTool.disPlay(detail, holder.headImg,
                            R.drawable.head_bg);

                }
            } else {
                holder.detail.setVisibility(View.VISIBLE);
                holder.headImg.setVisibility(View.GONE);
                holder.detail.setText(detail);
                if ("MAN".equals(detail)) {
                    holder.detail.setText("男");
                } else if ("WOMAN".equals(detail)) {
                    holder.detail.setText("女");
                }
            }
            holder.type.setText(oprition);

            return convertView;
        }
    }

    class ChooseAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ChooseAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return circles.size();
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
        public View getView(final int position, View convertView,
                ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder = null;
            String circleName = circles.get(position).getName();
            String circleLogo = circles.get(position).getLogo();
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.second_list_item, null);
                viewHolder.circleName = (TextView) convertView
                        .findViewById(R.id.circle_name);
                viewHolder.circleImg = (CircularImage) convertView
                        .findViewById(R.id.circle_img);
                viewHolder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.circleName.setText(StringUtils.ToDBC(circleName));
            // if (circleLogo == null || circleLogo.equals("")
            // || !circleLogo.startsWith("http")) {
            // viewHolder.circleImg.setImageResource(R.drawable.pic_bg_no);
            // } else {
            UniversalImageLoadTool.disPlay(circleLogo, viewHolder.circleImg,
                    R.drawable.pic_bg_no);
            // }

            viewHolder.checkBox.setChecked(circles.get(position).isNew());
            viewHolder.checkBox.setClickable(false);
            return convertView;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                final Dialog dialog;
                dialog = DialogUtil.getWaitDialog(this, "请稍候");
                dialog.show();
                StringBuffer amendmentIds = new StringBuffer();
                int a = 0;
                for (int i = 0; i < lists.size(); i++) {
                    amendmentIds.append(lists.get(i).getId() + ",");
                }
                if (circles.size() > 0) {
                    amendmentIds.deleteCharAt(amendmentIds.length() - 1);
                }
                final StringBuffer cids = new StringBuffer();
                for (int i = 0; i < circles.size(); i++) {
                    if (circles.get(i).isNew()) {
                        a++;
                        cids.append(circles.get(i).getId() + ",");
                    }
                }
                if (a > 0) {
                    cids.deleteCharAt(cids.length() - 1);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", uid);
                map.put("token", token);
                if (cid == 0) {
                    map.put("cid", -1);
                } else {
                    map.put("cid", cid);
                }
                map.put("amendmentIds", amendmentIds);
                map.put("cids", cids);
                PostAsyncTask task = new PostAsyncTask(this, map, PATH);
                task.setTaskCallBack(new PostCallBack() {
                    @Override
                    public void taskFinish(String result) {
                        dialog.dismiss();
                        JSONObject object;
                        try {
                            object = new JSONObject(result);
                            int rt = object.getInt("rt");
                            if (rt == 1) {
                                Utils.showToast("同步成功！", Toast.LENGTH_SHORT);
                                if (cids.toString().contains("-1")) {
                                    Intent intent = new Intent(
                                            Constants.REFUSH_MYCARD_FRMO_NET);
                                    intent.putExtra("isFefushMycardFragment",
                                            true);
                                    BroadCast
                                            .sendBroadCast(
                                                    SyncchronousInformationActivity.this,
                                                    intent);
                                }
                                exit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                task.execute();
                break;
            case R.id.back:
                exit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (circles.get(arg2).isNew()) {
            circles.get(arg2).setNew(false);
        } else {
            circles.get(arg2).setNew(true);
        }
        cAdapter.notifyDataSetChanged();
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
}
