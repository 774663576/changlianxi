package com.changlianxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.EditCircleGroupAdapter;
import com.changlianxi.contentprovider.CircleMemberProvider;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.DelGroupListener;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.SetCircleManagerTask;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.DrawableCenterTextView;

public class CircleManagerActivity extends BaseActivity implements
        OnClickListener, DelGroupListener {
    private ImageView back;
    private TextView title;
    private DrawableCenterTextView btnAddManager;
    private ListView mListView;
    private Button btnSave;
    private EditCircleGroupAdapter adapter;
    private int cid;
    private Dialog dialog;
    private List<CircleMember> oldListMembers = new ArrayList<CircleMember>();
    private List<CircleMember> newListMembers = new ArrayList<CircleMember>();
    private Circle circle;
    private AsyncQueryHandler asyncQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_manager);
        getDataFromPreActivity();
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.txt_title);
        btnAddManager = (DrawableCenterTextView) findViewById(R.id.btn_add_manager);
        mListView = (ListView) findViewById(R.id.listView);
        btnSave = (Button) findViewById(R.id.btn_save);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnAddManager.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        title.setText("设置圈子管理员");
        adapter = new EditCircleGroupAdapter(this, this, newListMembers);
        mListView.setAdapter(adapter);
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        initQuery();
    }

    private void getDataFromPreActivity() {
        cid = getIntent().getIntExtra("cid", 0);
    }

    private void initQuery() {
        String[] projection = { CircleMemberProvider.CircleMemberColumns.NAME,
                CircleMemberProvider.CircleMemberColumns.UID,
                CircleMemberProvider.CircleMemberColumns.PID,
                CircleMemberProvider.CircleMemberColumns.EMPLAYER,
                CircleMemberProvider.CircleMemberColumns.CELL_PHONE,
                CircleMemberProvider.CircleMemberColumns.AVATAR,
                CircleMemberProvider.CircleMemberColumns.ISMANAGER,
                CircleMemberProvider.CircleMemberColumns.SORT_KEY, }; // 查询的列
        asyncQuery.startQuery(0, null,
                CircleMemberProvider.CircleMemberColumns.CONTENT_URI,
                projection, CircleMemberProvider.CircleMemberColumns.CID
                        + "=? and "
                        + CircleMemberProvider.CircleMemberColumns.ISMANAGER
                        + "=?", new String[] { cid + "", "1" },
                "sortkey COLLATE LOCALIZED asc");
    }

    /**
     * 数据库异步查询类AsyncQueryHandler
     * 
     * 
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 查询结束的回调函数
         */
        @Override
        protected void onQueryComplete(int token, Object cookie,
                final Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    CircleMember member = new CircleMember(cid);
                    member.setName(cursor.getString(0));
                    member.setUid(cursor.getInt(1));
                    member.setPid(cursor.getInt(2));
                    member.setEmployer(cursor.getString(3));
                    member.setCellphone(cursor.getString(4));
                    member.setAvatar(cursor.getString(5));
                    member.setSortkey(cursor.getString(7));
                    member.setManager(cursor.getInt(6) == 1);
                    newListMembers.add(member);
                    cursor.moveToNext();
                }
            }
            Collections.addAll(oldListMembers,
                    new CircleMember[newListMembers.size()]);
            Collections.copy(oldListMembers, newListMembers);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_add_manager:
                Intent intent = new Intent();
                intent.putExtra("cid", cid);
                intent.setClass(this, AddCircleGroupMemberActivity.class);
                startActivityForResult(intent, 2);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btn_save:
                StringBuilder sb = new StringBuilder();
                for (CircleMember m : newListMembers) {
                    sb.append(m.getPid() + ",");
                }
                setManagers(sb.toString());
                break;
            default:
                break;
        }
    }

    private void setManagers(String managers) {
        dialog = DialogUtil.getWaitDialog(this, "请稍后");
        dialog.show();
        circle = new Circle(cid);
        SetCircleManagerTask task = new SetCircleManagerTask(oldListMembers,
                newListMembers);
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
        task.executeWithCheckNet(circle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2 || data == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<CircleMember> selectMembers = (List<CircleMember>) data
                .getExtras().getSerializable("members");
        newListMembers.addAll(selectMembers);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void delGroup(int position) {
        newListMembers.remove(position);
        adapter.notifyDataSetChanged();
    }

}
