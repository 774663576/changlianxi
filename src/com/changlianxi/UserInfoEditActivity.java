package com.changlianxi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.popwindow.UserInfoEditSelectTypePopwindow;
import com.changlianxi.popwindow.UserInfoEditSelectTypePopwindow.OnSelectKey;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.UpLoadCircleMemberIdetailTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

/**
 * 圈子成员信息编辑界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoEditActivity extends BaseActivity implements
        OnClickListener {
    private List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
    private List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
    private List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
    private List<Info> addressList = new ArrayList<Info>();// 存放地址数据
    private List<Info> eduList = new ArrayList<Info>();// 存放教育经历
    private List<Info> workList = new ArrayList<Info>();// 存放工作经历
    private List<Info> emailList = new ArrayList<Info>();// 存放工作经历
    private List<Info> finalInfo = new ArrayList<Info>();
    private List<List<Info>> lists = new ArrayList<List<Info>>();
    private List<GroupModle> group = new ArrayList<GroupModle>();
    private Button back;
    private CircularImage avatar;
    private String strName = "";
    private String avatarURL;
    private RelativeLayout layParent;
    private ImageView avatarReplace;
    private RelativeLayout avatarLay;
    private Button btnSave;
    private Dialog dialog;
    private Calendar cal = Calendar.getInstance();
    private RelativeLayout layTop;
    private CircleMember circleMember;
    private CircleMember newCircleMember;
    private List<PersonDetail> newDetails;
    private ExpandableListView listView;
    private Adapter adapter;
    private final int TYPE_1 = 1;
    private final int TYPE_2 = 2;
    private SelectPicPopwindow pop;
    private String selectPicPath = "";
    private Bitmap avatarBitmap = null;
    private String specialKey[] = { "姓名", "性別", "生日", "单位" };
    private FinalBitmap fb;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    setBackGroubdOfDrable((Drawable) msg.obj);
                    break;
                case 2:
                    init();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit_activity);
        listView = (ExpandableListView) findViewById(R.id.expandableListView1);
        listView.setGroupIndicator(null);
        listView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                return true;
            }
        });
        mHandler.sendEmptyMessageDelayed(2, 200);
    }

    private void init() {
        initFB();
        avatarURL = getIntent().getStringExtra("avatar");
        initView();
        circleMember = (CircleMember) getIntent().getSerializableExtra(
                "circleMumber");
        setListener();
        setAvatar();
        filldata();
    }

    private void initFB() {
        fb = CLXApplication.getFb();
        fb.configLoadingImage(R.drawable.head_bg);
        fb.configLoadfailImage(R.drawable.head_bg);
    }

    @SuppressWarnings("unchecked")
    private void filldata() {
        Bundle bundle = getIntent().getExtras();
        basicList = (List<Info>) bundle.getSerializable("basicList");
        contactList = (List<Info>) bundle.getSerializable("contactList");
        socialList = (List<Info>) bundle.getSerializable("socialList");
        addressList = (List<Info>) bundle.getSerializable("addressList");
        eduList = (List<Info>) bundle.getSerializable("eduList");
        workList = (List<Info>) bundle.getSerializable("workList");
        emailList = (List<Info>) bundle.getSerializable("emailList");
        addMustType();
        // 复制一个相同的对象
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(circleMember);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(
                    byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            newCircleMember = (CircleMember) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        newDetails = newCircleMember.getDetails();
        notifyAdapter();
    }

    private void addMustType() {
        List<String> lists = new ArrayList<String>();
        for (Info info : basicList) {
            lists.add(info.getKey());
        }
        Info info = null;
        for (String str : specialKey) {
            if (!lists.contains(str)) {
                info = new Info();
                info.setKey(str);
                info.setType(UserInfoUtils.convertToEnglish(str));
                info.setEditType(2);
                info.setValue("");
                basicList.add(info);
            }
        }
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

    private void notifyAdapter() {
        GroupModle gmodle = null;
        gmodle = new GroupModle();
        gmodle.setTitle("");
        group.add(gmodle);
        lists.add(basicList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[0]);
        group.add(gmodle);
        lists.add(contactList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[1]);
        group.add(gmodle);
        lists.add(emailList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[2]);
        group.add(gmodle);
        lists.add(socialList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[3]);
        group.add(gmodle);
        lists.add(addressList);
        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[4]);
        group.add(gmodle);
        lists.add(eduList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[5]);
        group.add(gmodle);
        lists.add(workList);

        gmodle = new GroupModle();
        gmodle.setTitle(UserInfoUtils.infoTitleKey[6]);
        group.add(gmodle);
        lists.add(finalInfo);
        adapter = new Adapter();
        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);

        }
    }

    class GroupModle {
        String title = "";
        boolean isShow = true;
        boolean isAnim = false;

        public boolean isShow() {
            return isShow;
        }

        public void setShow(boolean isShow) {
            this.isShow = isShow;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private void initView() {
        layTop = (RelativeLayout) findViewById(R.id.top);
        layParent = (RelativeLayout) findViewById(R.id.parent);
        back = (Button) findViewById(R.id.back);
        avatar = (CircularImage) findViewById(R.id.avatar);
        btnSave = (Button) findViewById(R.id.btnSave);
        avatarLay = (RelativeLayout) findViewById(R.id.avatarLay);
        avatarReplace = (ImageView) findViewById(R.id.avatarRelace);
        avatarReplace.getBackground().setAlpha(200);
    }

    private void setAvatar() {
        Bitmap mBitmap = fb.getBitmapFromDiskCache(avatarURL,
                new BitmapDisplayConfig());
        if (mBitmap != null) {
            avatar.setImageBitmap(mBitmap);
            new BoxBlurFilterThread(mBitmap).start();
        } else {
            avatar.setImageResource(R.drawable.head_bg);
        }
    }

    @SuppressLint("NewApi")
    private void setBackGroubdOfDrable(Drawable darble) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layTop.setBackgroundDrawable(darble);
        } else {
            layTop.setBackground(darble);
        }
    }

    class BoxBlurFilterThread extends Thread {
        private Bitmap bmp = null;

        public BoxBlurFilterThread(Bitmap bmp) {
            this.bmp = bmp;
        }

        public void run() {
            Message msg = mHandler.obtainMessage(1);
            msg.obj = BitmapUtils.BoxBlurFilter(bmp);
            mHandler.sendMessage(msg);

        }
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        avatarLay.setOnClickListener(this);

    }

    class Adapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildTypeCount() {
            return 3;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            String viewType = group.get(groupPosition).getTitle();
            if (UserInfoUtils.infoTitleKey[4].equals(viewType)
                    || UserInfoUtils.infoTitleKey[5].equals(viewType)) {
                return TYPE_2;
            } else {
                return TYPE_1;
            }
        }

        @Override
        public View getChildView(final int groupPosition,
                final int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            ChildViewHolder cHolder = null;
            EduHolderValues eduHolder = null;
            int viewType = getItemViewType(groupPosition);
            // if (convertView == null) {
            switch (viewType) {
                case 1:
                    cHolder = new ChildViewHolder();
                    convertView = LayoutInflater
                            .from(UserInfoEditActivity.this).inflate(
                                    R.layout.user_info_edit_item, null);
                    cHolder.bg = (LinearLayout) convertView
                            .findViewById(R.id.bg);
                    cHolder.key = (TextView) convertView.findViewById(R.id.key);
                    cHolder.value = (EditText) convertView
                            .findViewById(R.id.value);
                    cHolder.btnDel = (ImageView) convertView
                            .findViewById(R.id.btnDel);
                    cHolder.text = (TextView) convertView
                            .findViewById(R.id.text);
                    convertView.setTag(cHolder);
                    break;
                case 2:
                    convertView = LayoutInflater
                            .from(UserInfoEditActivity.this).inflate(
                                    R.layout.user_info_edit_item_edu, null);
                    eduHolder = new EduHolderValues();
                    eduHolder.btnDel = (ImageView) convertView
                            .findViewById(R.id.btnDel);
                    eduHolder.key = (TextView) convertView
                            .findViewById(R.id.key);
                    eduHolder.value = (EditText) convertView
                            .findViewById(R.id.value);
                    eduHolder.startTime = (EditText) convertView
                            .findViewById(R.id.startTime);
                    eduHolder.endTime = (EditText) convertView
                            .findViewById(R.id.endTime);
                    convertView.setTag(eduHolder);
                    break;
                default:
                    break;
            }
            List<Info> valueList = lists.get(groupPosition);
            String tag = group.get(groupPosition).getTitle();
            String key = lists.get(groupPosition).get(childPosition).getKey();
            String value = lists.get(groupPosition).get(childPosition)
                    .getValue();
            int editType = lists.get(groupPosition).get(childPosition)
                    .getEditType();
            switch (viewType) {
                case 1:
                    if (isContainsSpecialKey(key) || key.equals("手机号")) {
                        cHolder.key
                                .setCompoundDrawables(null, null, null, null);
                    }
                    cHolder.key.setText(key);
                    cHolder.value.setText(value);
                    cHolder.btnDel.setTag(tag);
                    cHolder.key.setOnClickListener(new BtnKeyEditClick(
                            groupPosition, childPosition, key));
                    cHolder.value.addTextChangedListener(new EditTextWatcher(
                            valueList, childPosition, editType));
                    if ("手机号".equals(key)) {
                        cHolder.value.setFocusable(false);
                        cHolder.text.setVisibility(View.VISIBLE);
                        cHolder.btnDel.setVisibility(View.INVISIBLE);
                    } else if ("姓名".equals(key)) {
                        cHolder.btnDel.setVisibility(View.INVISIBLE);
                        cHolder.text.setVisibility(View.GONE);
                        cHolder.value.setFocusable(true);

                    } else {
                        cHolder.btnDel.setVisibility(View.VISIBLE);
                        cHolder.text.setVisibility(View.GONE);
                        cHolder.value.setFocusable(true);
                    }
                    if ("昵称".equals(key) || "备注".equals(key)) {
                        cHolder.text.setVisibility(View.VISIBLE);
                        cHolder.text.setText("(只对我可见)");
                    }
                    cHolder.btnDel.setOnClickListener(new BtnDelClick(
                            childPosition, (String) cHolder.btnDel.getTag(),
                            editType));
                    if (key.equals("QQ")) {
                        cHolder.value.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (Arrays.toString(
                            UserInfoUtils.contactPhoneChinesetStr)
                            .contains(key)) {
                        cHolder.value.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (Arrays.toString(UserInfoUtils.socialChineseStr)
                            .contains(key)) {
                        cHolder.value
                                .setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                    } else if (key.equals("性別")) {
                        cHolder.value.setFocusable(false);
                        cHolder.value.setOnClickListener(new BtnSelectGendar(
                                childPosition, editType));
                        if (value.equals("1") || value.equals("男")) {
                            cHolder.value.setText("男");
                        } else if (value.equals("2") || value.equals("女")) {
                            cHolder.value.setText("女");
                        }
                    } else if (key.equals("生日")) {
                        cHolder.value.setFocusable(false);
                        cHolder.value.setOnClickListener(new OnTimeClick("生日",
                                valueList, childPosition, editType));
                        cHolder.value.setText(value);

                    }
                    if (editType == 2 && value.equals("")) {
                        cHolder.value.requestFocus();
                    }

                    break;
                case 2:
                    eduHolder.btnDel.setTag(tag);
                    eduHolder.btnDel.setOnClickListener(new BtnDelClick(
                            childPosition, (String) eduHolder.btnDel.getTag(),
                            editType));
                    eduHolder.key.setText(key);
                    eduHolder.key.setOnClickListener(new BtnKeyEditClick(
                            groupPosition, childPosition, key));
                    if (value.equals("")) {
                        if (tag.equals(UserInfoUtils.infoTitleKey[4])) {
                            eduHolder.value.setHint("输入学校名称");
                        } else {
                            eduHolder.value.setHint("输入单位名称");
                        }
                    }
                    if (editType == 2 && value.equals("")) {
                        eduHolder.value.requestFocus();
                    }
                    eduHolder.value.addTextChangedListener(new EditTextWatcher(
                            valueList, childPosition, editType));
                    eduHolder.value.setText(value);
                    eduHolder.startTime.setText(DateUtils.interceptDateStr(
                            lists.get(groupPosition).get(childPosition)
                                    .getStartDate(), "yyyy-MM-dd"));
                    eduHolder.endTime.setText(DateUtils.interceptDateStr(
                            lists.get(groupPosition).get(childPosition)
                                    .getEndDate(), "yyyy-MM-dd"));
                    eduHolder.endTime.setOnClickListener(new OnTimeClick(
                            "endTime", valueList, childPosition, editType));
                    eduHolder.startTime.setOnClickListener(new OnTimeClick(
                            "startTime", valueList, childPosition, editType));
                    break;
                default:
                    break;
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return lists.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return group.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return group.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            GroupViewHoler gHolder = null;

            if (convertView == null) {
                gHolder = new GroupViewHoler();
                convertView = LayoutInflater.from(UserInfoEditActivity.this)
                        .inflate(R.layout.user_info_edit_group_item, null);
                gHolder.titleKey = (TextView) convertView
                        .findViewById(R.id.titleKey);
                gHolder.bg = (LinearLayout) convertView.findViewById(R.id.bg);
                gHolder.imgAdd = (ImageView) convertView
                        .findViewById(R.id.imgAdd);
                gHolder.line = (View) convertView.findViewById(R.id.line);
                convertView.setTag(gHolder);
            } else {
                gHolder = (GroupViewHoler) convertView.getTag();
            }
            gHolder.titleKey.setText(group.get(groupPosition).getTitle());
            gHolder.bg.setOnClickListener(new BtnGroupAddClick(
                    groupPosition - 1));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
            if (groupPosition == 0) {
                lp.setMargins(0, 0, 0, 0);
                gHolder.line.setLayoutParams(lp);
                gHolder.bg.setVisibility(View.GONE);
                gHolder.line.setVisibility(View.GONE);

            } else {
                lp.setMargins(0, 0, 0, 40);
                gHolder.line.setLayoutParams(lp);
                if (group.get(groupPosition).isShow) {
                    gHolder.bg.setVisibility(View.VISIBLE);
                } else {
                    gHolder.bg.setVisibility(View.GONE);

                }
                gHolder.line.setVisibility(View.VISIBLE);
                if (groupPosition == group.size() - 1) {
                    gHolder.line.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        public int getItemViewType(int groupPosition) {
            String viewType = group.get(groupPosition).getTitle();
            if (UserInfoUtils.infoTitleKey[4].equals(viewType)
                    || UserInfoUtils.infoTitleKey[5].equals(viewType)) {
                return TYPE_2;
            } else {
                return TYPE_1;
            }
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class ChildViewHolder {
        TextView key;
        EditText value;
        LinearLayout bg;
        ImageView btnDel;
        TextView text;
    }

    class GroupViewHoler {
        ImageView imgAdd;
        TextView titleKey;
        LinearLayout bg;
        View line;

    }

    class BtnSelectGendar implements OnClickListener {
        int position;
        int editType;

        public BtnSelectGendar(int position, int editType) {
            this.position = position;
            this.editType = editType;
        }

        @Override
        public void onClick(View v) {
            UserInfoEditSelectTypePopwindow pop = new UserInfoEditSelectTypePopwindow(
                    UserInfoEditActivity.this, layParent, new String[] { "男",
                            "女" }, "性别");
            pop.setCallBack(new OnSelectKey() {

                @Override
                public void getSelectKey(String str) {
                    if (editType != 2) {
                        basicList.get(position).setEditType(3);
                    }
                    basicList.get(position).setValue(str);
                    adapter.notifyDataSetChanged();
                }
            });
            pop.show();
        }

    }

    class OnTimeClick implements OnClickListener {
        String type = "";
        List<Info> valuesList;
        int position;
        int editType;

        public OnTimeClick(String type, List<Info> valuesList, int position,
                int editType) {
            this.type = type;
            this.valuesList = valuesList;
            this.position = position;
            this.editType = editType;
        }

        @Override
        public void onClick(View v) {
            showDateDialog(valuesList, position, type, editType);

        }

    }

    // 日期选择对话框的 DateSet 事件监听器
    class DateListener implements OnDateSetListener {
        List<Info> valuesList;
        int position;
        String tag;
        int editType;

        public DateListener(List<Info> valuesList, int position, String tag,
                int editType) {
            this.position = position;
            this.tag = tag;
            this.valuesList = valuesList;
            this.editType = editType;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(valuesList, position, tag, editType);
        }
    }

    private void showDateDialog(List<Info> valuesList, int position,
            String tag, int editType) {
        String date = "";
        int year = 0;
        int month = 0;
        int day = 0;
        if (tag.equals("startTime")) {
            date = valuesList.get(position).getStartDate();
        } else if (tag.equals("endTime")) {
            date = valuesList.get(position).getEndDate();
        } else if (tag.equals("生日")) {
            date = valuesList.get(position).getValue();
        }
        if ("".equals(date)) {
            if (tag.equals("生日")) {
                java.util.Date de = DateUtils.stringToDate("1990-01-01");
                cal.setTime(de);
            }
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            java.util.Date de = DateUtils.stringToDate(date);
            cal.setTime(de);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
        new DatePickerDialog(this, new DateListener(valuesList, position, tag,
                editType), year, month, day).show();
    }

    // 当 DatePickerDialog 关闭，更新日期显示
    private void updateDate(List<Info> valuesList, int position, String tag,
            int editType) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(cal.getTime());
        if (tag.equals("startTime")) {
            String endTime = valuesList.get(position).getEndDate();
            if (!endTime.equals("") && !endTime.equals("null")) {
                if (!DateUtils.compareDate(date, endTime)) {
                    Utils.showToast("亲，结束时间早于开始时间，爱因斯坦也做不到的。",
                            Toast.LENGTH_SHORT);
                }
            }
            if (editType == 2) {
                valuesList.get(position).setStartDate(date);
            } else {
                String values = valuesList.get(position).getStartDate();
                if (!values.equals(date.toString())) {
                    valuesList.get(position).setStartDate(date);
                    valuesList.get(position).setEditType(3);
                }
            }
        } else if (tag.equals("endTime")) {
            String startTime = valuesList.get(position).getStartDate();
            if (!startTime.equals("") && !startTime.equals("null")) {
                if (!DateUtils.compareDate(startTime, date)) {
                    Utils.showToast("亲，结束时间早于开始时间，爱因斯坦也做不到的。",
                            Toast.LENGTH_SHORT);
                    return;
                }
            }
            if (editType == 2) {
                valuesList.get(position).setEndDate(date);
            } else {
                String values = valuesList.get(position).getEndDate();
                if (!values.equals(date.toString())) {
                    valuesList.get(position).setEndDate(date);
                    valuesList.get(position).setEditType(3);
                }
            }
        } else if (tag.equals("生日")) {
            if (editType == 2) {
                valuesList.get(position).setValue(
                        DateUtils.interceptDateStr(date, "yyyy-MM-dd"));
            } else {
                String values = valuesList.get(position).getEndDate();
                if (!values.equals(date.toString())) {
                    valuesList.get(position).setValue(
                            DateUtils.interceptDateStr(date, "yyyy-MM-dd"));
                    valuesList.get(position).setEditType(3);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    class EduHolderValues {
        TextView key;
        EditText value;
        EditText startTime;
        EditText endTime;
        ImageView btnDel;
    }

    class EditTextWatcher implements TextWatcher {
        List<Info> valuesList;
        int position;
        int editType;

        public EditTextWatcher(List<Info> valuesList, int position, int type) {
            this.valuesList = valuesList;
            this.position = position;
            this.editType = type;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (position >= valuesList.size()) {
                return;
            }
            if (editType == 2) {
                valuesList.get(position).setValue(s.toString());
            } else {

                String values = valuesList.get(position).getValue();
                if (!values.equals(s.toString())) {
                    valuesList.get(position).setValue(s.toString());
                    valuesList.get(position).setEditType(3);
                }
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

    private void confirmDialog(String str, final int position, final String tag) {
        Dialog dialog = DialogUtil.confirmDialog(this, str, "确定", "取消",
                new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        del(position, tag);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    private void del(int position, String tag) {

        if (tag.equals("")) {
            if (basicList.get(position).getEditType() != 2) {
                Info remove = basicList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                basicList.remove(position);
            }
            adapter.notifyDataSetChanged();

            return;
        }
        if (tag.equals(UserInfoUtils.infoTitleKey[0])) {
            if (contactList.get(position).getEditType() != 2) {
                Info remove = contactList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                contactList.remove(position);
            }
        } else if (tag.equals(UserInfoUtils.infoTitleKey[1])) {
            if (emailList.get(position).getEditType() != 2) {
                Info remove = emailList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                emailList.remove(position);
            }

        } else if (tag.equals(UserInfoUtils.infoTitleKey[2])) {
            if (socialList.get(position).getEditType() != 2) {
                Info remove = socialList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                socialList.remove(position);
            }

        } else if (tag.equals(UserInfoUtils.infoTitleKey[3])) {
            if (addressList.get(position).getEditType() != 2) {
                Info remove = addressList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                addressList.remove(position);
            }

        } else if (tag.equals(UserInfoUtils.infoTitleKey[4])) {
            if (eduList.get(position).getEditType() != 2) {
                Info remove = eduList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                eduList.remove(position);
            }

        } else if (tag.equals(UserInfoUtils.infoTitleKey[5])) {
            if (workList.get(position).getEditType() != 2) {
                Info remove = workList.remove(position);
                String id = remove.getId();
                removeFromDetails(Integer.parseInt(id));
            } else {
                workList.remove(position);
            }

        }

        adapter.notifyDataSetChanged();
    }

    class BtnDelClick implements OnClickListener {
        int position;
        String tag = "";
        int type;

        public BtnDelClick(int posi, String strTag, int type) {
            position = posi;
            tag = strTag;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            if (type == 2) {
                del(position, tag);
                return;
            }
            confirmDialog("确认要删除吗？", position, tag);
        }
    }

    class BtnKeyEditClick implements OnClickListener {
        int position;
        String array[];
        int childPosition;
        String key = "";

        public BtnKeyEditClick(int posi, int childPosition, String key) {
            position = posi;
            this.childPosition = childPosition;
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            if (isContainsSpecialKey(key) || "手机号".equals(key)) {
                return;
            }
            List<String> list = null;
            List<String> arrayList = null;
            switch (position) {
                case 0:
                    array = UserInfoUtils.basicUserChineseStr;
                    break;
                case 1:
                    array = UserInfoUtils.contactPhoneChinesetStr;
                    break;
                case 2:
                    array = UserInfoUtils.emailChineseStr;
                    break;
                case 3:
                    array = UserInfoUtils.socialChineseStr;
                    break;
                case 4:
                    array = UserInfoUtils.addressChineseStr;
                    break;
                case 5:
                    array = UserInfoUtils.eduChinesStr;
                    break;
                case 6:
                    array = UserInfoUtils.workChineseStr;
                    break;

                default:
                    break;
            }

            switch (position) {
                case 0:
                    list = Arrays.asList(UserInfoUtils.basicChineseStr);
                    arrayList = new ArrayList<String>(list);
                    for (int i = basicList.size() - 1; i >= 0; i--) {
                        if (arrayList.contains(basicList.get(i).getKey())) {
                            arrayList.remove(basicList.get(i).getKey());
                        }
                    }
                    if (circleMember.getUid() == Global.getIntUid()) {
                        arrayList.remove("昵称");
                        arrayList.remove("备注");
                    }
                    array = (arrayList.toArray(new String[arrayList.size()]));
                    break;
                case 1:
                    list = Arrays.asList(UserInfoUtils.contactPhoneChinesetStr);
                    arrayList = new ArrayList<String>(list);
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).equals("手机号")) {
                            arrayList.remove(i);
                            break;
                        }
                    }
                    array = (arrayList.toArray(new String[arrayList.size()]));
                    break;

                default:
                    break;
            }
            UserInfoEditSelectTypePopwindow pop = new UserInfoEditSelectTypePopwindow(
                    UserInfoEditActivity.this, layParent, array,
                    UserInfoUtils.infoTitleKey[position]);
            pop.setCallBack(new OnSelectKey() {

                @Override
                public void getSelectKey(String str) {
                    String type = UserInfoUtils.convertToEnglish(str);
                    switch (position) {
                        case 0:
                            basicList.get(childPosition).setKey(str);
                            basicList.get(childPosition).setNewKey(str);
                            basicList.get(childPosition).setType(type);

                            break;
                        case 1:
                            contactList.get(childPosition).setKey(str);
                            contactList.get(childPosition).setNewKey(str);
                            contactList.get(childPosition).setType(type);
                            break;
                        case 2:
                            emailList.get(childPosition).setKey(str);
                            emailList.get(childPosition).setNewKey(str);
                            emailList.get(childPosition).setType(type);
                            break;
                        case 3:
                            socialList.get(childPosition).setKey(str);
                            socialList.get(childPosition).setNewKey(str);
                            socialList.get(childPosition).setType(
                                    UserInfoUtils.convertToEnglish(str));
                            break;
                        case 4:
                            addressList.get(childPosition).setKey(str);
                            addressList.get(childPosition).setNewKey(str);
                            addressList.get(childPosition).setType(type);
                            break;
                        case 5:
                            eduList.get(childPosition).setKey(str);
                            eduList.get(childPosition).setNewKey(str);
                            eduList.get(childPosition).setType(type);
                            break;
                        case 6:
                            workList.get(childPosition).setKey(str);
                            workList.get(childPosition).setNewKey(str);
                            workList.get(childPosition).setType(type);
                            break;
                        default:
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }

            });

            pop.show();
        }
    }

    class BtnGroupAddClick implements OnClickListener {
        int position;
        String array[];

        public BtnGroupAddClick(int posi) {
            position = posi;
        }

        @Override
        public void onClick(View v) {
            List<String> list = null;
            List<String> arrayList = null;
            switch (position) {
                case 0:
                    array = UserInfoUtils.basicUserChineseStr;
                    break;
                case 1:
                    array = UserInfoUtils.contactPhoneChinesetStr;
                    break;
                case 2:
                    array = UserInfoUtils.emailChineseStr;
                    break;
                case 3:
                    array = UserInfoUtils.socialChineseStr;
                    break;
                case 4:
                    array = UserInfoUtils.addressChineseStr;
                    break;
                case 5:
                    array = UserInfoUtils.eduChinesStr;
                    break;
                case 6:
                    array = UserInfoUtils.workChineseStr;
                    break;

                default:
                    break;
            }
            switch (position) {
                case 0:
                    list = Arrays.asList(UserInfoUtils.basicChineseStr);
                    arrayList = new ArrayList<String>(list);
                    for (int i = 0; i < basicList.size(); i++) {
                        if (arrayList.contains(basicList.get(i).getKey())) {
                            arrayList.remove(basicList.get(i).getKey());
                        }
                    }
                    if (circleMember.getUid() == Global.getIntUid()) {
                        arrayList.remove("昵称");
                        arrayList.remove("备注");
                    }
                    array = (arrayList.toArray(new String[arrayList.size()]));
                    break;
                case 1:
                    list = Arrays.asList(UserInfoUtils.contactPhoneChinesetStr);
                    arrayList = new ArrayList<String>(list);
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).equals("手机号")) {
                            arrayList.remove(i);
                            break;
                        }
                    }
                    array = (arrayList.toArray(new String[arrayList.size()]));
                    break;

                default:
                    break;
            }

            if (array.length == 0) {
                Utils.showToast("没有类别可以添加了", Toast.LENGTH_SHORT);
                return;
            }
            int size = lists.get(position).size();
            int index = 0;
            if (size != 0) {
                String key = lists.get(position).get(size - 1).getKey();
                index = getKeyIndex(array, key);
                index += 1;
            }
            if (index == array.length) {
                index = 0;
            }
            Info info = new Info();
            info.setKey(array[index]);
            info.setType(UserInfoUtils.convertToEnglish(array[index]));
            info.setEditType(2);
            info.setValue("");
            switch (position) {
                case 0:
                    basicList.add(info);
                    break;
                case 1:
                    contactList.add(info);
                    break;
                case 2:
                    emailList.add(info);
                    break;
                case 3:
                    socialList.add(info);
                    break;
                case 4:
                    addressList.add(info);
                    break;
                case 5:
                    if (circleMember.getState().equals(
                            CircleMemberState.STATUS_INVITING)) {
                        Utils.showToast("该成员还未加入圈子，暂时不能添加教育经历",
                                Toast.LENGTH_LONG);
                        return;
                    }
                    eduList.add(info);
                    break;
                case 6:
                    if (circleMember.getState().equals(
                            CircleMemberState.STATUS_INVITING)) {
                        Utils.showToast("该成员还未加入圈子，暂时不能添加工作经历",
                                Toast.LENGTH_LONG);
                        return;
                    }
                    workList.add(info);
                    listView.setSelectedChild(position, workList.size(), false);
                    break;
                default:
                    break;
            }
            adapter.notifyDataSetChanged();

        }
    }

    private int getKeyIndex(String array[], String key) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(key)) {
                return i;
            }
        }
        return 0;
    }

    private boolean isContainsSpecialKey(String key) {
        for (String str : specialKey) {
            if (key.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建上传的字符串
     */
    // TODO 分类上传数据
    private boolean BuildJson() {
        for (int i = 0; i < basicList.size(); i++) {
            int editType = basicList.get(i).getEditType();
            String keyType = basicList.get(i).getType();
            String value = basicList.get(i).getValue();
            String tid = basicList.get(i).getId();
            String key = basicList.get(i).getKey();
            if (PersonDetailType.D_NAME.name().equals(keyType)) {
                strName = value;
            }
            if (editType == 2) {
                if (keyType.equals(PersonDetailType.D_GENDAR)) {
                    if (value.equals("男")) {
                        value = "1";
                    } else if (value.equals("女")) {
                        value = "2";
                    }
                }
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, "", "");
            } else if (editType == 3) {
                if (value.equals("")) {
                    Utils.showToast(key + "不能为空", Toast.LENGTH_SHORT);
                    return false;
                }
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        break;
                    }
                }
            }
            if (!"".equals(basicList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, "", "");
                removeFromDetails(Integer.parseInt(tid));
            }
        }
        for (int i = 0; i < contactList.size(); i++) {
            int editType = contactList.get(i).getEditType();
            String keyType = contactList.get(i).getType();
            String value = contactList.get(i).getValue();
            String tid = contactList.get(i).getId();
            String key = contactList.get(i).getKey();

            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, "", "");

            } else if (editType == 3) {

                if (value.equals("")) {
                    Utils.showToast(key + "不能为空", Toast.LENGTH_SHORT);
                    return false;
                }
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        break;
                    }
                }
            }
            if (!"".equals(contactList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, "", "");
                removeFromDetails(Integer.parseInt(tid));
            }

        }
        for (int i = 0; i < socialList.size(); i++) {
            int editType = socialList.get(i).getEditType();
            String tid = socialList.get(i).getId();
            String keyType = socialList.get(i).getType();
            String value = socialList.get(i).getValue();
            String key = socialList.get(i).getKey();
            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, "", "");
            } else if (editType == 3) {
                if (value.equals("")) {
                    Utils.showToast(key + "不能为空", Toast.LENGTH_SHORT);

                    return false;
                }
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        break;
                    }
                }
            }
            if (!"".equals(socialList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, "", "");
                removeFromDetails(Integer.parseInt(tid));
            }
        }
        for (int i = 0; i < emailList.size(); i++) {
            int editType = emailList.get(i).getEditType();
            String tid = emailList.get(i).getId();
            String keyType = emailList.get(i).getType();
            String value = emailList.get(i).getValue();
            String key = emailList.get(i).getKey();

            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, "", "");
            } else if (editType == 3) {
                if (value.equals("")) {
                    Utils.showToast(key + "不能为空", Toast.LENGTH_SHORT);
                    return false;
                }
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        break;
                    }
                }
            }
            if (!"".equals(emailList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, "", "");
                removeFromDetails(Integer.parseInt(tid));
            }
        }
        for (int i = 0; i < addressList.size(); i++) {
            int editType = addressList.get(i).getEditType();
            String tid = addressList.get(i).getId();
            String keyType = addressList.get(i).getType();
            String value = addressList.get(i).getValue();
            String key = addressList.get(i).getKey();

            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, "", "");

            } else if (editType == 3) {
                if (value.equals("")) {
                    Utils.showToast(key + "不能为空", Toast.LENGTH_SHORT);
                    return false;
                }
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        break;
                    }
                }
            }
            if (!"".equals(addressList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, "", "");
                removeFromDetails(Integer.parseInt(tid));
            }

        }
        for (int i = 0; i < eduList.size(); i++) {
            int editType = eduList.get(i).getEditType();
            String keyType = eduList.get(i).getType();
            String value = eduList.get(i).getValue();
            String start = eduList.get(i).getStartDate();
            String end = eduList.get(i).getEndDate();
            String tid = eduList.get(i).getId();

            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, start, end);

            } else if (editType == 3) {
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        detail.setStart(start);
                        detail.setEnd(end);
                        break;
                    }
                }
            }
            if (!"".equals(eduList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, start, end);
                removeFromDetails(Integer.parseInt(tid));
            }
        }
        for (int i = 0; i < workList.size(); i++) {
            int editType = workList.get(i).getEditType();
            String keyType = workList.get(i).getType();
            String value = workList.get(i).getValue();
            String start = workList.get(i).getStartDate();
            String end = workList.get(i).getEndDate();
            String tid = workList.get(i).getId();

            if (editType == 2) {
                if (value.equals("")) {
                    continue;
                }
                addDetails(keyType, value, start, end);

            } else if (editType == 3) {
                for (PersonDetail detail : newDetails) {
                    if (detail.getId() == Integer.parseInt(tid)) {
                        detail.setType(PersonDetailType.convertToType(keyType));
                        detail.setValue(value);
                        detail.setStart(start);
                        detail.setEnd(end);
                        break;
                    }
                }
            }
            if (!"".equals(workList.get(i).getNewKey()) && editType != 2) {
                addDetails(keyType, value, start, end);
                removeFromDetails(Integer.parseInt(tid));
            }
        }
        return true;
    }

    private void addDetails(String keyType, String value, String startTime,
            String endTime) {
        PersonDetail detail = new PersonDetail(0, circleMember.getCid(),
                circleMember.getPid(), circleMember.getUid(),
                PersonDetailType.convertToType(keyType), value);
        detail.setStart(startTime);
        detail.setEnd(endTime);
        newDetails.add(detail);
    }

    private void removeFromDetails(int id) {
        for (int n = 0; n < newDetails.size(); n++) {
            PersonDetail detail = newDetails.get(n);
            if (detail.getId() == id) {
                newDetails.remove(n);
                break;
            }
        }
    }

    private void confirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(this, "确定要放弃编辑吗？", "确定", "取消",
                new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        finishAc();

                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                BuildJson();
                JSONArray changeJson = circleMember
                        .getChangedDetails(newCircleMember);
                if (changeJson.length() > 0) {
                    confirmDialog();
                } else {
                    finishAc();
                }
                break;
            case R.id.btnSave:
                if (!BuildJson()) {
                    return;
                }
                dialog = DialogUtil.getWaitDialog(this, "请稍候");
                dialog.show();
                UpLoadCircleMemberIdetailTask loadCircleMemberIdetailTask = new UpLoadCircleMemberIdetailTask(
                        this, selectPicPath);
                loadCircleMemberIdetailTask
                        .setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
                            @Override
                            public void taskFinish(RetError result) {
                                if (dialog != null) dialog.dismiss();
                                if (result != RetError.NONE) {
                                    return;
                                }
                                BroadCast.sendBroadCast(
                                        UserInfoEditActivity.this,
                                        Constants.REFRESH_CIRCLE_USER_LIST);
                                updateSucceed();

                            }

                            @Override
                            public void readDBFinish() {

                            }
                        });
                loadCircleMemberIdetailTask.executeWithCheckNet(circleMember,
                        newCircleMember);
                break;
            case R.id.avatarLay:
                pop = new SelectPicPopwindow(this, v);
                pop.show();
                break;
            default:
                break;
        }
    }

    /**
     * 是否同步对话框
     */
    private void synchronizeConfirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(this, "修改成功\n是否同步到其他圈子?", "是",
                "否", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        Intent intent = new Intent();
                        intent.setClass(UserInfoEditActivity.this,
                                SynchronizeSelfInfoActivity.class);
                        Bundle budle = new Bundle();
                        budle.putSerializable("editData",
                                (Serializable) circleMember.getEditData());
                        intent.putExtras(budle);
                        intent.putExtra("headicon", avatarURL);
                        intent.putExtra("uid", circleMember.getUid());
                        intent.putExtra("cid", circleMember.getCid());
                        startActivity(intent);
                        finish();
                        Utils.leftOutRightIn(UserInfoEditActivity.this);
                    }

                    @Override
                    public void onCancleClick() {
                        finish();
                        Utils.rightOut(UserInfoEditActivity.this);
                    }
                });
        dialog.show();
    }

    // TODO 修改成功
    private void updateSucceed() {
        Intent it = new Intent();
        it.putExtra("flag", true);
        it.putExtra("name", strName);
        Bundle b = new Bundle();
        b.putParcelable("avatar", avatarBitmap);
        it.putExtras(b);
        setResult(2, it);
        if (circleMember.getUid() == Global.getIntUid()
                && circleMember.getEditData().size() > 0) {
            synchronizeConfirmDialog();
        } else {
            Utils.showToast("修改成功", Toast.LENGTH_SHORT);
            finish();
            Utils.rightOut(this);
        }
    }

    private void finishAc() {
        Intent it = new Intent();
        it.putExtra("flag", false);
        setResult(2, it);
        finish();
        Utils.rightOut(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BuildJson();
            JSONArray changeJson = circleMember
                    .getChangedDetails(newCircleMember);
            if (changeJson.length() > 0 || !"".equals(selectPicPath)) {
                confirmDialog();
            } else {
                finishAc();
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
                && resultCode == RESULT_OK && data != null) {
            selectPicPath = BitmapUtils.startPhotoZoom(this, data.getData());

        }// 拍摄图片
        else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (resultCode != RESULT_OK) {
                return;
            }
            String fileName = pop.getTakePhotoPath();
            selectPicPath = fileName;
            selectPicPath = BitmapUtils.startPhotoZoom(this,
                    Uri.fromFile(new File(fileName)));
        } else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
                && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                bitmap = photo;
            }
            // upLoadAvatar(bitmap);
            if (bitmap != null) {
                avatarBitmap = bitmap;
                avatar.setImageBitmap(bitmap);
                new BoxBlurFilterThread(bitmap).start();
                new BoxBlurFilterThread(bitmap).start();

            }
        }

    }
}
