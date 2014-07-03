package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.UserInfoActivity.BoxBlurFilterThread;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class NoExistPersonInfoActivity extends BaseActivity implements
        OnClickListener, ImageLoadingListener {
    private ImageView back;
    private TextView txtName;
    private RelativeLayout layTop;
    private CircularImage avatar;
    private String name = "";
    private String avatarUrl = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_exist_person_info);
        getDataFromePreviousActivity();
        initView();
    }

    private void getDataFromePreviousActivity() {
        name = getIntent().getStringExtra("name");
        avatarUrl = getIntent().getStringExtra("avatar");
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        txtName = (TextView) findViewById(R.id.name);
        avatar = (CircularImage) findViewById(R.id.avatar);
        layTop = (RelativeLayout) findViewById(R.id.top);
        setListener();
        setValue();
    }

    private void setListener() {
        back.setOnClickListener(this);
        avatar.setOnClickListener(this);
    }

    private void setValue() {
        txtName.setText(name);
        setAvatar();
    }

    private void setAvatar() {
        UniversalImageLoadTool.disPlayListener(avatarUrl, avatar,
                R.drawable.head_bg, this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.avatar:
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(StringUtils.revertAliyunOSSImageUrl(avatarUrl));
                Intent intent = new Intent(this, AvatarImagePagerActivity.class);
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
    public void onLoadingCancelled(String arg0, View arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
        if (mBitmap != null) {
            avatar.setImageBitmap(mBitmap);
            setBackGroubdOfDrable(BitmapUtils.BoxBlurFilter(mBitmap));
        } else {
            avatar.setImageResource(R.drawable.head_bg);
        }

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
