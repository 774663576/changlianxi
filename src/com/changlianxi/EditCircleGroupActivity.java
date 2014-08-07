package com.changlianxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.EditCircleGroupAdapter;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleGroup;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.DelGroupListener;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.GetCircleGroupMembersTask;
import com.changlianxi.task.SetCircleGroupMemberTask;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.DrawableCenterTextView;

public class EditCircleGroupActivity extends BaseActivity implements
        OnClickListener, DelGroupListener {
    private ImageView back;
    private TextView title;
    private DrawableCenterTextView btnAddGroup;
    private ListView mListView;
    private Button btnSave;
    private EditCircleGroupAdapter adapter;
    private Circle circle;
    private int cid;
    private Dialog dialog;
    private List<CircleMember> listMembers = new ArrayList<CircleMember>();
    private int groupId;
    private CircleMemberList memberList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_circle_group);
        getDataFromPreActivity();
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.txt_title);
        btnAddGroup = (DrawableCenterTextView) findViewById(R.id.btn_add_group_member);
        mListView = (ListView) findViewById(R.id.listView);
        btnSave = (Button) findViewById(R.id.btn_save);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnAddGroup.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        circle = new Circle(cid);
        title.setText("分组成员");
        memberList = new CircleMemberList(cid);
        adapter = new EditCircleGroupAdapter(this, this, listMembers);
        mListView.setAdapter(adapter);
        getMembers();
    }

    private void getDataFromPreActivity() {
        cid = getIntent().getIntExtra("cid", 0);
        groupId = getIntent().getIntExtra("groupId", 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_add_group_member:
                Intent intent = new Intent();
                intent.putExtra("cid", cid);
                intent.setClass(this, AddCircleGroupMemberActivity.class);
                startActivityForResult(intent, 2);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btn_save:
                StringBuilder sb = new StringBuilder();
                for (CircleMember m : listMembers) {
                    sb.append(m.getPid() + ",");
                }
                setGroupMembers(sb.toString());
                break;
            default:
                break;
        }
    }

    private void getMembers() {
        GetCircleGroupMembersTask task = new GetCircleGroupMembersTask(groupId);
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                Collections.addAll(listMembers, new CircleMember[memberList
                        .getMembers().size()]);
                Collections.copy(listMembers, memberList.getMembers());
                adapter.setData(listMembers);
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(memberList);
    }

    private void setGroupMembers(String membersID) {
        CircleGroup group = new CircleGroup(cid, groupId, "");
        dialog = DialogUtil.getWaitDialog(this, "请稍后");
        dialog.show();
        SetCircleGroupMemberTask task = new SetCircleGroupMemberTask(
                listMembers, memberList.getMembers());
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

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(group);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2 || data == null) {
            return;
        }
        List<CircleMember> selectMembers = (List<CircleMember>) data
                .getExtras().getSerializable("members");
        listMembers.addAll(selectMembers);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void delGroup(int position) {
        listMembers.remove(position);
        adapter.notifyDataSetChanged();
    }

}
