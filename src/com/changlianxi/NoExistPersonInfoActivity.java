package com.changlianxi;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
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

import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.FileUtils;
import com.changlianxi.view.CircularImage;

public class NoExistPersonInfoActivity extends BaseActivity implements
        OnClickListener {
    private ImageView back;
    private TextView txtName;
    private FinalBitmap fb;
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
        initFB();
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

    private void initFB() {
        fb = FinalBitmap.create(this);
        fb.configMemoryCacheSize(3 * 1024 * 1024).configBitmapLoadThreadSize(3);
        fb.configDiskCachePath(FileUtils.getRootDir() + File.separator + "CLX"
                + File.separator + "img");
        fb.configLoadingImage(R.drawable.head_bg);
        fb.configLoadfailImage(R.drawable.head_bg);
    }

    private void setAvatar() {
        Bitmap mBitmap = fb.getBitmapFromDiskCache(avatarUrl,
                new BitmapDisplayConfig());
        if (mBitmap != null) {
            avatar.setImageBitmap(mBitmap);
            setBackGroubdOfDrable(BitmapUtils.convertBimapToDrawable(mBitmap));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.avatar:
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(avatarUrl.replace("_160x160", ""));
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
}
