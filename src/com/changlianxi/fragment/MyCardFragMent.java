package com.changlianxi.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.changlianxi.ChangeHistoryActivity;
import com.changlianxi.MyCardEditActivity;
import com.changlianxi.R;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.modle.Info;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.ReadMyCardTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.SortPersonType;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;

@SuppressLint("NewApi")
public class MyCardFragMent extends Fragment implements OnClickListener,
        OnScrollListener {
    private ExpandableListView listView;
    private ImageView back;
    private RelativeLayout layTop;
    private CircularImage avatar;
    private TextView btnHistory;
    private Button btnEdit;
    private List<Info> showBasicList = new ArrayList<Info>();// 存放基本信息数据
    private List<Info> showContactList = new ArrayList<Info>();// 存放联系方式数据
    private List<Info> showEmailList = new ArrayList<Info>();// 存放联系方式数据
    private List<Info> showSocialList = new ArrayList<Info>();// 存放社交账号数据
    private List<Info> showAddressList = new ArrayList<Info>();// 存放地址数据
    private List<Info> showEduList = new ArrayList<Info>();// 存放教育经历
    private List<Info> showWorkList = new ArrayList<Info>();// 存放工作经历
    private List<List<Info>> lists = new ArrayList<List<Info>>();
    private List<GroupModle> group = new ArrayList<GroupModle>();
    private MyCard card;
    private ReadMyCardTask task;
    private final int TYPE_1 = 1;
    private final int TYPE_2 = 2;
    private final int TYPE_4 = 4;
    private Adapter adapter;
    private View footView;
    private boolean isRefush = false;

    private int itemposiotion = -1;
    private TextView txtShow;
    private int topsi = 0;
    private int botsi = 0;
    private View emptheadview;
    private ImageView iv_headbg;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    setBackGroubdOfDrable((Drawable) msg.obj);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_card_show, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        txtShow = (TextView) getView().findViewById(R.id.txtShow);
        iv_headbg = (ImageView) getView().findViewById(R.id.iv_headbg);
        back = (ImageView) getView().findViewById(R.id.back);
        layTop = (RelativeLayout) getView().findViewById(R.id.top);
        avatar = (CircularImage) getView().findViewById(R.id.avatar);
        btnEdit = (Button) getView().findViewById(R.id.btnedit);
        listView = (ExpandableListView) getView().findViewById(
                R.id.expandableListView1);
        LayoutInflater infla = LayoutInflater.from(getActivity());
        footView = infla.inflate(R.layout.mycard_bottom, null);
        listView.addFooterView(footView, null, true);
        footView.setVisibility(View.GONE);
        btnHistory = (TextView) footView.findViewById(R.id.btnHistory);
        listView.setGroupIndicator(null);
        inithead();
        setListener();
    }

    @SuppressLint("NewApi")
    private void inithead() {
        emptheadview = View.inflate(getActivity(), R.layout.user_info_headview,
                null);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setOnScrollListener(this);
        listView.addHeaderView(emptheadview);
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        avatar.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        listView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                return true;
            }
        });
        setValue();
    }

    private void setValue() {
        registerBoradcastReceiver();
        adapter = new Adapter();
        listView.setAdapter(adapter);
        card = new MyCard(0, Global.getIntUid());
        getMyCard();
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

    private void setAvatar() {
        if ("".equals(card.getAvatar())) {
            avatar.setImageResource(R.drawable.head_bg);
            return;
        }
        Bitmap mBitmap = FinalBitmapLoadTool.getFb().getBitmapFromDiskCache(
                card.getAvatar(), new BitmapDisplayConfig());
        if (mBitmap != null) {
            avatar.setImageBitmap(mBitmap);
            new BoxBlurFilterThread(mBitmap).start();
        } else {
            avatar.setImageResource(R.drawable.head_bg);
        }
    }

    private void loadingAvatar(String avatarURL) {
        FinalBitmapLoadTool.getFb().configDisplayer(new Displayer() {

            @Override
            public void loadFailDisplay(View arg0, Bitmap arg1) {

            }

            @Override
            public void loadCompletedisplay(View arg0, Bitmap mBitmap,
                    BitmapDisplayConfig arg2) {
                avatar.setImageBitmap(mBitmap);
                new BoxBlurFilterThread(mBitmap).start();

            }
        });
        // fb.display(avatar, avatarURL);
        FinalBitmapLoadTool.display(avatarURL, avatar, R.drawable.head_bg);
    }

    /**
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFRESH_MYCARD_AVATAR);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 定义广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.REFRESH_MYCARD_AVATAR)) {// 更新我的名片头像i
                isRefush = true;
                getMyCard();

            }
        }
    };

    private void getMyCard() {
        task = new ReadMyCardTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (!isRefush) {
                    setAvatar();
                } else {
                    loadingAvatar(card.getAvatar());
                }
                List<PersonDetail> details = card.getDetails();
                clearData();
                // 数据分类
                for (PersonDetail detail : details) {
                    valuesClassification(detail.getId(), detail.getType(),
                            detail.getValue(), detail.getStart(),
                            detail.getEnd());
                }
                notifyAdapter();
            }

            @Override
            public void readDBFinish() {
            }
        });
        task.executeWithCheckNet(card);
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
        adapter.notifyDataSetChanged();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
            lists.add(showBasicList);
        }
        footView.setVisibility(View.VISIBLE);
    }

    /**
     * 数据分类
     * @param id
     * @param key
     * @param value
     */
    public void valuesClassification(int id, PersonDetailType key,
            String value, String start, String end) {
        Info info = new Info();
        info.setValue(value);
        info.setId(id + "");
        info.setType(key.name());
        int sortKey = SortPersonType.typeSort.get(key) == null ? 0
                : SortPersonType.typeSort.get(key);
        info.setSortKey(sortKey);
        String typekey = "";
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
        } else if (Arrays.toString(UserInfoUtils.basicUserStr).contains(
                key.name())) {
            typekey = UserInfoUtils.convertToChines(key.name());
            info.setKey(typekey);
            info.setTitleKey(UserInfoUtils.infoTitleKey[0]);
            showBasicList.add(info);
        }
    }

    class GroupModle {
        String title = "";

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
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

    class Adapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("NewApi")
        @Override
        public View getChildView(final int groupPosition,
                final int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            String value = lists.get(groupPosition).get(childPosition)
                    .getValue();
            String key = lists.get(groupPosition).get(childPosition).getKey();
            String type = lists.get(groupPosition).get(childPosition).getType();

            ChildViewHolder cHolder = null;
            ChildViewHolderBasic holderBasic = null;
            EduHolderValues ediHolder = null;
            int viewType = getItemViewType(groupPosition);
            switch (viewType) {
                case 1:
                    cHolder = new ChildViewHolder();
                    convertView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.user_info_child_item, null);
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
                    convertView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.user_info_child_item_edu_work, null);
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
                    convertView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.user_info_child_item_basic, null);
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
                        cHolder.bg.setBackgroundColor(getActivity()
                                .getResources().getColor(R.color.f6));
                    }
                    break;

                case 2:
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
                        ediHolder.bg.setBackgroundColor(getActivity()
                                .getResources().getColor(R.color.f6));
                    }
                    break;
                case 4:
                    holderBasic.key.setText(key);
                    if ("D_GENDAR".equals(type)) {
                        if (value.equals("1") || value.equals("男")) {
                            holderBasic.value.setText("男");
                        } else if (value.equals("2") || value.equals("女")) {
                            holderBasic.value.setText("女");
                        }
                    } else {
                        holderBasic.value.setText(value);
                    }
                    if (groupPosition % 2 == 0) {
                        holderBasic.bg.setBackgroundColor(Color.WHITE);
                    } else {
                        holderBasic.bg.setBackgroundColor(getActivity()
                                .getResources().getColor(R.color.f6));
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
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.listview_item, null);
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
                gHolder.bg.setBackgroundColor(getActivity().getResources()
                        .getColor(R.color.f6));
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
            if ("教育经历".equals(viewType)) {
                return TYPE_2;
            } else if ("工作经历".equals(viewType)) {
                return TYPE_2;
            } else if ("基本信息".equals(viewType)) {
                return TYPE_4;
            } else {
                return TYPE_1;
            }
        }

    }

    class ChildViewHolderBasic {
        TextView key;
        TextView value;
        ImageView img;
        RelativeLayout bg;
        ImageView topLine;
        ImageView bottomLine;

    }

    class ChildViewHolder {
        TextView key;
        TextView value;
        LinearLayout bg;
        LinearLayout valueLayout;

    }

    class ContactViewHolderValues {
        TextView key;
        TextView value;
        ImageView iconSms;
        ImageView iconCall;
        LinearLayout bg;
        LinearLayout valueLayout;
    }

    class EduHolderValues {
        TextView key;
        TextView value;
        TextView startTime;
        TextView endTime;
        LinearLayout bg;
        LinearLayout valueLayout;
    }

    class GroupViewHoler {
        LinearLayout bg;
        TextView titleKey;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((SlidingActivity) getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.btnedit:
                initentEditActivity();
                break;
            case R.id.btnHistory:
                Intent intent = new Intent();
                intent.putExtra("uid", Global.getIntUid());
                intent.putExtra("cid", 0);
                intent.putExtra("headicon", card.getAvatar());
                intent.setClass(getActivity(), ChangeHistoryActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            case R.id.avatar:
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(card.getAvatar().replace("_160x160", ""));
                intent = new Intent(getActivity(),
                        AvatarImagePagerActivity.class);
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

    private void initentEditActivity() {
        Intent it = new Intent();
        it.putExtra("avatar", card.getAvatar());
        Bundle bundle = new Bundle();
        addInSex();
        bundle.putSerializable("basicList", (Serializable) showBasicList);
        bundle.putSerializable("contactList", (Serializable) showContactList);
        bundle.putSerializable("socialList", (Serializable) showSocialList);
        bundle.putSerializable("addressList", (Serializable) showAddressList);
        bundle.putSerializable("eduList", (Serializable) showEduList);
        bundle.putSerializable("workList", (Serializable) showWorkList);
        bundle.putSerializable("emailList", (Serializable) showEmailList);
        it.putExtras(bundle);
        it.putExtra("circleMumber", card);
        it.setClass(getActivity(), MyCardEditActivity.class);
        getActivity().startActivity(it);
        Utils.leftOutRightIn(getActivity());
    }

    private void addInSex() {
        for (Info in : showBasicList) {
            if (PersonDetailType.D_GENDAR.name().equals(in.getType())) {
                if ("1".equals(in.getValue()) || "男".equals(in.getValue())) {
                    in.setValue("男");
                } else if ("2".equals(in.getValue())
                        || "女".equals(in.getValue())) {
                    in.setValue("女");
                }

                break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        int[] location = new int[2];
        emptheadview.getLocationInWindow(location);
        int top = Utils.px2dip(getActivity(), location[1]);
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
                topsi = txtShow.getTop();
                botsi = txtShow.getBottom();
            }
            txtShow.layout(txtShow.getLeft(),
                    topsi - Utils.dip2px(getActivity(), itemposiotion - top),
                    txtShow.getRight(),
                    botsi - Utils.dip2px(getActivity(), itemposiotion - top));
        } else {
            avatar.setVisibility(View.INVISIBLE);
            iv_headbg.setVisibility(View.INVISIBLE);
            txtShow.layout(txtShow.getLeft(),
                    topsi - Utils.dip2px(getActivity(), 70),
                    txtShow.getRight(), botsi - Utils.dip2px(getActivity(), 70));
        }
    }

}
