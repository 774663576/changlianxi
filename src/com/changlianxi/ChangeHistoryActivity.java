package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.EditData;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.db.DBUtils;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;

/**
 * 圈子成员资料历史修改记录界面
 * @author LG
 *
 */
public class ChangeHistoryActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    private MyAdapter adapter;
    private ListView listView;
    private String PATH = "people/imyHistory";
    private int uid;
    private String token = "";
    private int cid;
    private TextView title_name;
    private List<EditData> lists = new ArrayList<EditData>();
    private List<EditData> lists2;
    private Map<String, Object> map = new HashMap<String, Object>();
    private ImageView back;
    private String headicon;
    private Dialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_history);
        pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
        listView = (ListView) findViewById(R.id.history_list);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setVerticalScrollBarEnabled(true);
        title_name = (TextView) findViewById(R.id.titleTxt);
        cid = getIntent().getIntExtra("cid", 0);
        uid = getIntent().getIntExtra("uid", 0);
        headicon = getIntent().getExtras().getString("headicon");
        title_name.setText("修改历史");
        token = SharedUtils.getString("token", "");
        map.put("uid", uid);
        map.put("cid", cid);
        map.put("token", token);
        getInformationTask(map, PATH);
        adapter = new MyAdapter(this);
    }

    private void getInformationTask(Map<String, Object> map, String path) {
        PostAsyncTask task = new PostAsyncTask(ChangeHistoryActivity.this, map,
                path);
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                pd.dismiss();
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    int rt = object.getInt("rt");
                    if (rt == 1) {
                        JSONArray jsonArr = object.getJSONArray("amendments");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArr.opt(i);
                            int id = obj.getInt("id");
                            int user_id = obj.getInt("user_id");
                            String type = obj.getString("type");
                            String operation = obj.getString("operation");
                            String detail = obj.getString("detail");
                            String time = obj.getString("time");
                            EditData cHistory = new EditData();
                            PersonDetailType pType = PersonDetailType
                                    .convertToType(type);
                            cHistory.setId(id);
                            cHistory.setUser_id(user_id);
                            cHistory.setType(pType);
                            if (pType == PersonDetailType.D_GENDAR) {
                                if ("MAN".equals(detail)) {
                                    detail = "男";
                                } else {
                                    detail = "女";
                                }
                            }
                            cHistory.setOperation(operation);
                            cHistory.setDetail(detail);
                            cHistory.setTime(time);
                            lists.add(cHistory);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Utils.showToast("获取信息失败", Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute();
    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView content;
        TextView type;
        CircularImage avatar;
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
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
            ViewHolder holder = null;
            CircleMember c = new CircleMember(cid, 0, lists.get(position)
                    .getUser_id());
            c.getNameAndAvatar(DBUtils.getDBsa(1));
            String time = lists.get(position).getTime();
            String value = lists.get(position).getDetail();
            String type = lists.get(position).getOperation() + ":";
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.activity_change_history_item, null);
                holder.name = (TextView) convertView
                        .findViewById(R.id.history_name);
                holder.time = (TextView) convertView
                        .findViewById(R.id.history_time);
                holder.type = (TextView) convertView
                        .findViewById(R.id.history_type);
                holder.content = (TextView) convertView
                        .findViewById(R.id.history_content);
                holder.avatar = (CircularImage) convertView
                        .findViewById(R.id.img_ava);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (lists.get(position).getType() == PersonDetailType.D_AVATAR) {
                holder.content.setVisibility(View.GONE);
                holder.avatar.setVisibility(View.VISIBLE);
                String path = StringUtils.JoinString(value, "_160x160");
                // FinalBitmapLoadTool.display(
                // StringUtils.JoinString(value, "_160x160"),
                // holder.avatar, R.drawable.head_bg);
                UniversalImageLoadTool.disPlay(path, new RotateImageViewAware(
                        holder.avatar, path), R.drawable.head_bg);
            } else {
                holder.content.setVisibility(View.VISIBLE);
                holder.avatar.setVisibility(View.GONE);
                holder.content.setText(value);
            }
            holder.name.setText(c.getName());
            holder.time.setText(DateUtils.publishedTime3(time));
            holder.type.setText(type);
            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        lists2 = new ArrayList<EditData>();
        EditData cHistory = new EditData();
        PersonDetailType type = lists.get(arg2).getType();
        String detail = lists.get(arg2).getDetail();
        int id = lists.get(arg2).getId();
        cHistory.setType(type);
        cHistory.setDetail(detail);
        cHistory.setId(id);
        lists2.add(cHistory);
        Intent intent = new Intent();
        intent.setClass(this, SyncchronousInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("change", (Serializable) lists2);
        intent.putExtras(bundle);
        intent.putExtra("uid", uid);
        intent.putExtra("cid", cid);
        intent.putExtra("headicon", headicon);
        startActivity(intent);
        Utils.leftOutRightIn(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;

            default:
                break;
        }
    }

}
