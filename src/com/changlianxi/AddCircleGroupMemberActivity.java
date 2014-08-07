package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.AddCircleGroupMemberAdapter;
import com.changlianxi.adapter.AddCircleGroupMemberAdapter.ViewHolder;
import com.changlianxi.contentprovider.CircleMemberProvider;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.db.DBUtils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddCircleGroupMemberActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    private ListView mListView;
    private ImageView back;
    private TextView title;
    private int cid;
    private AddCircleGroupMemberAdapter adapter;
    private List<CircleMember> listMembers = new ArrayList<CircleMember>();
    private CircleMemberList circleMemberList;
    private AsyncQueryHandler asyncQuery;
    private LinearLayout layButtom;
    private Button btnFinish;
    private LinearLayout addicon;
    private HorizontalScrollView scrollview;
    private List<CircleMember> selectMembers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_circle_group_member);
        getDataFromPreActivity();
        initView();
    }

    private void initView() {
        addicon = (LinearLayout) findViewById(R.id.addicon);
        scrollview = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        mListView = (ListView) findViewById(R.id.listView);
        layButtom = (LinearLayout) findViewById(R.id.layBottom);
        btnFinish = (Button) findViewById(R.id.btnfinish);
        setListener();
    }

    private void setListener() {
        btnFinish.setOnClickListener(this);
        back.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        setValue();
    }

    private void setValue() {
        title.setText("添加分组成员");
        circleMemberList = new CircleMemberList(cid);
        circleMemberList.read(DBUtils.getDBsa(1));
        listMembers = circleMemberList.getLegalMembers();
        adapter = new AddCircleGroupMemberAdapter(this, listMembers);
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
                CircleMemberProvider.CircleMemberColumns.STATE,
                CircleMemberProvider.CircleMemberColumns.SORT_KEY, }; // 查询的列
        asyncQuery.startQuery(0, null,
                CircleMemberProvider.CircleMemberColumns.CONTENT_URI,
                projection,
                CircleMemberProvider.CircleMemberColumns.CID + "=?",
                new String[] { cid + "" }, "sortkey COLLATE LOCALIZED asc");
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
                    String state = cursor.getString(6);
                    if (!CircleMemberState.notInCircle(CircleMemberState
                            .convert(state))) {
                        continue;
                    }
                    CircleMember member = new CircleMember(cid);
                    member.setName(cursor.getString(0));
                    member.setUid(cursor.getInt(1));
                    member.setPid(cursor.getInt(2));
                    member.setEmployer(cursor.getString(3));
                    member.setCellphone(cursor.getString(4));
                    member.setAvatar(cursor.getString(5));
                    member.setSortkey(cursor.getString(7));

                    listMembers.add(member);
                    cursor.moveToNext();
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.activity_add_circle_group_member, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
            long arg3) {
        layButtom.setVisibility(View.VISIBLE);
        ViewHolder holder = (ViewHolder) view.getTag();
        // 在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。
        holder.checkBox.toggle();
        boolean check = holder.checkBox.isChecked();
        adapter.isSelected.put(position, check);
        adapter.notifyDataSetChanged();
        if (check) {
            addImg(listMembers.get(position).getAvatar(),
                    listMembers.get(position).getName(), position);
        } else {
            delicon(position);
        }
        if (addicon.getChildCount() == 0) {
            layButtom.setVisibility(View.GONE);
        } else {
            btnFinish.setText("确定（ " + addicon.getChildCount() + "）");
        }
    }

    private void addImg(String avatarURl, String name, int position) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.select_circle_group_buttom, null);
        CircularImage img = (CircularImage) view.findViewById(R.id.img_avatar);
        TextView lastName = (TextView) view.findViewById(R.id.lastName);
        if (!"".equals(name)) {
            lastName.setText(name.substring(name.length() - 1));
            if ("".equals(avatarURl)) {
                img.setVisibility(View.GONE);
            } else {
                Bitmap bmp = ImageLoader.getInstance().loadImageSync(avatarURl);
                img.setImageBitmap(bmp);
            }
        }
        view.setTag(position);
        addicon.addView(view);
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                int off = addicon.getMeasuredWidth() - scrollview.getWidth();
                if (off > 0) {
                    scrollview.smoothScrollTo(off, 0);
                }

            }
        }, 100);
    }

    private void delicon(int position) {
        for (int i = 0; i < addicon.getChildCount(); i++) {
            if (addicon.getChildAt(i).getTag().equals(position)) {
                addicon.removeViewAt(i);
                break;
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btnfinish:
                selectMembers = new ArrayList<CircleMember>();
                for (int i = 0, len = adapter.getCount(); i < len; i++) {
                    View view = adapter.getView(i, null, mListView);
                    CheckBox box = (CheckBox) view.findViewById(R.id.checkBox1);
                    if (box.isChecked()) {
                        selectMembers.add(listMembers.get(i));
                    }

                }
                Bundle b = new Bundle();
                Intent intent = new Intent();
                b.putSerializable("members", (Serializable) selectMembers);
                intent.putExtras(b);
                setResult(2, intent);
                exit();
                break;
            default:
                break;
        }
    }
}
