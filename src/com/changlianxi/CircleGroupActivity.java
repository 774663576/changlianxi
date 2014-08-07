package com.changlianxi;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.CircleGroupAdapter;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleGroup;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.DelGroupListener;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.SetCircleGroupTask;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.DrawableCenterTextView;

public class CircleGroupActivity extends BaseActivity implements
        OnClickListener, DelGroupListener, OnItemClickListener {
    private ImageView back;
    private TextView title;
    private DrawableCenterTextView btnAddGroup;
    private ListView mListView;
    private Button btnSave;
    private CircleGroupAdapter adapter;
    private List<CircleGroup> lists = new ArrayList<CircleGroup>();
    private List<CircleGroup> editListsGroups = new ArrayList<CircleGroup>();
    private Circle circle;
    private int cid;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_group);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.txt_title);
        btnAddGroup = (DrawableCenterTextView) findViewById(R.id.btn_add_group);
        mListView = (ListView) findViewById(R.id.listView);
        btnSave = (Button) findViewById(R.id.btn_save);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnAddGroup.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        setValue();
    }

    private void setValue() {
        title.setText("圈子分组");
        getDataFromPreActivity();
        circle = new Circle(cid);
        circle.readCircleGorups(DBUtils.getDBsa(1));
        lists = circle.getGroups();
        adapter = new CircleGroupAdapter(lists, this, this);
        mListView.setAdapter(adapter);
    }

    private void getDataFromPreActivity() {
        cid = getIntent().getIntExtra("cid", 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_add_group:
                startActivityForResult(new Intent(this,
                        InputCircleGroupNameActivity.class), 2);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btn_save:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {
        for (CircleGroup group : lists) {
            if (group.getGroupsId() == 0) {
                editListsGroups.add(group);
            }
        }
        if (editListsGroups.size() == 0) {
            return;
        }
        setGroup();
    }

    private void setGroup() {
        dialog = DialogUtil.getWaitDialog(this, "请稍等");
        dialog.show();
        SetCircleGroupTask task = new SetCircleGroupTask(editListsGroups);
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                Utils.showToast("保存成功", Toast.LENGTH_SHORT);
                adapter.notifyDataSetChanged();
                editListsGroups.clear();
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(circle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || requestCode != 2) {
            return;
        }
        String name = data.getStringExtra("name");
        CircleGroup group = new CircleGroup(cid, 0, name);
        lists.add(group);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void delGroup(int position) {
        if (lists.get(position).getGroupsId() != 0) {
            editListsGroups.add(lists.get(position));
        }
        lists.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent intent = new Intent();
        intent.putExtra("cid", cid);
        intent.putExtra("groupId", lists.get(position).getGroupsId());
        intent.setClass(this, EditCircleGroupActivity.class);
        startActivity(intent);
        Utils.leftOutRightIn(this);
    }
}
