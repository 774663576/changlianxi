package com.changlianxi.showBigPic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.Utils;
import com.changlianxi.view.photoview.PhotoView;
import com.changlianxi.view.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 打图预览界面
 * 
 * @author teeker_bin
 * 
 */
public class AvatarImageDetailFragment extends Fragment {
    private String mImageUrl;
    private ImageView mImageView;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ProgressBar progressBar;
    // private PhotoViewAttacher mAttacher;
    private OnBack callBack;
    private int defaultImg;
    private PhotoView photoView;

    public static AvatarImageDetailFragment newInstance(String imageUrl,
            int defaultImg) {
        final AvatarImageDetailFragment f = new AvatarImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        args.putInt("defaultImg", defaultImg);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;
        defaultImg = getArguments().getInt("defaultImg", 0);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(CLXApplication
                .getInstance()));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImg)
                .showImageForEmptyUri(defaultImg).showImageOnFail(defaultImg)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment,
                container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        // mAttacher = new PhotoViewAttacher(mImageView);
        // mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
        //
        // @Override
        // public void onPhotoTap(View arg0, float arg1, float arg2) {
        // if (callBack != null) {
        // callBack.onBackClick();
        // } else {
        // getActivity().finish();
        //
        // }
        // }
        // });
        photoView = (PhotoView) v.findViewById(R.id.image);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (callBack != null) {
                    callBack.onBackClick();
                } else {
                    getActivity().finish();

                }
            }
        });
        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ("".equals(mImageUrl)) {
            mImageView.setImageResource(defaultImg);
            return;
        }
        if (!mImageUrl.startsWith("http")) {
            mImageUrl = "file://" + mImageUrl;
        }
        imageLoader.displayImage(mImageUrl, photoView, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                            FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "下载错误";
                                break;
                            case DECODING_ERROR:
                                message = "图片无法显示";
                                break;
                            case NETWORK_DENIED:
                                message = "网络有问题，无法下载";
                                break;
                            case OUT_OF_MEMORY:
                                message = "图片太大无法显示";
                                break;
                            case UNKNOWN:
                                message = "未知的错误";
                                break;
                        }
                        Utils.showToast(message, Toast.LENGTH_SHORT);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                            Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        // mAttacher.update();
                    }
                });

    }

    public void setCallBack(OnBack callBack) {
        this.callBack = callBack;
    }

    public interface OnBack {
        void onBackClick();
    }
}
