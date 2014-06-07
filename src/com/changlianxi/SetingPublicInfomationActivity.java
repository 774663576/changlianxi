package com.changlianxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.InitMyDetailsTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.SortPersonType;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.MySlipSwitch;
import com.changlianxi.view.MySlipSwitch.OnSwitchListener;

public class SetingPublicInfomationActivity extends BaseActivity implements
        OnClickListener {
    private ImageView back;
    private Button btnSave;
    private TextView title;
    private List<ChildModle> basic = new ArrayList<ChildModle>();;
    private List<ChildModle> contact = new ArrayList<ChildModle>();;
    private List<ChildModle> social = new ArrayList<ChildModle>();;
    private List<ChildModle> email = new ArrayList<ChildModle>();;
    private List<ChildModle> add = new ArrayList<ChildModle>();;
    private List<ChildModle> edu = new ArrayList<ChildModle>();;
    private List<ChildModle> work = new ArrayList<ChildModle>();;
    private List<GroupModle> group;
    private List<List<ChildModle>> child = new ArrayList<List<ChildModle>>();
    private MyCard card;
    private ExpandableListView eListView;
    private Adapter adapter;
    private final int TYPE_1 = 1;
    private final int TYPE_2 = 2;
    private final int TYPE_3 = 3;
    private int cid;
    private CircleMember circleMember;
    private String circleIDs = "";
    private String personalIDs = "";
    private TextView text;
    private String type = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_public_infomation);
        cid = getIntent().getIntExtra("cid", 0);
        type = getIntent().getStringExtra("type");
        group = new ArrayList<GroupModle>();
        initView();
        card = new MyCard(0, Global.getIntUid());
        card.read(DBUtils.getDBsa(1));
        card.readDetails(DBUtils.getDBsa(1));
        circleMember = new CircleMember(cid, 0, Global.getIntUid());
        circleMember.read(DBUtils.getDBsa(1));
        circleMember.readDetails(DBUtils.getDBsa(1));
        getData();
        getGroupData();
    }

    private void initView() {
        text = (TextView) findViewById(R.id.text);
        Circle c = new Circle(cid);
        c.read(DBUtils.getDBsa(1));
        if (c.getCreator() == Global.getIntUid()) {
            text.setText("请选择您在本圈子中公开的个人资料");
        }
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("设置我的资料");
        eListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        eListView.setGroupIndicator(null);
        eListView.setCacheColorHint(0);
        eListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        eListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                return true;
            };
        });
        LayoutInflater infla = LayoutInflater.from(this);
        View footView = infla.inflate(R.layout.footer_view_save, null);
        btnSave = (Button) footView.findViewById(R.id.btn_save);
        eListView.addFooterView(footView);
        adapter = new Adapter();
        eListView.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    private void getData() {
        for (PersonDetail detail : card.getDetails()) {
            valuesClassification(detail.getType(), detail.getValue(),
                    detail.getStart(), detail.getEnd(), card.getCid(),
                    detail.getId());
        }
        for (PersonDetail detail : circleMember.getDetails()) {
            valuesClassification(detail.getType(), detail.getValue(),
                    detail.getStart(), detail.getEnd(), circleMember.getCid(),
                    detail.getId());
        }
    }

    private void getGroupData() {
        GroupModle gmodle = null;
        if (basic.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[0]);
            group.add(gmodle);
            child.add(basic);
        }
        if (contact.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[1]);
            group.add(gmodle);
            child.add(contact);

        }
        if (email.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[2]);
            group.add(gmodle);
            child.add(email);

        }
        if (social.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[3]);
            group.add(gmodle);
            child.add(social);

        }
        if (add.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[4]);
            group.add(gmodle);
            child.add(add);

        }
        if (edu.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[5]);
            group.add(gmodle);
            child.add(edu);

        }
        if (work.size() > 0) {
            gmodle = new GroupModle();
            gmodle.setTitle(UserInfoUtils.infoTitleKey[6]);
            group.add(gmodle);
            child.add(work);

        }
        sort();
        adapter.notifyDataSetChanged();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            eListView.expandGroup(i);

        }
    }

    /**
     * 数据分类
     * 
     * @param id
     * @param key
     * @param value
     */
    public void valuesClassification(PersonDetailType type, String value,
            String startTime, String endTime, int cid, int id) {
        ChildModle cmodle = null;
        if (type.name().equals("D_NICKNAME") || type.name().equals("D_REMARK")) {
            return;
        }
        cmodle = new ChildModle();
        cmodle.setKey(type);
        cmodle.setValue(value);
        cmodle.setCid(cid);
        cmodle.setId(id);
        int sortKey = SortPersonType.typeSort.get(type) == null ? 100
                : SortPersonType.typeSort.get(type);
        cmodle.setSortKey(sortKey);
        if (type.name().equals("D_AVATAR")) {
            for (ChildModle c : basic) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
                if (c.getKey().equals(type)
                        && (type == PersonDetailType.D_AVATAR)) {
                    cmodle.setFalg(false);
                    break;
                }
            }
            basic.add(cmodle);
            return;
        }
        if (Arrays.toString(UserInfoUtils.workStr).contains(type.name())) {
            cmodle.setEndTime(endTime);
            cmodle.setStartTime(startTime);
            for (ChildModle c : work) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            work.add(cmodle);
            return;
        }
        if (Arrays.toString(UserInfoUtils.basicUserStr).contains(type.name())) {
            for (ChildModle c : basic) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
                if (c.getKey().equals(type)
                        && (type == PersonDetailType.D_NAME
                                || type == PersonDetailType.D_BIRTHDAY || type == PersonDetailType.D_GENDAR)) {
                    cmodle.setFalg(false);
                    break;
                }
            }
            basic.add(cmodle);

        } else if (Arrays.toString(UserInfoUtils.contactPhone).contains(
                type.name())) {
            for (ChildModle c : contact) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            contact.add(cmodle);

        } else if (Arrays.toString(UserInfoUtils.socialStr).contains(
                type.name())) {
            for (ChildModle c : social) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            social.add(cmodle);

        } else if (Arrays.toString(UserInfoUtils.emailStr)
                .contains(type.name())) {
            for (ChildModle c : email) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            email.add(cmodle);

        } else if (Arrays.toString(UserInfoUtils.addressStr).contains(
                type.name())) {
            for (ChildModle c : add) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            add.add(cmodle);

        } else if (Arrays.toString(UserInfoUtils.eduStr).contains(type.name())) {
            cmodle.setEndTime(endTime);
            cmodle.setStartTime(startTime);
            for (ChildModle c : edu) {
                if (c.getKey().equals(type) && c.getValue().equals(value)) {
                    return;
                }
            }
            edu.add(cmodle);
        }

    }

    class GroupModle {
        String title = "";
        boolean falg = false;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isFalg() {
            return falg;
        }

        public void setFalg(boolean falg) {
            this.falg = falg;
        }

    }

    class ChildModle {
        boolean falg = true;
        PersonDetailType key;
        String value = "";
        String endTime = "";
        String startTime = "";
        int sortKey = 0;
        int cid;
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public int getSortKey() {
            return sortKey;
        }

        public void setSortKey(int sortKey) {
            this.sortKey = sortKey;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public PersonDetailType getKey() {
            return key;
        }

        public void setKey(PersonDetailType key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isFalg() {
            return falg;
        }

        public void setFalg(boolean falg) {
            this.falg = falg;
        }
    }

    // public int getItemViewType(int groupPosition, int childPosition) {
    // String viewType = group.get(groupPosition).getTitle();
    // if ("教育经历".equals(viewType)) {
    // return TYPE_2;
    // } else if ("工作经历".equals(viewType)) {
    // return TYPE_2;
    // } else if ("基本信息".equals(viewType)) {
    // PersonDetailType type = child.get(groupPosition).get(childPosition)
    // .getKey();
    // if (type.equals(PersonDetailType.D_AVATAR)) {
    // return TYPE_1;
    // } else {
    // return TYPE_3;
    // }
    // } else {
    // return TYPE_3;
    // }
    // }

    class Adapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return child.get(groupPosition).get(childPosition);
        }

        @Override
        public int getChildTypeCount() {
            return 4;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            String viewType = group.get(groupPosition).getTitle();
            if ("教育经历".equals(viewType)) {
                return TYPE_2;
            } else if ("工作经历".equals(viewType)) {
                return TYPE_2;
            } else if ("基本信息".equals(viewType)) {
                PersonDetailType type = child.get(groupPosition)
                        .get(childPosition).getKey();
                if (type.equals(PersonDetailType.D_AVATAR)) {
                    return TYPE_1;
                } else {
                    return TYPE_3;
                }
            } else {
                return TYPE_3;
            }
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition,
                final int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            final String key = PersonDetailType.toText(child.get(groupPosition)
                    .get(childPosition).getKey());
            String value = child.get(groupPosition).get(childPosition)
                    .getValue();
            // int viewType = getItemViewType(groupPosition, childPosition);
            int viewType = getChildType(groupPosition, childPosition);

            ChildViewHoler cHolder = null;
            ChildViewHolerAvatar avatarHolder = null;
            ChildViewHolerEdu eduHolder = null;
            // if (convertView == null) {
            switch (viewType) {
                case TYPE_1:
                    avatarHolder = new ChildViewHolerAvatar();
                    convertView = LayoutInflater
                            .from(SetingPublicInfomationActivity.this)
                            .inflate(
                                    R.layout.seting_public_infomation_child_avatar_item,
                                    null);
                    avatarHolder.key = (TextView) convertView
                            .findViewById(R.id.key);
                    avatarHolder.avatar = (CircularImage) convertView
                            .findViewById(R.id.avatar);
                    avatarHolder.mySwitch = (MySlipSwitch) convertView
                            .findViewById(R.id.mySwitch);
                    convertView.setTag(avatarHolder);
                    break;
                case TYPE_2:
                    eduHolder = new ChildViewHolerEdu();
                    convertView = LayoutInflater.from(
                            SetingPublicInfomationActivity.this).inflate(
                            R.layout.seting_public_infomation_child_edu_item,
                            null);
                    eduHolder.key = (TextView) convertView
                            .findViewById(R.id.key);
                    eduHolder.valeu = (TextView) convertView
                            .findViewById(R.id.value);
                    eduHolder.time = (TextView) convertView
                            .findViewById(R.id.time);
                    eduHolder.mySwitch = (MySlipSwitch) convertView
                            .findViewById(R.id.mySwitch);
                    convertView.setTag(eduHolder);

                    break;
                case TYPE_3:
                    cHolder = new ChildViewHoler();
                    convertView = LayoutInflater.from(
                            SetingPublicInfomationActivity.this).inflate(
                            R.layout.seting_public_infomation_child_item, null);
                    cHolder.key = (TextView) convertView.findViewById(R.id.key);
                    cHolder.valeu = (TextView) convertView
                            .findViewById(R.id.value);
                    cHolder.mySwitch = (MySlipSwitch) convertView
                            .findViewById(R.id.mySwitch);
                    cHolder.txtMust = (TextView) convertView
                            .findViewById(R.id.txtMust);
                    convertView.setTag(cHolder);
                    break;
                default:
                    break;
            }
            // } else {
            // switch (viewType) {
            // case 1:
            // avatarHolder = (ChildViewHolerAvatar) convertView
            // .getTag();
            // break;
            // case 2:
            // eduHolder = (ChildViewHolerEdu) convertView.getTag();
            // break;
            // case 3:
            // cHolder = (ChildViewHoler) convertView.getTag();
            // break;
            // default:
            // break;
            // }
            // }
            switch (viewType) {
                case TYPE_1:
                    avatarHolder.key.setText(key + ":");
                    // fb.display(avatarHolder.avatar, value);
                    FinalBitmapLoadTool.display(value, avatarHolder.avatar,
                            R.drawable.head_bg);

                    avatarHolder.mySwitch
                            .setOnSwitchListener(new OnSwitchListener() {

                                @Override
                                public void onSwitched(boolean isSwitchOn) {
                                    child.get(groupPosition).get(childPosition)
                                            .setFalg(isSwitchOn);
                                    cannotChooseDouble(
                                            key,
                                            isSwitchOn,
                                            child.get(groupPosition)
                                                    .get(childPosition)
                                                    .getCid());

                                }
                            });
                    avatarHolder.mySwitch.setSwitchState(child
                            .get(groupPosition).get(childPosition).isFalg());
                    break;
                case TYPE_2:
                    String end = DateUtils.interceptDateStr(
                            child.get(groupPosition).get(childPosition)
                                    .getEndTime(), "yyyy-MM-dd");
                    String start = DateUtils.interceptDateStr(
                            child.get(groupPosition).get(childPosition)
                                    .getStartTime(), "yyyy-MM-dd");
                    if ("".equals(end)) {
                        end = "无";
                    }
                    if ("".equals(start)) {
                        start = "无";
                    }
                    eduHolder.key.setText(key + ":");
                    eduHolder.valeu.setText(value);
                    eduHolder.time.setText(start + "至" + end);
                    eduHolder.mySwitch
                            .setOnSwitchListener(new OnSwitchListener() {

                                @Override
                                public void onSwitched(boolean isSwitchOn) {
                                    child.get(groupPosition).get(childPosition)
                                            .setFalg(isSwitchOn);

                                }
                            });
                    eduHolder.mySwitch.setSwitchState(child.get(groupPosition)
                            .get(childPosition).isFalg());
                    break;
                case TYPE_3:
                    cHolder.key.setText(key + ":");
                    cHolder.valeu.setText(value);
                    if ("姓名".equals(key)) {
                        int count = getCountOfKey(key);
                        if (count == 1) {
                            cHolder.mySwitch.setVisibility(View.INVISIBLE);
                            cHolder.txtMust.setVisibility(View.VISIBLE);
                        }
                    }
                    if ("手机号".equals(key)) {
                        cHolder.mySwitch.setVisibility(View.INVISIBLE);
                        cHolder.txtMust.setVisibility(View.VISIBLE);
                    }
                    if ("性别".equals(key)) {
                        if ("1".equals(value)) {
                            cHolder.valeu.setText("男");
                        } else {
                            cHolder.valeu.setText("女");

                        }
                    }
                    cHolder.mySwitch
                            .setOnSwitchListener(new OnSwitchListener() {

                                @Override
                                public void onSwitched(boolean isSwitchOn) {
                                    child.get(groupPosition).get(childPosition)
                                            .setFalg(isSwitchOn);
                                    if ("姓名".equals(key) || "性别".equals(key)
                                            || "生日".equals(key)) {
                                        cannotChooseDouble(
                                                key,
                                                isSwitchOn,
                                                child.get(groupPosition)
                                                        .get(childPosition)
                                                        .getCid());
                                    }

                                }
                            });
                    cHolder.mySwitch.setSwitchState(child.get(groupPosition)
                            .get(childPosition).isFalg());
                    break;
                default:
                    break;
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return child.get(groupPosition).size();
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
                convertView = LayoutInflater.from(
                        SetingPublicInfomationActivity.this).inflate(
                        R.layout.seting_public_infomation_group_item, null);
                gHolder.titleKey = (TextView) convertView
                        .findViewById(R.id.titleKey);
                gHolder.offOrOn = (TextView) convertView
                        .findViewById(R.id.offOrOn);
                convertView.setTag(gHolder);
            } else {
                gHolder = (GroupViewHoler) convertView.getTag();
            }
            gHolder.offOrOn.setOnClickListener(new GroupOffOrOnClick(
                    groupPosition));
            gHolder.titleKey.setText(group.get(groupPosition).getTitle());
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

    }

    private int getCountOfKey(String key) {
        int count = 0;
        for (ChildModle c : basic) {
            String keyStr = PersonDetailType.toText(c.getKey());
            if (keyStr.equals(key)) {
                count++;
            }
        }
        return count;
    }

    private void cannotChooseDouble(String key, boolean flag, int cid) {
        for (ChildModle c : basic) {
            String keyStr = PersonDetailType.toText(c.getKey());
            if (keyStr.equals(key) && c.getCid() != cid) {
                c.setFalg(!flag);
            }
        }
        adapter.notifyDataSetChanged();
    }

    class GroupOffOrOnClick implements OnClickListener {
        int groupPosition;

        public GroupOffOrOnClick(int groupPosition) {
            this.groupPosition = groupPosition;
        }

        @Override
        public void onClick(View v) {
            TextView text = (TextView) v;
            String str = text.getText().toString();
            if ("选择全部".equals(str)) {
                text.setText("取消全部");
                // group.get(groupPosition).setFalg(false);
                text.setTextColor(getResources().getColor(
                        R.color.setingpublic80));
                setOffOrOn(true, groupPosition);

            } else {
                text.setText("选择全部");
                // group.get(groupPosition).setFalg(true);
                text.setTextColor(getResources().getColor(
                        R.color.setingpublic14));
                setOffOrOn(false, groupPosition);

            }
            adapter.notifyDataSetChanged();
        }

    }

    private void setOffOrOn(boolean flag, int groupPositon) {
        String str = group.get(groupPositon).getTitle();
        if (str.equals(UserInfoUtils.infoTitleKey[0])) {
            for (int i = 0; i < basic.size(); i++) {
                ChildModle c = basic.get(i);
                c.setFalg(flag);
                if (!flag) {
                    if (i - 1 >= 0
                            && (c.getKey().equals(PersonDetailType.D_NAME))) {
                        if (basic.get(i - 1).getKey().equals(c.getKey())) {
                            c.setFalg(true);
                        }
                    }
                } else {
                    if (i - 1 >= 0
                            && (c.getKey() == PersonDetailType.D_NAME
                                    || c.getKey() == PersonDetailType.D_AVATAR
                                    || c.getKey() == PersonDetailType.D_BIRTHDAY || c
                                    .getKey() == PersonDetailType.D_GENDAR)) {
                        if (basic.get(i - 1).getKey() == c.getKey()) {
                            c.setFalg(false);
                        }
                    }
                }
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[1])) {
            for (ChildModle c : contact) {
                if (c.getKey() == PersonDetailType.D_CELLPHONE) {
                    c.setFalg(true);
                    continue;
                }
                c.setFalg(flag);
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[2])) {
            for (ChildModle c : email) {
                c.setFalg(flag);
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[3])) {
            for (ChildModle c : social) {
                c.setFalg(flag);
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[4])) {
            for (ChildModle c : add) {
                c.setFalg(flag);
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[5])) {
            for (ChildModle c : edu) {
                c.setFalg(flag);
            }
        } else if (str.equals(UserInfoUtils.infoTitleKey[6])) {
            for (ChildModle c : work) {
                c.setFalg(flag);
            }
        }
    }

    class GroupViewHoler {
        TextView titleKey;
        TextView offOrOn;
    }

    class ChildViewHoler {
        TextView key;
        TextView valeu;
        MySlipSwitch mySwitch;
        TextView txtMust;

    }

    class ChildViewHolerEdu {
        TextView key;
        TextView valeu;
        MySlipSwitch mySwitch;
        TextView time;
    }

    class ChildViewHolerAvatar {
        TextView key;
        MySlipSwitch mySwitch;
        CircularImage avatar;
    }

    private void sort() {
        Collections.sort(basic, getComparator());
        Collections.sort(contact, getComparator());
        Collections.sort(social, getComparator());
        Collections.sort(add, getComparator());
        Collections.sort(edu, getComparator());
        Collections.sort(work, getComparator());
        Collections.sort(email, getComparator());

    }

    public static Comparator<ChildModle> getComparator() {
        return new Comparator<ChildModle>() {
            @Override
            public int compare(ChildModle l, ChildModle r) {
                Integer lSort = l.getSortKey(), rSort = r.getSortKey();
                return lSort > rSort ? 1 : -1;
            }
        };

    }

    private void getIds() {

        for (List<ChildModle> lists : child) {
            for (ChildModle c : lists) {
                if (c.isFalg()) {
                    int id = c.getId();
                    if (c.getCid() == 0) {
                        personalIDs += id + ",";
                    } else {
                        circleIDs += id + ",";

                    }
                }
            }
        }
    }

    private void seting() {
        final Dialog dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        getIds();
        InitMyDetailsTask task = new InitMyDetailsTask(cid, circleIDs,
                personalIDs);
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result == RetError.NONE) {
                    Utils.showToast("设置成功", Toast.LENGTH_SHORT);
                    BroadCast.sendBroadCast(
                            SetingPublicInfomationActivity.this,
                            Constants.REFRESH_CIRCLE_USER_LIST);
                    initenUserInfoActivity();
                }
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();
    }

    private void initenUserInfoActivity() {
        int cid = this.cid;
        int pid = circleMember.getPid();
        int uid = circleMember.getUid();
        String userName = circleMember.getName();
        String iconImg = circleMember.getAvatar();
        String cellPhone = circleMember.getCellphone();
        if ("createCircle".equals(type)) {
            pid = card.getPid();
            uid = Global.getIntUid();
            userName = card.getName();
            iconImg = card.getAvatar();
            cellPhone = card.getCellphone();
        }
        Intent it = new Intent();
        it.setClass(this, SetPublicInfoShowInfoActivity.class);
        it.putExtra("cid", cid);
        it.putExtra("pid", pid);
        it.putExtra("uid", uid);
        it.putExtra("username", userName);
        it.putExtra("iconImg", iconImg);
        it.putExtra("cellPhone", cellPhone);
        startActivity(it);
        finish();
        Utils.leftOutRightIn(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                confirmDialog();
                break;
            case R.id.btn_save:
                seting();
                break;
            default:
                break;
        }

    }

    private void confirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(this, "确定取消设置吗？", "确定", "取消",
                new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        finish();
                        Utils.rightOut(SetingPublicInfomationActivity.this);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmDialog();
        }
        return false;
    }
}
