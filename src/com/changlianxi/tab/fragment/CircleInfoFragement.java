package com.changlianxi.tab.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.EditCircleActivity;
import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.popwindow.CircleInfoPopwindow;
import com.changlianxi.popwindow.CircleInfoPopwindow.OnlistOnclick;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CircleIdetailTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class CircleInfoFragement extends Fragment implements OnClickListener,
        ImageLoadingListener {
    private TextView circleName;// 圈子名称
    private TextView titleName;
    private TextView circleDescription;// 圈子描述
    private TextView circleMemberCount;
    private TextView creatorName;
    private ImageView circleLogo;
    private ImageView back;
    private ImageView edit;
    private int cid;
    private Circle circle;
    private CircleMember cMember;
    private Dialog dialog;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private Bitmap logoBmp;
    private RelativeLayout layTitle;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setValue(circle);
                    setCreatorName();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cid = getArguments().getInt("cid", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.circle_info_fragment, null);
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
        layTitle = (RelativeLayout) getView().findViewById(R.id.titlebar);
        creatorName = (TextView) getView().findViewById(R.id.creatorName);
        titleName = (TextView) getView().findViewById(R.id.titleTxt);
        circleName = (TextView) getView().findViewById(R.id.circleName);
        circleDescription = (TextView) getView().findViewById(R.id.circleDis);
        circleLogo = (ImageView) getView().findViewById(R.id.circleLogo);
        back = (ImageView) getView().findViewById(R.id.back);
        edit = (ImageView) getView().findViewById(R.id.rightImg);
        edit.setVisibility(View.VISIBLE);
        edit.setImageResource(R.drawable.icon_set_up);
        circleMemberCount = (TextView) getView().findViewById(
                R.id.circleMemberCount);
        invisibleEdit();
        setListener();
        registerBoradcastReceiver();
    }

    private void invisibleEdit() {
        CircleMember cm = new CircleMember(cid, 0, Global.getIntUid());
        cm.getMemberState(DBUtils.getDBsa(1));
        if (cm.getState().equals(CircleMemberState.STATUS_INVITING)) {
            edit.setVisibility(View.GONE);
        }

    }

    private void setListener() {
        back.setOnClickListener(this);
        circleLogo.setOnClickListener(this);
        edit.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        // circleLogo.setImageResource(R.drawable.pic_bg_no);
        getServerData();

    }

    private void setValue(Circle circle) {
        setvalue(circle.getName(), circle.getLogo(), circle.getDescription(),
                circle.getTotalCnt() + "", circle.getVerifiedCnt() + "");
    }

    private void setvalue(String name, String cirIcon, String des,
            String total, String ver) {
        circleDescription.setText(des);
        circleName.setText(name);
        titleName.setText(name);
        setCircleImg(cirIcon);
        setCount(total, ver);
    }

    private void setCircleImg(String img) {
        if ("".equals(img)) {
            circleLogo.setImageResource(R.drawable.pic_bg_no);
        } else {
            UniversalImageLoadTool.disPlayListener(img, circleLogo,
                    R.drawable.pic_bg_no, this);
        }
    }

    private void getServerData() {
        circle = new Circle(cid);
        CircleIdetailTask circleIdetailTask = new CircleIdetailTask(circle);
        circleIdetailTask
                .setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

                    @Override
                    public void taskFinish(RetError result) {
                        if (circle == null) {
                            return;
                        }
                        setValue(circle);
                        setCreatorName();

                    }

                    @Override
                    public void readDBFinish() {
                        mHandler.sendEmptyMessage(0);
                    }
                });
        circleIdetailTask.executeWithCheckNet();
    }

    private void setCreatorName() {
        if (creatorName.getText().toString().length() == 0) {
            CircleMember c = new CircleMember(cid, 0, circle.getCreator());
            c.getNameAndAvatar(DBUtils.getDBsa(1));
            creatorName.setText(c.getName());
        }
    }

    private void intentShowBigImg() {
        List<String> imgUrl = new ArrayList<String>();
        imgUrl.add(circle.getOriginalLogo());
        Intent intent = new Intent(getActivity(),
                AvatarImagePagerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                (Serializable) imgUrl);
        intent.putExtras(bundle);
        intent.putExtra("defaultImg", R.drawable.pic_bg_no);
        intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 1);
        startActivity(intent);

    }

    private void showDialog(View v) {
        List<String> strList = new ArrayList<String>();
        if (circle.getCreator() == Global.getIntUid()) {
            strList.add("编辑圈子");
            strList.add("解散圈子");
        } else {
            strList.add("退出圈子");
        }
        CircleInfoPopwindow pop = new CircleInfoPopwindow(getActivity(),
                v, strList);
        pop.show();
        pop.setOnlistOnclick(new OnlistOnclick() {

            @Override
            public void onclick(int position) {
                switch (position) {
                    case 0:
                        if (circle.getCreator() == Global.getIntUid()) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(),
                                    EditCircleActivity.class);
                            intent.putExtra("circle", (Serializable) circle);
                            intent.putExtra("logoBmp", logoBmp);
                            startActivityForResult(intent, 2);
                            getActivity().overridePendingTransition(
                                    R.anim.in_from_right, R.anim.out_to_left);
                        } else {
                            confirmDialog(
                                    "退出后，您将失去与圈内朋友的互动，不能了解大家的近况，您确定要退出么？", 0);
                        }
                        break;
                    case 1:
                        confirmDialog("您确认不是一时冲动？真的要解散本圈子吗？本操作不可恢复，误操作后果很严重哦。",
                                1);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void confirmDialog(String str, final int id) {
        Dialog dialog = DialogUtil.confirmDialog(getActivity(), str, "确定",
                "取消", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        if (id == 1) {
                            dissolveCircle();
                        } else {
                            quitCircle();
                        }
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    /**
     * 退出圈子
     */
    private void quitCircle() {
        cMember = new CircleMember(cid, 0, Integer.valueOf(Global.getUid()));
        cMember.getMemberState(DBUtils.getDBsa(1));
        dialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
        dialog.show();
        BaseAsyncTask<Void, Void, RetError> task = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = cMember.quit();
                if (ret == RetError.NONE) {
                    Circle c = new Circle(cid);
                    c.setStatus(com.changlianxi.data.AbstractData.Status.DEL);
                    c.write(DBUtils.getDBsa(2));
                    // cMember.write(DBUtils.getDBsa());
                }
                return ret;
            }
        };
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                Utils.showToast("退出成功，如果想再加入，可以请圈中朋友把您拉回来。", Toast.LENGTH_LONG);
                exitSuccess();
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();
    }

    private void promptDialog() {
        Dialog dialog = DialogUtil.promptDialog(getActivity(),
                "圈子中还有其他认证成员存在，您目前还不能解散本圈子", "确定", new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    /**
     * 解散圈子
     */
    private void dissolveCircle() {
        if (circle.getVerifiedCnt() > 1) {
            promptDialog();
            return;
        }
        dialog = DialogUtil.getWaitDialog(getActivity(), "请稍后");
        dialog.show();
        BaseAsyncTask<Void, Void, RetError> task = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = circle.dissolve();
                if (ret == RetError.NONE) {
                    circle.write(DBUtils.getDBsa(2));
                }
                return ret;
            }
        };
        task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                Utils.showToast("解散成功", Toast.LENGTH_SHORT);
                exitSuccess();
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();
    }

    private void setCount(String total, String veriofied) {
        String strtotal = "<font color=\"#fd7a00\">" + total + "</font>人";
        String strunverified = " （<font color=\"#fd7a00\">" + veriofied
                + "</font>认证成员）";
        circleMemberCount.setText(Html.fromHtml(strtotal + strunverified));
    }

    private void exitSuccess() {
        Intent acIntent = new Intent();
        acIntent.setAction(Constants.EXIT_CIRCLE);
        acIntent.putExtra("cid", cid);
        BroadCast.sendBroadCast(getActivity(), acIntent);
        getActivity().finish();
        Utils.rightOut(getActivity());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                BroadCast.sendBroadCast(getActivity(), Constants.CHANGE_TAB);
                break;
            case R.id.rightImg:
                showDialog(v);
                break;
            case R.id.circleLogo:
                intentShowBigImg();
                break;
            default:
                break;
        }
    }

    /**
    * 注册该广播
    */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.EDIT_CIRCLE_INFO);

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
            if (action.equals(Constants.EDIT_CIRCLE_INFO)) {
                int cid = intent.getIntExtra("cid", 0);
                circle = new Circle(cid);
                circle.read(DBUtils.getDBsa(1));
                circleDescription.setText(circle.getDescription());
                circleName.setText(circle.getName());
                titleName.setText(circle.getName());
                Bitmap bmp = intent.getExtras().getParcelable("logoBmp");
                if (bmp != null) {
                    circleLogo.setImageBitmap(bmp);
                    logoBmp = bmp;
                }
                Intent it = new Intent(Constants.UPDECIRNAME);
                it.putExtra("circleName", circle.getName());
                BroadCast.sendBroadCast(getActivity(), it);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onLoadingCancelled(String arg0, View arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
        circleLogo.setImageBitmap(logoBmp);
        logoBmp = bmp;
    }

    @Override
    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadingStarted(String arg0, View arg1) {
        // TODO Auto-generated method stub

    }
}