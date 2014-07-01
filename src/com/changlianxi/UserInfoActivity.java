package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.modle.Info;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CircleMemberIdetailTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SaveContactsToPhone;
import com.changlianxi.util.SortPersonType;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

@TargetApi(16)
public class UserInfoActivity extends BaseActivity implements OnClickListener,
        OnScrollListener, ImageLoadingListener {
    private ExpandableListView list;
    private String iconPath = "";
    private int pid;// 用户pid
    private int uid;// 用户uid
    private int cid;// 圈子id
    private Adapter adapter;
    private List<Info> showBasicList = new ArrayList<Info>();// 存放基本信息数据
    private List<Info> showContactList = new ArrayList<Info>();// 存放联系方式数据
    private List<Info> showSocialList = new ArrayList<Info>();// 存放社交账号数据
    private List<Info> showAddressList = new ArrayList<Info>();// 存放地址数据
    private List<Info> showEduList = new ArrayList<Info>();// 存放教育经历
    private List<Info> showWorkList = new ArrayList<Info>();// 存放工作经历
    private List<Info> showEmailList = new ArrayList<Info>();// 存放电子邮箱
    private List<List<Info>> lists = new ArrayList<List<Info>>();
    private List<GroupModle> group = new ArrayList<GroupModle>();
    private Dialog dialog;
    private CircleMemberIdetailTask task;
    private CircleMember circleMember;
    private ImageView back;
    private TextView name;
    private CircularImage avatar;
    private Button btnEdit;
    private RelativeLayout layTop;
    private TextView txtnews;
    private final int TYPE_1 = 1;
    private final int TYPE_2 = 2;
    private final int TYPE_3 = 3;
    private final int TYPE_4 = 4;
    private String registerEmail = "";
    private String username = ""; // 姓名
    private String cellPhone = "";// 手机
    private String compony = ""; // 公司
    private String title = ""; // 职位
    private String homePhone = ""; // 住宅电话
    private String workPhone = ""; // 工作电话
    private String commonlyUsed_email = "";// 常用邮箱
    private String home_email = ""; // 个人邮箱
    private String work_email = ""; // 工作邮箱
    private String work_address = ""; // 工作地址
    private String home_address = ""; // 家庭地址
    private TextView history; // 查看历史记录
    private TextView save;
    private TextView kickOut;
    private TextView sendMessage;
    private TextView line1;
    private TextView line2;
    private TextView line3;
    private TextView line4;
    private TextView authState;
    private ImageView iv_sex;
    private LinearLayout btnOk;
    private String sex = "";
    private Info inSex = null;
    private ViewStub layPrompt;
    private List<CircleMember> listMemers;
    private View footView;
    private int itemposiotion = -1;
    private RelativeLayout selMes;
    private int topsi = 0;
    private int botsi = 0;
    private View emptheadview;
    private ImageView iv_headbg;
    private Bitmap editAvatarBitmap;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    notifyAdapter();
                    break;
                case 1:
                    setBackGroubdOfDrable((Drawable) msg.obj);
                    break;
                case 4:
                    if (sex.equals("1") || "男".equals(sex)) {
                        iv_sex.setVisibility(View.VISIBLE);
                        iv_sex.setImageResource(R.drawable.icon_b);
                    } else if (sex.equals("2") || "女".equals(sex)) {
                        iv_sex.setImageResource(R.drawable.icon_g);
                        iv_sex.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    initView();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        circleMember = (CircleMember) getIntent().getExtras().getSerializable(
                "member");
        if (circleMember.getState().equals(CircleMemberState.STATUS_INVITING)) {
            listMemers = (List<CircleMember>) getIntent().getExtras()
                    .getSerializable("listMemers");
        }
        iconPath = circleMember.getAvatar();
        username = circleMember.getName();
        pid = circleMember.getPid();
        cid = circleMember.getCid();
        uid = circleMember.getUid();
        cellPhone = circleMember.getCellphone();
        registerEmail = circleMember.getAccount_email();
        mHandler.sendEmptyMessageDelayed(5, 200);
    }

    private void initView() {
        selMes = (RelativeLayout) findViewById(R.id.selMes);
        iv_headbg = (ImageView) findViewById(R.id.iv_headbg);
        list = (ExpandableListView) findViewById(R.id.expandableListView1);
        layTop = (RelativeLayout) findViewById(R.id.top);
        back = (ImageView) findViewById(R.id.back);
        name = (TextView) findViewById(R.id.name);
        avatar = (CircularImage) findViewById(R.id.avatar);
        btnEdit = (Button) findViewById(R.id.btnedit);
        authState = (TextView) findViewById(R.id.authState);
        iv_sex = (ImageView) findViewById(R.id.img_sex);
        initFooterView();
        inithead();
        setFooterVisible();
        setOnClickListener();
    }

    private void inithead() {
        emptheadview = View.inflate(this, R.layout.user_info_headview, null);
        list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        list.setOnScrollListener(this);
        list.addHeaderView(emptheadview);
    }

    private void setFooterVisible() {
        authState(circleMember.getState());
        if (circleMember.getState() == CircleMemberState.STATUS_INVITING) {
            // initLayPrompt();
        }

        if (circleMember.getState().equals(CircleMemberState.STATUS_INVITING)
                && uid == Global.getIntUid()) {
            btnEdit.setVisibility(View.GONE);
            footView.setVisibility(View.GONE);
        }
        if (circleMember.isAuth(DBUtils.getDBsa(1))) {
            btnEdit.setVisibility(View.VISIBLE);
        }

        if (Global.getIntUid() == uid) {
            save.setVisibility(View.GONE);
            sendMessage.setVisibility(View.GONE);
            kickOut.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line4.setVisibility(View.GONE);
        } else {
            history.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            if (circleMember.getState() != CircleMemberState.STATUS_VERIFIED) {
                sendMessage.setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
            }
        }
    }

    private void initFooterView() {
        LayoutInflater infla = LayoutInflater.from(this);
        footView = infla.inflate(R.layout.activity_user_info_add, null);
        list.addFooterView(footView, null, false);
        txtnews = (TextView) findViewById(R.id.txtnews);
        history = (TextView) footView.findViewById(R.id.btnHistory);
        sendMessage = (TextView) footView.findViewById(R.id.btnSendMessage);
        save = (TextView) footView.findViewById(R.id.btnSave);
        kickOut = (TextView) findViewById(R.id.btnKickOut);
        line1 = (TextView) footView.findViewById(R.id.lineSave);
        line2 = (TextView) footView.findViewById(R.id.lineSendMessage);
        line3 = (TextView) footView.findViewById(R.id.lineHistory);
        line4 = (TextView) footView.findViewById(R.id.lineKickOut);

    }

    private void initLayPrompt() {
        layPrompt = (ViewStub) findViewById(R.id.lay_prompt);
        layPrompt.inflate();
        btnOk = (LinearLayout) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

    }

    private void setOnClickListener() {
        back.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        avatar.setOnClickListener(this);
        history.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        kickOut.setOnClickListener(this);
        save.setOnClickListener(this);
        list.setGroupIndicator(null);
        list.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                return true;
            }
        });
        setValue();
    }

    private void setValue() {
        if (!"".equals(registerEmail) && "".equals(cellPhone)) {
            txtnews.setText(registerEmail);
        } else {
            txtnews.setText(cellPhone);
        }
        name.setText(username);
        filldata();
        setAvatar();
    }

    private void authState(CircleMemberState state) {
        authState.setVisibility(View.VISIBLE);
        if (state.equals(CircleMemberState.STATUS_VERIFIED)
                || state.equals(CircleMemberState.STATUS_KICKOFFING)) {
            authState.setText("已认证");
            authState.setBackgroundResource(R.drawable.auth);
        } else if (state.equals(CircleMemberState.STATUS_ENTER_AND_VERIFYING)) {
            authState.setText("认证中");
            authState.setBackgroundResource(R.drawable.btn_aaaaaa);
        } else if (state.equals(CircleMemberState.STATUS_INVITING)) {
            authState.setText("未加入");
            authState.setBackgroundResource(R.drawable.btn_aaaaaa);
        } else {
            authState.setVisibility(View.GONE);
        }
    }

    private void setAvatar() {
        UniversalImageLoadTool.disPlayListener(iconPath, avatar,
                R.drawable.head_bg, this);
    }

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

    private void notifyAdapter() {
        GroupModle gmodle = null;
        if (showBasicList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[0]);
            group.add(gmodle);
            lists.add(showBasicList);
        }
        if (showContactList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[1]);
            group.add(gmodle);
            lists.add(showContactList);

        }
        if (showEmailList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[2]);
            group.add(gmodle);
            lists.add(showEmailList);

        }
        if (showSocialList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[3]);
            group.add(gmodle);
            lists.add(showSocialList);

        }
        if (showAddressList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[4]);
            group.add(gmodle);
            lists.add(showAddressList);

        }
        if (showEduList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[5]);
            group.add(gmodle);
            lists.add(showEduList);

        }
        if (showWorkList.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[6]);
            group.add(gmodle);
            lists.add(showWorkList);

        }
        sort();
        if (adapter == null) {
            adapter = new Adapter();
            list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            list.expandGroup(i);

        }
    }

    private class GroupModle {
        String title = "";

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private void filldata() {
        task = new CircleMemberIdetailTask();
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                // 分类
                clearData();
                List<PersonDetail> details = circleMember.getDetails();
                for (PersonDetail detail : details) {
                    valuesClassification(detail.getId(), detail.getType(),
                            detail.getValue(), detail.getStart(),
                            detail.getEnd());
                }
                notifyAdapter();
                if (inSex == null) {
                    iv_sex.setVisibility(View.GONE);

                }
            }

            @Override
            public void readDBFinish() {

            }

        });
        task.executeWithCheckNet(circleMember);
    }

    private void clearData() {
        lists.clear();
        group.clear();
        showBasicList.clear();
        showContactList.clear();
        showSocialList.clear();
        showAddressList.clear();
        showEduList.clear();
        showWorkList.clear();
        showEmailList.clear();
    }

    private void sort() {
        Collections.sort(showBasicList, SortPersonType.getComparator());
        Collections.sort(showContactList, SortPersonType.getComparator());
        Collections.sort(showSocialList, SortPersonType.getComparator());
        Collections.sort(showAddressList, SortPersonType.getComparator());
        Collections.sort(showEduList, SortPersonType.getComparator());
        Collections.sort(showWorkList, SortPersonType.getComparator());
        Collections.sort(showEmailList, SortPersonType.getComparator());

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

    /**
     * 数据分类
     * 
     * @param id
     * @param key
     * @param value
     */
    public void valuesClassification(int id, PersonDetailType key,
            String value, String start, String end) {
        if (value.equals("null") || "".equals(value)) {
            return;
        }
        Info info = new Info();
        info.setValue(value);
        info.setId(id + "");
        info.setType(key.name());
        int sortKey = SortPersonType.typeSort.get(key) == null ? 0
                : SortPersonType.typeSort.get(key);
        info.setSortKey(sortKey);
        String typekey = "";
        if (key.equals(PersonDetailType.D_EMPLOYER)) {
            compony = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[0]);
            showBasicList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_JOBTITLE)) {
            title = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[0]);
            showBasicList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_GENDAR)) {
            sex = value;
            mHandler.sendEmptyMessage(4);
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[3]);
            inSex = info;
            return;
        }
        if (key.equals(PersonDetailType.D_HOME_PHONE)) {
            homePhone = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[1]);
            showContactList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_WORK_PHONE)) {
            workPhone = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[1]);
            showContactList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_PERSONAL_EMAIL)) {
            home_email = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[2]);
            info.setEndDate(end);
            showEmailList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_EMAIL)) {
            work_email = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[2]);
            info.setEndDate(end);
            showEmailList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_WORK_EMAIL)) {
            commonlyUsed_email = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[2]);
            info.setEndDate(end);
            showEmailList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_HOME_ADDRESS)) {
            home_address = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[4]);
            showAddressList.add(info);
            return;
        }
        if (key.equals(PersonDetailType.D_WORK_ADDRESS)) {
            work_address = value;
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[4]);
            showAddressList.add(info);
            return;
        }
        if (Arrays.toString(UserInfoUtils.workStr).contains(key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[6]);
            info.setEndDate(end);
            showWorkList.add(info);
        } else if (Arrays.toString(UserInfoUtils.socialStr)
                .contains(key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[3]);
            showSocialList.add(info);
        } else if (Arrays.toString(UserInfoUtils.contactPhone).contains(
                key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[1]);
            showContactList.add(info);

        } else if (Arrays.toString(UserInfoUtils.emailStr).contains(key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[2]);
            info.setEndDate(end);
            showEmailList.add(info);
        } else if (Arrays.toString(UserInfoUtils.addressStr).contains(
                key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[4]);
            showAddressList.add(info);
        } else if (Arrays.toString(UserInfoUtils.eduStr).contains(key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setEndDate(end);
            info.setTitleKey(UserInfoUtils.infoTitleKey[5]);
            showEduList.add(info);
        } else if (Arrays.toString(UserInfoUtils.workStr).contains(key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setStartDate(start);
            info.setTitleKey(UserInfoUtils.infoTitleKey[6]);
            info.setEndDate(end);
            showWorkList.add(info);
        } else if (Arrays.toString(UserInfoUtils.basicUserStr).contains(
                key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[0]);
            showBasicList.add(info);
        }

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
        public View getChildView(final int groupPosition,
                final int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            String value = lists.get(groupPosition).get(childPosition)
                    .getValue();
            String key = lists.get(groupPosition).get(childPosition).getKey();
            String type = lists.get(groupPosition).get(childPosition).getType();
            ChildViewHolderBasic holderBasic = null;
            ContactViewHolderValues contacHolderValues = null;
            ChildViewHolder cHolder = null;
            EduHolderValues ediHolder = null;
            int viewType = getItemViewType(groupPosition);
            switch (viewType) {
                case 1:
                    cHolder = new ChildViewHolder();
                    convertView = LayoutInflater.from(UserInfoActivity.this)
                            .inflate(R.layout.user_info_child_item, null);
                    cHolder.bg = (LinearLayout) convertView
                            .findViewById(R.id.bg);
                    cHolder.valueLayout = (LinearLayout) convertView
                            .findViewById(R.id.valueLayout);
                    cHolder.key = (TextView) convertView.findViewById(R.id.key);
                    cHolder.value = (TextView) convertView
                            .findViewById(R.id.value);
                    convertView.setTag(cHolder);
                    break;
                case 2:
                    convertView = LayoutInflater.from(UserInfoActivity.this)
                            .inflate(R.layout.user_info_child_item_call_sms,
                                    null);
                    contacHolderValues = new ContactViewHolderValues();
                    contacHolderValues.key = (TextView) convertView
                            .findViewById(R.id.key);
                    contacHolderValues.value = (TextView) convertView
                            .findViewById(R.id.value);
                    contacHolderValues.iconSms = (ImageView) convertView
                            .findViewById(R.id.icon_sms);
                    contacHolderValues.iconCall = (ImageView) convertView
                            .findViewById(R.id.icon_call);
                    contacHolderValues.bg = (LinearLayout) convertView
                            .findViewById(R.id.bg);
                    contacHolderValues.valueLayout = (LinearLayout) convertView
                            .findViewById(R.id.valueLayout);
                    convertView.setTag(contacHolderValues);
                    break;
                case 3:
                    convertView = LayoutInflater.from(UserInfoActivity.this)
                            .inflate(R.layout.user_info_child_item_edu_work,
                                    null);
                    ediHolder = new EduHolderValues();
                    ediHolder.key = (TextView) convertView
                            .findViewById(R.id.key);
                    ediHolder.value = (TextView) convertView
                            .findViewById(R.id.value);
                    ediHolder.endTime = (TextView) convertView
                            .findViewById(R.id.endTime);
                    ediHolder.startTime = (TextView) convertView
                            .findViewById(R.id.startTime);
                    ediHolder.bg = (LinearLayout) convertView
                            .findViewById(R.id.bg);
                    ediHolder.valueLayout = (LinearLayout) convertView
                            .findViewById(R.id.valueLayout);
                    convertView.setTag(ediHolder);
                    break;
                case 4:
                    convertView = LayoutInflater.from(UserInfoActivity.this)
                            .inflate(R.layout.user_info_child_item_basic, null);
                    holderBasic = new ChildViewHolderBasic();
                    holderBasic.bg = (RelativeLayout) convertView
                            .findViewById(R.id.bg);
                    holderBasic.key = (TextView) convertView
                            .findViewById(R.id.key);
                    holderBasic.value = (TextView) convertView
                            .findViewById(R.id.value);
                    holderBasic.topLine = (ImageView) convertView
                            .findViewById(R.id.line_top);
                    holderBasic.bottomLine = (ImageView) convertView
                            .findViewById(R.id.line_bottom);
                    holderBasic.img = (ImageView) convertView
                            .findViewById(R.id.image);
                    convertView.setTag(holderBasic);
                    break;
                default:
                    break;
            }

            switch (viewType) {
                case 1:
                    cHolder.key.setText(key);
                    cHolder.value.setText(value);
                    if (groupPosition % 2 == 0) {
                        cHolder.bg.setBackgroundColor(Color.WHITE);
                    } else {
                        cHolder.bg.setBackgroundColor(getResources().getColor(
                                R.color.f6));
                    }
                    break;
                case 2:
                    contacHolderValues.iconCall
                            .setOnClickListener(new BtnClick(value));
                    contacHolderValues.iconSms.setOnClickListener(new BtnClick(
                            value));
                    contacHolderValues.key.setOnClickListener(new BtnClick(
                            value));
                    contacHolderValues.value.setOnClickListener(new BtnClick(
                            value));
                    contacHolderValues.key.setText(key);
                    contacHolderValues.value.setText(value);
                    if (Utils.isPhoneNum(value)) {
                        contacHolderValues.iconSms.setVisibility(View.VISIBLE);
                    } else {
                        contacHolderValues.iconSms.setVisibility(View.GONE);
                    }
                    if (groupPosition % 2 == 0) {
                        contacHolderValues.bg.setBackgroundColor(Color.WHITE);
                    } else {
                        contacHolderValues.bg.setBackgroundColor(getResources()
                                .getColor(R.color.f6));
                    }
                    break;
                case 3:
                    ediHolder.key.setText(key);
                    ediHolder.value.setText(value);
                    String end = DateUtils.interceptDateStr(
                            lists.get(groupPosition).get(childPosition)
                                    .getEndDate(), "yyyy-MM-dd");
                    String start = DateUtils.interceptDateStr(
                            lists.get(groupPosition).get(childPosition)
                                    .getStartDate(), "yyyy-MM-dd");
                    ediHolder.endTime.setText(end.equals("") ? "无" : end);
                    ediHolder.startTime.setText(start.equals("") ? "无" : start);
                    if (groupPosition % 2 == 0) {
                        ediHolder.bg.setBackgroundColor(Color.WHITE);
                    } else {
                        ediHolder.bg.setBackgroundColor(getResources()
                                .getColor(R.color.f6));
                    }
                    break;
                case 4:
                    holderBasic.key.setText(key);
                    holderBasic.value.setText(value);
                    if (groupPosition % 2 == 0) {
                        holderBasic.bg.setBackgroundColor(Color.WHITE);
                    } else {
                        holderBasic.bg.setBackgroundColor(getResources()
                                .getColor(R.color.f6));
                    }
                    if (childPosition == 0) {
                        holderBasic.img.setImageResource(R.drawable.dian);
                        holderBasic.topLine.setVisibility(View.GONE);
                        holderBasic.bg.setPadding(
                                holderBasic.bg.getPaddingLeft(),
                                holderBasic.bg.getPaddingTop() + 20,
                                holderBasic.bg.getPaddingRight(),
                                holderBasic.bg.getPaddingBottom());
                    }
                    if (childPosition == showBasicList.size() - 1) {
                        holderBasic.bottomLine.setVisibility(View.GONE);
                        holderBasic.bg.setPadding(
                                holderBasic.bg.getPaddingLeft(),
                                holderBasic.bg.getPaddingTop(),
                                holderBasic.bg.getPaddingRight(),
                                holderBasic.bg.getPaddingBottom() + 20);
                    }
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
                convertView = LayoutInflater.from(UserInfoActivity.this)
                        .inflate(R.layout.listview_item, null);
                gHolder.bg = (LinearLayout) convertView.findViewById(R.id.bg);
                gHolder.titleKey = (TextView) convertView
                        .findViewById(R.id.titleKey);
                convertView.setTag(gHolder);
            } else {
                gHolder = (GroupViewHoler) convertView.getTag();
            }
            if (groupPosition % 2 == 0) {
                gHolder.bg.setBackgroundColor(Color.WHITE);
            } else {
                gHolder.bg.setBackgroundColor(getResources().getColor(
                        R.color.f6));
            }
            String titleKey = group.get(groupPosition).getTitle();
            gHolder.titleKey.setText(titleKey);

            if (UserInfoUtils.infoTitleKey[0].equals(titleKey)) {
                gHolder.titleKey.setVisibility(View.GONE);
            } else {
                gHolder.titleKey.setVisibility(View.VISIBLE);

            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public int getItemViewType(int groupPosition) {
            String viewType = group.get(groupPosition).getTitle();
            if ("联系电话".equals(viewType)) {
                return TYPE_2;
            } else if ("教育经历".equals(viewType)) {
                return TYPE_3;
            } else if ("工作经历".equals(viewType)) {
                return TYPE_3;
            } else if ("基本信息".equals(viewType)) {
                return TYPE_4;
            } else {
                return TYPE_1;
            }
        }

    }

    class ChildViewHolder {
        TextView key;
        TextView value;
        LinearLayout bg;
        LinearLayout valueLayout;

    }

    class ChildViewHolderBasic {
        TextView key;
        TextView value;
        ImageView img;
        RelativeLayout bg;
        ImageView topLine;
        ImageView bottomLine;

    }

    class ContactViewHolderValues {
        TextView key;
        TextView value;
        ImageView iconSms;
        ImageView iconCall;
        LinearLayout bg;
        // TextView titleKey;
        LinearLayout valueLayout;
    }

    class EduHolderValues {
        TextView key;
        TextView value;
        TextView startTime;
        TextView endTime;
        LinearLayout bg;
        // TextView titleKey;
        LinearLayout valueLayout;
    }

    class GroupViewHoler {
        LinearLayout bg;
        TextView titleKey;

    }

    class BtnClick implements OnClickListener {
        String str;

        public BtnClick(String str) {
            this.str = str;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icon_sms:
                    sendMessage(str);
                    break;
                case R.id.icon_call:
                    callPhone(str);
                    break;
                case R.id.key:
                    callPhone(str);
                    break;
                case R.id.value:
                    callPhone(str);
                    break;
                default:
                    break;
            }

        }

    }

    class KicOutTask extends AsyncTask<CircleMember, Integer, Integer> {
        private Dialog dialog;
        private String userName = "";

        public KicOutTask(String userName) {
            this.userName = userName;
        }

        @Override
        protected Integer doInBackground(CircleMember... params) {
            CircleMember cm = params[0];
            int need = cm.kickout2();
            return need;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result < 0) {
                if (result == -3) {
                    Utils.showToast("已经踢过一次了,不能再踢了！", Toast.LENGTH_SHORT);

                } else {
                    Utils.showToast("啊哦,很抱歉没有成功,请确认是否是网络的缘故！",
                            Toast.LENGTH_SHORT);
                }
                return;
            }
            BroadCast.sendBroadCast(UserInfoActivity.this,
                    Constants.REFRESH_CIRCLE_USER_LIST);
            String str = "";
            if (result == 0) {
                str = userName + " 已被成功踢出";

            } else {
                str = "您已经向  " + userName + "  发出了踢出申请,还需要本圈子中至少" + result
                        + "个成员同意,踢出才能成功。";
            }
            promptDialog(str);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getWaitDialog(UserInfoActivity.this, "请稍候");
            dialog.show();
        }
    }

    private void promptDialog(String str) {
        Dialog dialog = DialogUtil.promptDialog(this, str, "确定",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        finish();
                        Utils.rightOut(UserInfoActivity.this);
                    }

                    @Override
                    public void onCancleClick() {
                    }
                });
        dialog.show();
    }

    private void confirmDialog(String str, final String userName) {
        Dialog dialog = DialogUtil.confirmDialog(this, str, "确定", "取消",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        kickOut(userName);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    /**
     * 踢出成员
     */
    private void kickOut(final String userName) {
        if (!Utils.isNetworkAvailable()) {
            Utils.showToast("杯具,网络不通,快检查下吧！", Toast.LENGTH_SHORT);
            return;
        }
        CircleMember cm = new CircleMember(cid, pid);
        cm.read(DBUtils.getDBsa(1));
        new KicOutTask(username).execute(cm);
    }

    private void edit() {
        Intent it = new Intent();
        it.putExtra("avatar", iconPath);
        Bundle bundle = new Bundle();
        addInSex();
        bundle.putSerializable("basicList", (Serializable) showBasicList);
        bundle.putSerializable("contactList", (Serializable) showContactList);
        bundle.putSerializable("socialList", (Serializable) showSocialList);
        bundle.putSerializable("addressList", (Serializable) showAddressList);
        bundle.putSerializable("eduList", (Serializable) showEduList);
        bundle.putSerializable("workList", (Serializable) showWorkList);
        bundle.putSerializable("emailList", (Serializable) showEmailList);
        bundle.putParcelable("avatarBitmap", editAvatarBitmap);
        it.putExtras(bundle);
        it.putExtra("circleMumber", circleMember);
        it.setClass(this, UserInfoEditActivity.class);
        startActivityForResult(it, 2);
        Utils.leftOutRightIn(this);
    }

    private void addInSex() {
        if (inSex != null) {
            if ("1".equals(inSex.getValue())) {
                inSex.setValue("男");
            } else if ("2".equals(inSex.getValue())) {
                inSex.setValue("女");
            }
            showBasicList.add(inSex);
            inSex = null;
        }
    }

    /**
     * 发短信
     * 
     * @param num
     */
    private void sendMessage(String num) {
        Uri uri = Uri.parse("smsto:" + num);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "");
        startActivity(it);
    }

    /**
     * 拨打电话
     * 
     * @param num
     */
    private void callPhone(String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + num));
        startActivity(intent);

    }

    /**
     * 保存到已有联系人
     * 
     */
    private void saveToExistingContacts() {
        SaveContactsToPhone.saveToExistingContacts(this, home_address,
                work_address, username, cellPhone, workPhone, homePhone,
                commonlyUsed_email, home_email, work_email, compony, title);
    }

    /**
     * 新建联系人
     * 
     */
    private void newContacts() {

        SaveContactsToPhone.newContacts(this, home_address, work_address,
                username, cellPhone, workPhone, homePhone, commonlyUsed_email,
                home_email, work_email, compony, title);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.btnedit:
                edit();
                break;
            case R.id.btnHistory:
                intent = new Intent();
                intent.putExtra("uid", uid);
                intent.putExtra("cid", cid);
                intent.putExtra("headicon", iconPath);
                intent.setClass(this, ChangeHistoryActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btnKickOut:
                Circle c = new Circle(cid);
                c.read(DBUtils.getDBsa(1));
                int creator = c.getCreator();
                if (creator == Global.getIntUid()) {
                    confirmDialog("确认要把  " + username + "  踢出圈子吗？", username);
                    return;
                }
                confirmDialog("确认要把  " + username + "  踢出圈子吗？", username);
                break;
            case R.id.btnSave:
                // circleMember.getContactsValues(DBUtils.getDBsa(1));
                Dialog dialog = DialogUtil.confirmDialog(UserInfoActivity.this,
                        "请选择保存方式", "新建联系人", "保存至已有联系人", new ConfirmDialog() {
                            @Override
                            public void onOKClick() {
                                newContacts();
                            }

                            @Override
                            public void onCancleClick() {
                                saveToExistingContacts();
                            }
                        });
                dialog.show();
                break;
            case R.id.btnSendMessage:
                CircleMember member = new CircleMember(cid, 0, uid);
                if (!member.isAuth(DBUtils.getDBsa(1))) {
                    Utils.showToast("该用户不是认证成员,不能发送私信！", Toast.LENGTH_SHORT);
                    return;
                }
                intent = new Intent();
                intent.putExtra("ruid", uid);
                intent.putExtra("cid", cid);
                intent.putExtra("name", username);
                intent.putExtra("type", "write");
                intent.setClass(this, MessageActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case R.id.btnOk:
                Circle circle = new Circle(cid);
                circle.getCircleName(DBUtils.getDBsa(1));
                String circleName = circle.getName();
                CircleMember self = new CircleMember(cid, 0, Global.getIntUid());
                self.getNameAndAvatar(DBUtils.getDBsa(1));
                String content = Utils.getWarnContent(listMemers, username,
                        circleName, circleMember.getInviteCode(),
                        self.getName());
                Utils.sendSMS(this, content, circleMember.getCellphone());
                Utils.rightOut(this);
                break;
            case R.id.avatar:
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(StringUtils.revertAliyunOSSImageUrl(iconPath));
                intent = new Intent(this, AvatarImagePagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                        (Serializable) imgUrl);
                intent.putExtras(bundle);
                intent.putExtra("defaultImg", R.drawable.head_bg);
                intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 1);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            boolean flag = data.getBooleanExtra("flag", false);
            if (!flag) {
                return;
            }
            String nameStr = data.getStringExtra("name");
            if (!"".equals(nameStr)) {
                name.setText(nameStr);
            }
            editAvatarBitmap = data.getExtras().getParcelable("avatar");
            if (editAvatarBitmap != null) {
                avatar.setImageBitmap(editAvatarBitmap);
                new BoxBlurFilterThread(editAvatarBitmap).start();
            }
            filldata();
            BroadCast.sendBroadCast(this, Constants.REFRESH_CIRCLE_USER_LIST);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        int[] location = new int[2];
        emptheadview.getLocationInWindow(location);
        int top = Utils.px2dip(this, location[1]);
        if (itemposiotion <= 0) {
            itemposiotion = top;
        }
        final int alp = ((itemposiotion - top) * 255 / 70);
        if (0 <= alp && alp <= 255) {

            avatar.setVisibility(View.VISIBLE);
            avatar.setAlpha(255 - alp);
            iv_headbg.setVisibility(View.VISIBLE);
            iv_headbg.setAlpha(255 - alp);
            if (topsi == 0 || botsi == 0) {
                topsi = selMes.getTop();
                botsi = selMes.getBottom();
            }
            selMes.layout(selMes.getLeft(),
                    topsi - Utils.dip2px(this, itemposiotion - top),
                    selMes.getRight(),
                    botsi - Utils.dip2px(this, itemposiotion - top));
        } else {
            avatar.setVisibility(View.INVISIBLE);
            iv_headbg.setVisibility(View.INVISIBLE);
            selMes.layout(selMes.getLeft(), topsi - Utils.dip2px(this, 70),
                    selMes.getRight(), botsi - Utils.dip2px(this, 70));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // BroadCast.sendBroadCast(this, Constants.REFRESH_CIRCLE_USER_LIST);
    }

    @Override
    public void onLoadingCancelled(String arg0, View arg1) {

    }

    @Override
    public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
        if (mBitmap != null) {
            new BoxBlurFilterThread(mBitmap).start();
        } else {
            avatar.setImageResource(R.drawable.head_bg);
        }
    }

    @Override
    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

    }

    @Override
    public void onLoadingStarted(String arg0, View arg1) {

    }
}
