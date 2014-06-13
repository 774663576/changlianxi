package com.changlianxi.tab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.AddCircleMemberActivity;
import com.changlianxi.R;
import com.changlianxi.SetingPublicInfomationActivity;
import com.changlianxi.UserInfoActivity;
import com.changlianxi.adapter.MemberAdapter;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.popwindow.SearchLayerPopwindow;
import com.changlianxi.popwindow.SearchLayerPopwindow.OnCancleClick;
import com.changlianxi.task.AcceptCircleInvitationTask;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.CircleMemberListTask;
import com.changlianxi.task.RefuseCircleInvitationTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;
import com.changlianxi.view.SearchEditText;

public class MemberFragment extends Fragment implements
        OnTouchingLetterChangedListener, touchUp, OnItemClickListener,
        OnClickListener, OnPullDownListener {
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private QuickAlphabeticBar indexBar;// 右侧字母拦
    private int position;// 当前字母子listview中所对应的位置
    private ListView listView;
    private PullDownView mPullDownView;
    private TextView selectedChar;// 显示选择字母
    private ImageView btadd;
    private ImageView back;
    private TextView title;
    private Button btnOk;// 接受邀请按钮
    private Button btnCancle;// 拒绝邀请按钮
    private SearchEditText editSearch;
    private ViewStub vsLayInvite;
    private ViewStub vsLayEdit;
    private View fotter;
    private TextView footViewText;
    private RelativeLayout parentLayout;
    private SearchLayerPopwindow searchPopwindow;
    private String circleName = "";
    private int newMemberCount = 0;
    private int newMyDetailEditCount = 0;// 我的资料修改数
    private int cid;
    private int inviterID;
    private boolean isNewCircle;// 是否是新邀请的圈子
    private List<CircleMember> lists = new ArrayList<CircleMember>();
    private MemberAdapter adapter;
    private CircleMemberListTask task;
    private CircleMemberList circleMemberList;
    private Dialog progressDialog;
    private boolean isAuth;
    private CircleMember member = null;
    private LinearLayout titileLayout;
    private TextView inviteName;
    private Button btnLook;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    lists = circleMemberList.getLegalMembers();
                    if (lists.size() > 0 && progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    isAuth();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        circleName = getArguments().getString("circleName");
        cid = getArguments().getInt("cid");
        inviterID = getArguments().getInt("inviterID");
        newMemberCount = getArguments().getInt("newMemberCount");
        newMyDetailEditCount = getArguments().getInt("newMyDetailEditCount");
        isNewCircle = getArguments().getBoolean("isNewCircle", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.member_fragment, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isOnCreate) {
            initView();
        }
        isOnCreate = true;
    }

    private void initView() {
        titileLayout = (LinearLayout) getView().findViewById(R.id.titileLay);
        mPullDownView = (PullDownView) getView().findViewById(
                R.id.PullDownlistView);
        listView = mPullDownView.getListView();
        listView.setCacheColorHint(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(getActivity().getResources().getDrawable(
                R.color.member_list_item_line));
        listView.setDividerHeight(1);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setFooterDividersEnabled(false);
        parentLayout = (RelativeLayout) getView().findViewById(R.id.layout);
        back = (ImageView) getView().findViewById(R.id.back);
        btadd = (ImageView) getView().findViewById(R.id.rightImg);
        btadd.setImageResource(R.drawable.icon_add);
        title = (TextView) getView().findViewById(R.id.titleTxt);
        listView.setCacheColorHint(0);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.header,
                null);
        editSearch = (SearchEditText) view.findViewById(R.id.search);
        listView.addHeaderView(view, null, false);
        indexBar = (QuickAlphabeticBar) getView().findViewById(R.id.indexBar);
        indexBar.getBackground().setAlpha(0);
        indexBar.setOnTouchUp(this);
        selectedChar = (TextView) getView().findViewById(R.id.selected_tv);
        selectedChar.setVisibility(View.GONE);
        fotter = LayoutInflater.from(getActivity()).inflate(
                R.layout.auth_foot_view, null, false);
        footViewText = (TextView) fotter.findViewById(R.id.txtFoot);
        initLayInvite();
        initLayEdit();
        setListener();
    }

    private void initLayEdit() {
        if (newMyDetailEditCount == 0) {
            return;
        }
        vsLayEdit = (ViewStub) getView().findViewById(R.id.lay_edit);
        vsLayEdit.inflate();
        btnLook = (Button) getView().findViewById(R.id.btnLook);
        btnLook.setOnClickListener(this);
    }

    /**
     * 初始化接受邀请或者拒绝布局
     * @param name
     */
    private void initLayInvite() {
        if (!isNewCircle) {
            return;
        }
        vsLayInvite = (ViewStub) getView().findViewById(
                R.id.viewstub_lay_invite);
        vsLayInvite.inflate();
        btnOk = (Button) getView().findViewById(R.id.btnOk);
        btnCancle = (Button) getView().findViewById(R.id.btnCancle);
        btnOk.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        inviteName = (TextView) getView().findViewById(R.id.txtShow);

    }

    private void setInviteName() {
        if (!isNewCircle) {
            return;
        }
        CircleMember c = new CircleMember(cid, 0, inviterID);
        c.getNameAndAvatar(DBUtils.getDBsa(1));
        inviteName.setText(c.getName() + "  邀请您加入此圈子");
    }

    private void setListener() {
        title.setText(circleName);
        listView.setOnItemClickListener(this);
        indexBar.setOnTouchingLetterChangedListener(this);
        back.setOnClickListener(this);
        mPullDownView.setOnPullDownListener(this);
        mPullDownView.notifyDidMore();
        editSearch.setOnClickListener(this);
        btadd.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        progressDialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        progressDialog.show();
        circleMemberList = new CircleMemberList(cid);
        registerBoradcastReceiver();
        getCircleMembers(newMemberCount, newMyDetailEditCount);
    }

    /**
     * 注册该广播
     */
    private void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFRESH_CIRCLE_USER_LIST);
        myIntentFilter.addAction(Constants.UPDECIRNAME);
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
            if (action.equals(Constants.REFRESH_CIRCLE_USER_LIST)) {// 更新成员列表列表
                newMemberCount = 1;// 把newMemberCount设为大于0的数
                getCircleMembers(newMemberCount, newMyDetailEditCount);
            } else if (action.equals(Constants.UPDECIRNAME)) {// 更新标题
                String circleName = intent.getStringExtra("circleName");
                title.setText(circleName);
            }
        }
    };

    private void showSearchPopwindow() {
        titileLayout.setVisibility(View.GONE);
        titileLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.up_out));
        searchPopwindow = new SearchLayerPopwindow(getActivity(), parentLayout,
                lists, cid);
        searchPopwindow.setCallBack(new OnCancleClick() {
            @Override
            public void onCancle() {
                titileLayout.setVisibility(View.VISIBLE);
            }
        });

        searchPopwindow.show();
    }

    private void isAuth() {
        member = new CircleMember(cid, 0, Global.getIntUid());
        member.getMemberState(DBUtils.getDBsa(1));
        isAuth = member.getState().equals(CircleMemberState.STATUS_KICKOFFING)
                || member.getState().equals(CircleMemberState.STATUS_VERIFIED);
        addFooterView();
    }

    private void addFooterView() {
        if (adapter == null) {
            adapter = new MemberAdapter(getActivity(), lists);
            adapter.setAuth(isAuth);
            listView.setAdapter(adapter);
        } else {
            adapter.setAuth(isAuth);
            adapter.setData(lists);
        }
        if (!isAuth) {
            if (member.getState().equals(CircleMemberState.STATUS_INVITING)) {
                footViewText.setText("您还未加入本圈子，暂时只能看到20个圈子成员，想看完整列表请尽快加入！");
            } else if (member.getState().equals(
                    CircleMemberState.STATUS_ENTER_AND_VERIFYING)) {
                footViewText.setText("您还不是认证成员，暂时只能看到20个圈子成员，想看完整列表请尽快去认证吧！");

            }
            if (lists.size() == 0) {
                return;
            }
            if (lists.size() > 20) {
                if (listView.getFooterViewsCount() > 0) {
                    listView.removeFooterView(fotter);
                }
                listView.addFooterView(fotter, null, false);
                for (int i = lists.size() - 1; i >= 20; i--) {
                    lists.remove(i);
                }
            }
        } else {
            btadd.setVisibility(View.VISIBLE);
            if (lists.size() > 5) {
                addCircleMemberCountFooter();
            }
        }

    }

    private void addCircleMemberCountFooter() {
        if (listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(fotter);
        }
        listView.addFooterView(fotter, null, false);

        footViewText.setText("共" + lists.size() + "个联系人");

    }

    private void getCircleMembers(int newMemberCount, int newMyDetailEditCount) {
        task = new CircleMemberListTask(newMemberCount, newMyDetailEditCount);
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                mPullDownView.RefreshComplete();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                lists = circleMemberList.getLegalMembers();
                isAuth();
                setInviteName();
            }

            @Override
            public void readDBFinish() {
                mHandler.sendEmptyMessage(0);
            }
        });
        task.executeWithCheckNet(circleMemberList);
    }

    /**
     * 接受邀请
     */
    private void acceptInvitation() {
        progressDialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        progressDialog.show();
        member = new CircleMember(cid, 0, Global.getIntUid());
        AcceptCircleInvitationTask task = new AcceptCircleInvitationTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                vsLayInvite.setVisibility(View.GONE);
                newMemberCount = 1;// 把newMemberCount设为大于0的数
                getCircleMembers(newMemberCount, newMyDetailEditCount);
                BroadCast.sendBroadCast(getActivity(),
                        Constants.REFRESH_CIRCLE_LIST);
                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        SetingPublicInfomationActivity.class);
                intent.putExtra("cid", cid);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                Utils.leftOutRightIn(getActivity());
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(member);
    }

    private void refuseInvitation() {
        progressDialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        progressDialog.show();
        member = new CircleMember(cid, 0, Integer.valueOf(Global.getUid()));
        RefuseCircleInvitationTask task = new RefuseCircleInvitationTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Constants.EXIT_CIRCLE);
                intent.putExtra("cid", cid);
                BroadCast.sendBroadCast(getActivity(), intent);
                getActivity().finish();
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(member);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                getActivity().finish();
                Utils.rightOut(getActivity());
                break;
            case R.id.rightImg:
                Intent it = new Intent();
                it.setClass(getActivity(), AddCircleMemberActivity.class);
                it.putExtra("cid", cid);
                it.putExtra("type", "add");// 添加成员
                it.putExtra("cirName", title.getText().toString());
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.btnOk:
                acceptInvitation();
                break;
            case R.id.btnCancle:
                confirmDialog();

                break;
            case R.id.search:
                if (!isAuth) {
                    member = new CircleMember(cid, 0, Global.getIntUid());
                    member.getMemberState(DBUtils.getDBsa(1));
                    if (member.getState().equals(
                            CircleMemberState.STATUS_INVITING)) {
                        Utils.showToast("您还未加入本圈子，不能使用搜索功能，赶快加入圈子吧！",
                                Toast.LENGTH_SHORT);
                    } else if (member.getState().equals(
                            CircleMemberState.STATUS_ENTER_AND_VERIFYING)) {
                        Utils.showToast("您还不是认证成员，不能使用搜索功能，赶快去认证吧！",
                                Toast.LENGTH_SHORT);
                    }
                    return;
                }

                showSearchPopwindow();
                break;
            case R.id.btnLook:
                intentUserInfoActivity(findMeFromMembersList());
                vsLayEdit.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private CircleMember findMeFromMembersList() {
        for (CircleMember m : lists) {
            if (m.getUid() == Global.getIntUid()) {
                return m;
            }

        }
        return null;

    }

    private void confirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(getActivity(),
                "您真的要拒绝邀请？您可能会错过跟老朋友们的互动，也会错过老朋友们分享的精彩内容哦！", "确定", "取消",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        refuseInvitation();
                    }

                    @Override
                    public void onCancleClick() {
                    }
                });
        dialog.show();
    }

    private void intentUserInfoActivity(CircleMember member) {
        if (member == null) {
            return;
        }
        Intent it = new Intent();
        it.setClass(getActivity(), UserInfoActivity.class);
        Bundle bundle = new Bundle();
        // if (member.getState().equals(CircleMemberState.STATUS_INVITING)) {
        // bundle.putSerializable("listMemers", (Serializable) lists);
        // }
        bundle.putSerializable("member", member);
        it.putExtras(bundle);
        startActivity(it);
        this.getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        position = arg2 - 2;
        int uid = 0;
        uid = lists.get(position).getUid();
        if (!isAuth && uid != Global.getIntUid()) {
            CircleMember member = new CircleMember(cid, 0, Global.getIntUid());
            member.getMemberState(DBUtils.getDBsa(1));
            if (member.getState().equals(CircleMemberState.STATUS_INVITING)) {
                Utils.showToast("亲，加入圈子以后才能看到这些精彩内容哦！", Toast.LENGTH_SHORT);
            } else {
                Utils.showToast("非认证成员暂时看不到其他人的详细信息，快去找朋友帮您认证吧",
                        Toast.LENGTH_SHORT);
            }
            return;
        }
        intentUserInfoActivity(lists.get(position));

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        indexBar.getBackground().setAlpha(200);
        selectedChar.setText(s);
        selectedChar.setVisibility(View.VISIBLE);
        position = (findIndexer(s));
        if (position != 0) {
            listView.setSelection(position);
        }
    }

    @Override
    public void onTouchUp() {
        indexBar.getBackground().setAlpha(0);
        selectedChar.setVisibility(View.GONE);
        if (position != 0) {
            listView.setSelection(position);
        }
    }

    /**
     * 设置listview的当前选中值
     * 
     * @param s
     * @return
     */
    private int findIndexer(String s) {
        int position = 0;
        for (int i = 0; i < lists.size(); i++) {
            String sortkey = lists.get(i).getSortkey();
            if (sortkey.startsWith(s)) {
                position = i + 2;
                break;
            }
        }
        return position;
    }

    @Override
    public void onRefresh() {
        newMemberCount = 1;
        getCircleMembers(newMemberCount, newMyDetailEditCount);
    }

    @Override
    public void onMore() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);

    }
}
