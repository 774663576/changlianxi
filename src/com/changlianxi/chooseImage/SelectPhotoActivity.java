package com.changlianxi.chooseImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.chooseImage.PhotoFolderFragment.OnPageLodingClickListener;
import com.changlianxi.chooseImage.PhotoFragment.OnPhotoSelectClickListener;

/**    
 * @title SelectPhotoActivity.java
  * @author teeker_bin
  */
public class SelectPhotoActivity extends FragmentActivity implements
        OnPageLodingClickListener, OnPhotoSelectClickListener, OnClickListener {

    private PhotoFolderFragment photoFolderFragment;
    private Button btnback;
    private Button btnFinish;

    private TextView title;

    private List<PhotoInfo> hasList;

    private FragmentManager manager;
    private int backInt = 0;
    /**
     * 已选择图片数量
     */
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectphoto);
        count = getIntent().getIntExtra("count", 0);
        manager = getSupportFragmentManager();
        hasList = new ArrayList<PhotoInfo>();
        btnback = (Button) findViewById(R.id.btnback);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        btnback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (backInt == 0) {
                    finish();
                } else if (backInt == 1) {
                    backInt--;
                    hasList.clear();
                    title.setText("相册");
                    btnback.setVisibility(View.INVISIBLE);
                    FragmentTransaction transaction = manager
                            .beginTransaction();
                    transaction.show(photoFolderFragment).commit();
                    manager.popBackStack(0, 0);
                }
            }
        });

        btnback.setVisibility(View.INVISIBLE);
        photoFolderFragment = new PhotoFolderFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.body, photoFolderFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
    }

    @Override
    public void onPageLodingClickListener(List<PhotoInfo> list, String albumName) {
        title.setText(albumName);
        btnback.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = manager.beginTransaction();
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle args = new Bundle();
        PhotoSerializable photoSerializable = new PhotoSerializable();
        for (PhotoInfo photoInfoBean : list) {
            photoInfoBean.setChoose(false);
        }
        photoSerializable.setList(list);
        args.putInt("count", count);
        args.putSerializable("list", photoSerializable);
        photoFragment.setArguments(args);
        transaction = manager.beginTransaction();
        transaction.hide(photoFolderFragment).commit();
        transaction = manager.beginTransaction();
        transaction.add(R.id.body, photoFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        backInt++;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 0) {
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 1) {
            backInt--;
            hasList.clear();
            title.setText("相册");
            btnback.setVisibility(View.INVISIBLE);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.show(photoFolderFragment).commit();
            manager.popBackStack(0, 0);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // UniversalImageLoadTool.clear();
        // UniversalImageLoadTool.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinish:
                if (backInt == 0) {
                    finish();
                } else if (backInt == 1) {
                    backInt--;
                    hasList.clear();
                    title.setText("相册");
                    btnback.setVisibility(View.INVISIBLE);
                    FragmentTransaction transaction = manager
                            .beginTransaction();
                    transaction.show(photoFolderFragment).commit();
                    manager.popBackStack(0, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPhotoSelectClickListener(List<PhotoInfo> list) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("imgPath", (Serializable) list);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
