package com.changlianxi.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.changlianxi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoaderUtil {
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.friends_sends_pictures_no)
            .showImageForEmptyUri(R.drawable.friends_sends_pictures_no)
            .showImageOnFail(R.drawable.friends_sends_pictures_no)
            .cacheInMemory(true).cacheOnDisc(true).build();

    public static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
