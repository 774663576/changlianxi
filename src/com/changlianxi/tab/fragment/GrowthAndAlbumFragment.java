package com.changlianxi.tab.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.ReleaseGrowthActivity;
import com.changlianxi.chooseImage.PhotoInfo;
import com.changlianxi.chooseImage.SelectPhotoActivity;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.db.DBUtils;
import com.changlianxi.popwindow.GrowthFragmentSelectPicPopwindow;
import com.changlianxi.popwindow.GrowthFragmentSelectPicPopwindow.SelectPic;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.Utils;

public class GrowthAndAlbumFragment extends Fragment implements OnClickListener {
    private ImageView btnRelease;// 发布成长按钮
    private ImageView btback;
    private Button btnGrowth;
    private Button btnAlum;
    private LinearLayout tabLayout;
    private FragmentTransaction fraTra = null;
    private GrowthFragMent gf = null;
    private AlumYearFragMent af = null;
    private int cid;
    private FragmentManager manager;
    private Bundle bundle;
    private String releaseImgPath = "";
    private int newGrowthCount = 0;
    private int newCommentCount = 0;
    private boolean isAuth = true;
    private View rootView;// 缓存Fragment view
    private boolean isOnCreate = false;
    private GrowthFragmentSelectPicPopwindow pop;
    private int index = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        cid = getArguments().getInt("cid", 0);
        newCommentCount = getArguments().getInt("newCommentCount", 0);
        newGrowthCount = getArguments().getInt("newGrowthCount", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.growth_album_fragment, null);
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
        btnRelease = (ImageView) getView().findViewById(R.id.rightImg);
        btback = (ImageView) getView().findViewById(R.id.back);
        btnGrowth = (Button) getView().findViewById(R.id.btnGrwth);
        btnAlum = (Button) getView().findViewById(R.id.btnAlbum);
        tabLayout = (LinearLayout) getView().findViewById(R.id.tabBg);
        CircleMember c = new CircleMember(cid, 0, Global.getIntUid());
        if (!c.isAuth(DBUtils.getDBsa(1))) {
            btnRelease.setVisibility(View.INVISIBLE);
            isAuth = false;
        }
        setListener();
    }

    private void setListener() {
        btnRelease.setOnClickListener(this);
        btback.setOnClickListener(this);
        btnGrowth.setOnClickListener(this);
        btnAlum.setOnClickListener(this);
        setValue();
    }

    private void setValue() {
        manager = getChildFragmentManager();
        fraTra = manager.beginTransaction();
        gf = new GrowthFragMent();
        bundle = new Bundle();
        bundle.putInt("cid", cid);
        bundle.putInt("newGrowthCount", newGrowthCount);
        bundle.putInt("newCommentCount", newCommentCount);
        bundle.putBoolean("isAuth", isAuth);
        gf.setArguments(bundle);
        fraTra.replace(R.id.main_layout, gf);
        fraTra.commit();
        registerBoradcastReceiver();
    }

    @Override
    public void onClick(View v) {
        fraTra = getChildFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.btnGrwth:
                tabLayout.setBackgroundResource(R.drawable.tab_bg1_growth);
                btnAlum.setTextColor(Color.WHITE);
                btnGrowth.setTextColor(R.color.blue);
                gf.onResume();
                fraTra.show(gf);
                if (af != null) {
                    fraTra.hide(af);
                }
                fraTra.commitAllowingStateLoss();
                index = 0;
                break;
            case R.id.btnAlbum:
                if (!isAuth) {
                    Utils.showToast("亲，成为认证成员后才能查看相册", Toast.LENGTH_SHORT);
                    return;

                }
                tabLayout.setBackgroundResource(R.drawable.tab_bg_growth);
                btnGrowth.setTextColor(Color.WHITE);
                btnAlum.setTextColor(R.color.blue);
                if (af == null) {
                    af = new AlumYearFragMent();
                    af.setArguments(bundle);
                    fraTra.add(R.id.main_layout, af);
                } else {
                    af.onResume();
                }
                fraTra.show(af);

                if (gf != null) {
                    fraTra.hide(gf);
                }
                fraTra.commitAllowingStateLoss();
                index = 1;
                break;
            case R.id.back:
                BroadCast.sendBroadCast(getActivity(), Constants.CHANGE_TAB);
                break;
            case R.id.rightImg:
                selectPic(v);
                break;
            default:
                break;
        }

    }

    private void selectPic(View v) {
        pop = new GrowthFragmentSelectPicPopwindow(getActivity(), v, cid, 0);
        pop.setCallBack(new SelectPic() {

            @Override
            public void selectBySdcard() {
                Intent intent = new Intent();
                intent.putExtra("count", 0);
                intent.setClass(getActivity(), SelectPhotoActivity.class);
                startActivityForResult(intent,
                        Constants.REQUEST_CODE_GETIMAGE_BYSDCARD_MORE);
            }

            @Override
            public void selectByCamera() {
                String name = FileUtils.getFileName() + ".jpg";
                releaseImgPath = FileUtils.getCameraPath() + File.separator
                        + name;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(releaseImgPath)));
                startActivityForResult(intent,
                        Constants.REQUEST_CODE_GETIMAGE_BYCAMERA);
            }
        });
        pop.show();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<PhotoInfo> list = new ArrayList<PhotoInfo>();
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if ("".equals(releaseImgPath)) {
                return;
            }
            File fle = new File(releaseImgPath);
            if (!fle.exists()) {
                return;
            }
            PhotoInfo m = new PhotoInfo();
            m.setPath_absolute(releaseImgPath);
            list.add(m);
            intentReleaseGrowthActivity(list);
        } else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD_MORE) {
            if (data == null) {
                return;
            }
            list = (List<PhotoInfo>) data.getExtras()
                    .getSerializable("imgPath");
            intentReleaseGrowthActivity(list);

        }

    }

    private void intentReleaseGrowthActivity(List<PhotoInfo> list) {
        Intent it = new Intent();
        it.setClass(getActivity(), ReleaseGrowthActivity.class);
        it.putExtra("cid", cid);
        Bundle bundle = new Bundle();
        bundle.putSerializable("imgPath", (Serializable) list);
        it.putExtras(bundle);
        startActivity(it);
        getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    /**
     * 注册该广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter
                .addAction(Constants.CHANGE_GROWTHFRAGMENTANDALBUMFRAGMENT);

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
            if (action.equals(Constants.CHANGE_GROWTHFRAGMENTANDALBUMFRAGMENT)) {
                if (index == 1) {
                    fraTra = getChildFragmentManager().beginTransaction();
                    tabLayout.setBackgroundResource(R.drawable.tab_bg1_growth);
                    btnAlum.setTextColor(Color.WHITE);
                    btnGrowth.setTextColor(R.color.blue);
                    fraTra.show(gf);
                    fraTra.hide(af);
                    fraTra.commitAllowingStateLoss();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver.isOrderedBroadcast()) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
}