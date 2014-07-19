package com.changlianxi;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.changlianxi.adapter.HomeSearchAdapter;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.SearchEditText;

public class CircleHomeSearchActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    private Button btnCancle;
    private ListView searchListView;
    private SearchEditText searchEdit;
    private List<CircleMember> searchListModles = new ArrayList<CircleMember>();// 存储搜索列表
    private CircleMemberList cLsit = new CircleMemberList(0);
    private HomeSearchAdapter searchAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_home_search);
        intView();
    }

    private void intView() {
        btnCancle = (Button) findViewById(R.id.btnCancle);
        searchListView = (ListView) findViewById(R.id.search_list);
        searchEdit = (SearchEditText) findViewById(R.id.edit_search);

        setListener();
    }

    private void setListener() {
        searchEdit.addTextChangedListener(new EditWather());
        btnCancle.setOnClickListener(this);
        searchListView.setOnItemClickListener(this);
    }

    class EditWather implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            String key = s.toString().toLowerCase();
            if (key.length() == 0) {
                Utils.hideSoftInput(CircleHomeSearchActivity.this);
                searchEdit.setCompoundDrawables(null, null, null, null);
                searchListModles.clear();
                searchAdapter.setData(searchListModles);
                return;
            }
            Drawable del = CircleHomeSearchActivity.this.getResources()
                    .getDrawable(R.drawable.del);
            del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
            searchEdit.setCompoundDrawables(null, null, del, null);
            searchListModles.clear();
            searchListModles = cLsit.fuzzyQuery(key, DBUtils.getDBsa(1));

            if (searchAdapter == null) {
                searchAdapter = new HomeSearchAdapter(
                        CircleHomeSearchActivity.this, searchListModles);
                searchListView.setAdapter(searchAdapter);
            } else {
                searchAdapter.setData(searchListModles);

            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancle:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Circle c = new Circle(searchListModles.get(position).getCid());
        c.read(DBUtils.getDBsa(1));
        if (c.isNew()) {
            Utils.showToast("亲，加入圈子以后才能看到这些精彩内容哦！", Toast.LENGTH_SHORT);
            return;
        }
        Intent it = new Intent();
        it.setClass(this, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        CircleMember member = searchListModles.get(position);
        member.getMemberState(DBUtils.getDBsa(1));
        bundle.putSerializable("member", member);
        it.putExtras(bundle);
        startActivity(it);
        Utils.leftOutRightIn(this);
    }

}
