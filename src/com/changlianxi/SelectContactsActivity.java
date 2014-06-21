package com.changlianxi;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.IinviteCircleMemberTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.PinYinUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 从通讯录导入圈子程序界面
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, OnTouchingLetterChangedListener,
        touchUp {
    private ListView listview;// 显示联系人的列表
    private LinearLayout layBot;// 用来显示或隐藏选择数量
    private Button btfinish;
    private ImageView back;
    private LinearLayout addicon;
    private ContactsAdapter adapter;
    private String type;
    private int cid;
    private List<ContactModle> listModle = new ArrayList<ContactModle>();
    private List<ContactModle> selectLists = new ArrayList<ContactModle>();
    private List<ContactModle> searchListModles = new ArrayList<ContactModle>();// 存储搜索列表
    private TextView titleTxt;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar indexBar;// 右侧字母拦
    private TextView selectedChar;// 显示选择字母
    private int position;// 当前字母子listview中所对应的位置
    private SearchEditText editSearch;
    private Dialog dialog;
    private CircleMember member;
    private HorizontalScrollView scrollview;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Cursor cursor = (Cursor) msg.obj;
                    addContact(cursor);
                    break;
                case 1:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        };
    };

    private void addContact(Cursor cursor) {
        String name = cursor.getString(0);
        String number = cursor.getString(1);
        number = StringUtils.StringFilter(number);
        number = StringUtils.cutHead(number, "86");
        if (!Utils.isPhoneNum(number)) {
            return;
        }
        String sortKey = cursor.getString(2);
        int contactId = cursor.getInt(3);
        Long photoId = cursor.getLong(4);
        ContactModle modle = new ContactModle();
        modle.setName(name);
        modle.setNum(number);
        modle.setSort_key(sortKey.replace(" ", ""));
        modle.setPhotoid(photoId);
        modle.setContactid((long) contactId);
        listModle.add(modle);
        // adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        CLXApplication.addInviteActivity(this);
        type = getIntent().getStringExtra("type");
        initView();
        titleTxt.setText("添加第一批成员");
        if (type.equals("add")) {
            titleTxt.setText("添加成员");
            cid = getIntent().getIntExtra("cid", 0);
        }
        adapter = new ContactsAdapter(listModle);
        listview.setAdapter(adapter);
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        init();

    }

    /**设置页面统计
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
    protected void onDestroy() {
        super.onDestroy();

    }

    private void init() {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones"); // 联系人的Uri
        // 联系人的Uri
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
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
                new Thread() {
                    public void run() {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            // Message msg = mHandler.obtainMessage();
                            // msg.what = 0;
                            // msg.obj = cursor;
                            // // mHandler.sendMessage(msg);
                            // mHandler.sendMessageDelayed(msg, 1);
                            addContact(cursor);
                            if (SelectContactsActivity.this.isFinishing()) {
                                break;
                            }

                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }.start();

                new Thread() {
                    public void run() {
                        for (ContactModle modle : listModle) {
                            if (SelectContactsActivity.this.isFinishing()) {
                                break;
                            }
                            modle.setKey_pinyin_fir(PinYinUtils.getFirstPinYin(
                                    modle.getName()).replace(" ", ""));
                        }
                    }
                }.start();
            }
        }
    }

    /**
     * 初始各个化控件
     */
    private void initView() {
        addicon = (LinearLayout) findViewById(R.id.addicon);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        btfinish = (Button) findViewById(R.id.btnfinish);
        btfinish.setOnClickListener(this);
        layBot = (LinearLayout) findViewById(R.id.layBottom);
        listview = (ListView) findViewById(R.id.contactList);
        listview.setOnItemClickListener(this);
        listview.setCacheColorHint(0);
        View view = LayoutInflater.from(this).inflate(R.layout.header, null);
        editSearch = (SearchEditText) view.findViewById(R.id.search);
        editSearch.setFocusable(true);
        editSearch.setFocusableInTouchMode(true);
        editSearch.addTextChangedListener(new EditWather());
        listview.addHeaderView(view);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
        indexBar.setOnTouchingLetterChangedListener(this);
        indexBar.getBackground().setAlpha(0);
        indexBar.setOnTouchUp(this);
        selectedChar = (TextView) findViewById(R.id.selected_tv);
        selectedChar.setVisibility(View.INVISIBLE);
        scrollview = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        listview.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Utils.hideSoftInput(CLXApplication.getInstance());
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

            }
        });

    }

    class EditWather implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            String key = s.toString().toLowerCase();
            if (key.length() == 0) {
                adapter.setData(listModle);
                searchListModles.clear();
                indexBar.setVisibility(View.VISIBLE);
                Utils.hideSoftInput(SelectContactsActivity.this);
                editSearch.setCompoundDrawables(null, null, null, null);
                return;
            }
            Drawable del = getResources().getDrawable(R.drawable.del);
            del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
            editSearch.setCompoundDrawables(null, null, del, null);
            indexBar.setVisibility(View.GONE);
            layBot.setVisibility(View.GONE);
            searchListModles.clear();
            for (int i = 0; i < listModle.size(); i++) {
                String name = listModle.get(i).getName();
                String pinyin = listModle.get(i).getSort_key().toLowerCase();
                String pinyinFir = listModle.get(i).getKey_pinyin_fir()
                        .toLowerCase();
                String mobileNum = listModle.get(i).getNum();

                if (name.contains(key) || pinyin.contains(key)
                        || pinyinFir.contains(key) || mobileNum.contains(key)) {
                    ContactModle modle = listModle.get(i);
                    searchListModles.add(modle);

                }
            }
            adapter.setData(searchListModles);
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

    /** 得到手机通讯录联系人信息 **/

    private void delicon(String position) {
        if (position != null) {
            for (int i = 0; i < addicon.getChildCount(); i++) {
                if (addicon.getChildAt(i).getTag().equals(position)) {
                    addicon.removeViewAt(i);
                    break;
                }
            }
        }

    }

    private void addImg(Bitmap bmp, String tag, String name) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.select_contact_buttom, null);
        CircularImage img = (CircularImage) view.findViewById(R.id.img);
        TextView lastName = (TextView) view.findViewById(R.id.lastName);
        lastName.setText(name.substring(name.length() - 1));
        view.setTag(tag);
        if (bmp == null) {
            lastName.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
        } else {
            img.setImageBitmap(bmp);
            lastName.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
        }
        addicon.addView(view);

        img.postDelayed(new Runnable() {

            @Override
            public void run() {

                int off = addicon.getMeasuredWidth() - scrollview.getWidth();
                if (off > 0) {
                    scrollview.smoothScrollTo(off, 0);
                }

            }
        }, 100);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long id) {
        layBot.setVisibility(View.VISIBLE);
        int position = arg2 - 1;
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.check.toggle();
        String name = "";
        String num = "";
        String tag = "";
        if (searchListModles.size() > 0) {
            name = searchListModles.get(position).getName();
            num = searchListModles.get(position).getNum();
            tag = name + num;
            searchListModles.get(position).setChecked(holder.check.isChecked());
            adapter.notifyDataSetChanged();
            if (holder.check.isChecked()) {
                Bitmap bmp = searchListModles.get(position).getBmp();
                addImg(bmp, tag, name);
                ContactModle m = (ContactModle) adapter.getItem(position);
                m.setTag(tag);
                addSelectList(m);
            } else {
                delicon(tag);
                delSelectList(tag);
            }
            btfinish.setText("完成(" + addicon.getChildCount() + ")");
            return;
        }
        listModle.get(position).setChecked(holder.check.isChecked());
        adapter.notifyDataSetChanged();
        name = listModle.get(position).getName();
        num = listModle.get(position).getNum();
        tag = name + num;
        if (holder.check.isChecked()) {
            Bitmap bmp = listModle.get(position).getBmp();
            addImg(bmp, tag, name);
            ContactModle m = (ContactModle) adapter.getItem(position);
            m.setTag(tag);
            addSelectList(m);
        } else {
            delicon(tag);
            delSelectList(tag);

        }
        btfinish.setText("完成(" + addicon.getChildCount() + ")");

    }

    private void addSelectList(ContactModle modle) {
        selectLists.add(modle);

    }

    private void delSelectList(String tag) {
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.get(i).getTag().equals(tag)) {
                selectLists.remove(i);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);

                break;
            case R.id.btnfinish:
                List<CircleMember> selectMembers = new ArrayList<CircleMember>();
                for (int i = 0; i < selectLists.size(); i++) {
                    ContactModle modle = selectLists.get(i);
                    String name = modle.getName();
                    String num = modle.getNum();
                    CircleMember smsModle = new CircleMember(cid);
                    smsModle.setName(name);
                    smsModle.setCellphone(num.replace(" ", ""));
                    selectMembers.add(smsModle);
                }

                if (type.equals("add")) {
                    if (selectMembers.size() == 0) {
                        Utils.showToast("请至少要选择一个联系人:)", Toast.LENGTH_SHORT);
                        return;
                    }
                    InviteMember(selectMembers);
                    return;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("contactsList",
                        (Serializable) selectMembers);
                intent.putExtras(bundle);
                intent.setClass(this, CreateCircleActivity.class);
                intent.putExtra("type", "more");
                startActivity(intent);
                finish();
                Utils.leftOutRightIn(this);
                break;
            case R.id.search:
                break;
            default:
                break;
        }

    }

    /**
     * 邀请 成员
     */
    private void InviteMember(final List<CircleMember> contactsList) {
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        member = new CircleMember(cid, 0, Global.getIntUid());
        IinviteCircleMemberTask task = new IinviteCircleMemberTask(contactsList);
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                BroadCast.sendBroadCast(SelectContactsActivity.this,
                        Constants.REFRESH_CIRCLE_USER_LIST);
                intentSmsPreviewActivity(contactsList);
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(member);
    }

    private void promptDialog(String str) {
        Dialog dialog = DialogUtil.promptDialog(this, str, "确定",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        CLXApplication.exitSmsInvite();
                        Utils.rightOut(SelectContactsActivity.this);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    /**
     * 跳转到短信预览界面
     */
    private void intentSmsPreviewActivity(List<CircleMember> contactsList) {
        if (contactsList.size() > 1) {
            inviteMore(contactsList);
            return;
        }
        String str = "添加成功";
        if (!"1".equals(contactsList.get(0).getInviteRt())) {
            str = "添加失败,请检查您的网络是否正常！";
        } else if ("".equals(contactsList.get(0).getInviteCode())) {
            str = "该用户已存在！";
        }
        promptDialog(str);
    }

    private void inviteMore(List<CircleMember> contactsList) {
        int successCount = 0;
        int existCount = 0;
        int failCount = 0;
        for (CircleMember member : contactsList) {
            if (!"1".equals(member.getInviteRt())) {
                failCount += 1;
                continue;
            }
            if ("".equals(member.getInviteCode())) {
                existCount += 1;
            }
        }
        successCount = contactsList.size() - failCount - existCount;

        String str = "添加完毕\n添加" + contactsList.size() + "人";
        if (successCount > 0) {
            str += ",成功" + successCount + "人";
        }
        if (existCount > 0) {
            str += ",存在" + existCount + "人";
        }
        if (failCount > 0) {
            str += ",失败" + failCount + "人";
        }
        promptDialog(str);
    }

    class ContactsAdapter extends BaseAdapter {
        ViewHolder holder = null;
        List<ContactModle> listData = new ArrayList<ContactModle>();

        public ContactsAdapter(List<ContactModle> listData) {
            this.listData = listData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectContactsActivity.this)
                        .inflate(R.layout.contact_list_item, null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                holder.laybg = (LinearLayout) convertView
                        .findViewById(R.id.laybg);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.img = (CircularImage) convertView.findViewById(R.id.img);
                holder.check = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);
                holder.num = (TextView) convertView.findViewById(R.id.num);
                holder.lastName = (TextView) convertView
                        .findViewById(R.id.lastName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = listData.get(position).getName();
            // 绘制联系人名称
            holder.name.setText(name);
            holder.lastName.setText(name.substring(name.length() - 1));
            // 绘制联系人号码
            holder.num.setText(listData.get(position).getNum());
            // 绘制联系人头像
            if (0 == listModle.get(position).getPhotoid()) {
                holder.img.setVisibility(View.GONE);
                holder.lastName.setVisibility(View.VISIBLE);
            } else {
                Uri uri = ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        listModle.get(position).getContactid());
                InputStream input = ContactsContract.Contacts
                        .openContactPhotoInputStream(
                                SelectContactsActivity.this
                                        .getContentResolver(), uri);
                Bitmap contactPhoto = BitmapFactory.decodeStream(input);
                listModle.get(position).setBmp(contactPhoto);
                holder.img.setImageBitmap(contactPhoto);
                holder.lastName.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
            }
            holder.check.setChecked(listData.get(position).isChecked());
            if (position % 2 == 0) {
                holder.laybg.setBackgroundColor(Color.WHITE);
            } else {
                holder.laybg.setBackgroundColor(getResources().getColor(
                        R.color.f6));
            }
            showAlpha(position, holder, listData);
            return convertView;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        public void setData(List<ContactModle> list) {
            this.listData = list;
            this.notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private void showAlpha(int position, ViewHolder holder,
            List<ContactModle> listData) {
        // 当前联系人的sortKey
        String currentStr = getAlpha(listData.get(position).getSort_key());
        // 上一个联系人的sortKey
        String previewStr = (position - 1) >= 0 ? getAlpha(listData.get(
                position - 1).getSort_key()) : " ";
        /**
         * 判断显示#、A-Z的TextView隐藏与显示
         */
        if (!previewStr.equals(currentStr)) { //
            // 当前联系人的sortKey�?上一个联系人的sortKey，说明当前联系人是新组�?
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
    }

    /**
     * 提取英文的首字母，非英文字母#代替
     * 
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 大写输出
        } else {
            return "#";
        }

    }

    class ViewHolder {
        TextView alpha;
        LinearLayout laybg;
        TextView name;
        CheckBox check;
        TextView num;
        CircularImage img;
        TextView lastName;
    }

    /**
     * 设置listview的当前选中值
     * 
     * @param s
     * @return
     */
    public int findIndexer(String s) {
        int position = 0;
        for (int i = 0; i < listModle.size(); i++) {
            String sortkey = listModle.get(i).getSort_key().toUpperCase();
            if (sortkey.startsWith(s)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        indexBar.getBackground().setAlpha(200);
        selectedChar.setText(s);
        selectedChar.setVisibility(View.VISIBLE);
        position = (findIndexer(s)) + 1;
        listview.setSelection(position);
    }

    @Override
    public void onTouchUp() {
        indexBar.getBackground().setAlpha(0);
        selectedChar.setVisibility(View.GONE);
        listview.setSelection(position);

    }
}