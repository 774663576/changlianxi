package com.changlianxi.tab.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.popwindow.SelectPicPopwindow.CameraPath;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.CircleIdetailTask;
import com.changlianxi.task.UpLoadCircleLogoTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;

public class NomalCircleInfoFragment extends Fragment implements
        OnClickListener, CameraPath {
    private TextView circleName;// 圈子名称
    private TextView titleName;
    private TextView circleDescription;// 圈子描述
    private TextView circleMemberCount;
    private TextView exitCircle;
    private TextView dissolveCircle;
    private ImageView circleLogo;
    private ImageView back;
    private ImageView editCircleLogo;
    private int cid;
    private Circle circle;
    private CircleMember cMember;
    private Dialog dialog;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate;
    private SelectPicPopwindow pop;
    private String selectPicPath = "";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setValue(circle);
                    isCreator();
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
            rootView = inflater.inflate(R.layout.nomal_circle_info_fragment,
                    null);
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
            circle = new Circle(cid);
            initView();

        }
        isOnCreate = true;
    }

    private void initView() {
        dissolveCircle = (TextView) getView()
                .findViewById(R.id.dissolve_circle);
        exitCircle = (TextView) getView().findViewById(R.id.exit_circle);
        titleName = (TextView) getView().findViewById(R.id.titleTxt);
        circleName = (TextView) getView().findViewById(R.id.circleName);
        circleDescription = (TextView) getView().findViewById(R.id.circleDis);
        circleLogo = (ImageView) getView().findViewById(R.id.circleLogo);
        back = (ImageView) getView().findViewById(R.id.back);
        circleMemberCount = (TextView) getView().findViewById(
                R.id.circleMemberCount);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        circleLogo.setOnClickListener(this);
        exitCircle.setOnClickListener(this);
        dissolveCircle.setOnClickListener(this);
        getServerData();
    }

    private void isCreator() {
        if (circle.getCreator() != Global.getIntUid()) {
            exitCircle.setVisibility(View.VISIBLE);
        } else {
            editCircleLogo = (ImageView) getView().findViewById(
                    R.id.edit_circle_logo);
            editCircleLogo.setOnClickListener(this);
            editCircleLogo.setVisibility(View.VISIBLE);
            dissolveCircle.setVisibility(View.VISIBLE);
            Drawable dra = getResources().getDrawable(
                    R.drawable.circle_info_right_angle);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
            circleName.setCompoundDrawables(null, null, dra, null);
            circleDescription.setCompoundDrawables(null, null, dra, null);
        }
    }

    private void setValue(Circle circle) {
        setvalue(circle.getName(), circle.getLogo(), circle.getDescription(),
                circle.getTotalCnt() + "", circle.getVerifiedCnt() + "");
    }

    private void setvalue(String name, String cirIcon, String des,
            String total, String ver) {
        circleDescription.setText(des);
        if ("".equals(des)) {
            circleDescription.setVisibility(View.GONE);
        }
        circleName.setText(name);
        titleName.setText(name);
        setCircleImg(cirIcon);
        setCount(total, ver);

    }

    private void setCircleImg(String img) {
        if ("".equals(img)) {
            circleLogo.setImageResource(R.drawable.pic_bg_no);
        } else {
            UniversalImageLoadTool.disPlay(img, circleLogo,
                    R.drawable.pic_bg_no);
        }
    }

    private void getServerData() {
        CircleIdetailTask circleIdetailTask = new CircleIdetailTask(circle);
        circleIdetailTask
                .setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

                    @Override
                    public void taskFinish(RetError result) {
                        if (circle == null) {
                            return;
                        }
                        setValue(circle);
                        isCreator();
                    }

                    @Override
                    public void readDBFinish() {
                        mHandler.sendEmptyMessage(0);
                    }
                });
        circleIdetailTask.executeWithCheckNet();
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

    private void setCount(String total, String veriofied) {
        String strtotal = "<font color=\"#ffae00\">" + total + "</font>人";
        String strunverified = " （<font color=\"#ffae00\">" + veriofied
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
            case R.id.exit_circle:
                confirmDialog("退出后，您将失去与圈内朋友的互动，不能了解大家的近况，您确定要退出么？");
                break;
            case R.id.circleLogo:
                intentShowBigImg();
                break;
            case R.id.edit_circle_logo:
                pop = new SelectPicPopwindow(getActivity(), v);
                pop.show();
                pop.setCallBack(this);
                break;
            default:
                break;
        }
    }

    private void confirmDialog(String str) {
        Dialog dialog = DialogUtil.confirmDialog(getActivity(), str, "确定",
                "取消", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        quitCircle();

                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
                && data != null) {
            selectPicPath = BitmapUtils.startPhotoZoom(getActivity(),
                    data.getData());

        }// 拍摄图片
        else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            selectPicPath = BitmapUtils.startPhotoZoom(getActivity(),
                    Uri.fromFile(new File(selectPicPath)));
        } else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
                && data != null) {
            Bundle extras = data.getExtras();
            Bitmap photo = null;
            if (extras != null) {
                photo = extras.getParcelable("data");
            }
            if (photo != null) {
                circleLogo.setImageBitmap(photo);
                File file = new File(selectPicPath);
                if (file.exists()) {
                    upLoadLogo();
                    return;
                }
                String name = FileUtils.getFileName() + ".jpg";
                String fileName = FileUtils.getCameraPath() + File.separator
                        + name;
                BitmapUtils.saveFile(photo, fileName);
                selectPicPath = fileName;
                upLoadLogo();
            }
        }
    }

    private void upLoadLogo() {
        dialog = DialogUtil.getWaitDialog(getActivity(), "上传中");
        dialog.show();
        UpLoadCircleLogoTask task = new UpLoadCircleLogoTask(selectPicPath);
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result == RetError.NONE) {
                    Utils.showToast("上传成功", Toast.LENGTH_SHORT);
                    BroadCast.sendBroadCast(getActivity(),
                            Constants.REFRESH_CIRCLE_LIST);
                }

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet(circle);
    }

    @Override
    public void getCameraPath(String path) {
        selectPicPath = path;
    }
}