package com.changlianxi.chooseImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.changlianxi.R;
import com.changlianxi.util.DialogUtil;

/**    
 */
public class PhotoFolderFragment extends Fragment {

    public interface OnPageLodingClickListener {
        public void onPageLodingClickListener(List<PhotoInfo> list,
                String albumName);

    }

    private OnPageLodingClickListener onPageLodingClickListener;
    private ListView listView;
    private ContentResolver cr;
    private List<AlbumInfo> listImageInfo = new ArrayList<AlbumInfo>();
    private PhotoFolderAdapter listAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (onPageLodingClickListener == null) {
            onPageLodingClickListener = (OnPageLodingClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_photofolder, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.listView);

        cr = getActivity().getContentResolver();
        listImageInfo.clear();

        new ImageAsyncTask().execute();

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                onPageLodingClickListener.onPageLodingClickListener(
                        listImageInfo.get(arg2).getList(),
                        listImageInfo.get(arg2).getName_album());
            }
        });
    }

    private class ImageAsyncTask extends AsyncTask<Void, Void, Object> {
        private Dialog dialog;

        @Override
        protected Object doInBackground(Void... params) {

            // 获取缩略图
            ThumbnailsUtil.clear();
            String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,
                    Thumbnails.DATA };
            Cursor cur = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                    null, null, null);

            if (cur != null && cur.moveToFirst()) {
                int image_id;
                String image_path;
                int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
                int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
                do {
                    image_id = cur.getInt(image_idColumn);
                    image_path = cur.getString(dataColumn);
                    ThumbnailsUtil.put(image_id, "file://" + image_path);
                } while (cur.moveToNext());
            }

            // 获取原图
            Cursor cursor = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, "date_modified DESC");

            String _album = "bucket_display_name";

            HashMap<String, AlbumInfo> myhash = new HashMap<String, AlbumInfo>();
            AlbumInfo albumInfo = null;
            PhotoInfo photoInfo = null;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int index = 0;
                    int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    String album = cursor.getString(cursor
                            .getColumnIndex(_album));
                    double latitude = cursor.getDouble(cursor
                            .getColumnIndex(MediaStore.Images.Media.LATITUDE));
                    double longitude = cursor.getDouble(cursor
                            .getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                    String date = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    List<PhotoInfo> stringList = new ArrayList<PhotoInfo>();
                    photoInfo = new PhotoInfo();
                    if (myhash.containsKey(album)) {
                        albumInfo = myhash.remove(album);
                        if (listImageInfo.contains(albumInfo)) index = listImageInfo
                                .indexOf(albumInfo);
                        photoInfo.setImage_id(_id);
                        photoInfo.setPath_file("file://" + path);
                        photoInfo.setPath_absolute(path);
                        photoInfo.setDate(date);
                        photoInfo.setLongitude(longitude);
                        photoInfo.setLatitude(latitude);
                        albumInfo.getList().add(photoInfo);
                        listImageInfo.set(index, albumInfo);
                        myhash.put(album, albumInfo);
                    } else {
                        albumInfo = new AlbumInfo();
                        stringList.clear();
                        photoInfo.setImage_id(_id);
                        photoInfo.setPath_file("file://" + path);
                        photoInfo.setPath_absolute(path);
                        photoInfo.setDate(date);
                        photoInfo.setLongitude(longitude);
                        photoInfo.setLatitude(latitude);
                        stringList.add(photoInfo);
                        albumInfo.setImage_id(_id);
                        albumInfo.setPath_file("file://" + path);
                        albumInfo.setPath_absolute(path);
                        albumInfo.setName_album(album);
                        albumInfo.setList(stringList);
                        listImageInfo.add(albumInfo);
                        myhash.put(album, albumInfo);
                    }
                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getWaitDialog(getActivity(), "请稍候");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (getActivity() != null) {
                listAdapter = new PhotoFolderAdapter(getActivity(),
                        listImageInfo);
                listView.setAdapter(listAdapter);
            }
        }
    }

}
