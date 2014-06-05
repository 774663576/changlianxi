package com.changlianxi.chooseImage;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;

/**    
 */
public class PhotoFragment extends Fragment implements OnClickListener {
    public interface OnPhotoSelectClickListener {
        public void onPhotoSelectClickListener(List<PhotoInfo> list);

    }

    private OnPhotoSelectClickListener onPhotoSelectClickListener;

    private List<PhotoInfo> selsectPhoto = new ArrayList<PhotoInfo>();

    private GridView gridView;
    private PhotoAdapter photoAdapter;
    private RelativeLayout btnSave;
    private TextView num;
    private List<PhotoInfo> list;

    private int hasSelect = 1;

    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (onPhotoSelectClickListener == null) {
            onPhotoSelectClickListener = (OnPhotoSelectClickListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_photoselect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridView = (GridView) getView().findViewById(R.id.gridview);
        btnSave = (RelativeLayout) getView().findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        num = (TextView) getView().findViewById(R.id.txt_num_show);
        Bundle args = getArguments();
        PhotoSerializable photoSerializable = (PhotoSerializable) args
                .getSerializable("list");
        count = args.getInt("count", 0);
        list = new ArrayList<PhotoInfo>();
        list.addAll(photoSerializable.getList());
        hasSelect += count;

        photoAdapter = new PhotoAdapter(getActivity(), list, gridView);
        gridView.setAdapter(photoAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (list.get(position).isChoose() && hasSelect > 1) {
                    list.get(position).setChoose(false);
                    hasSelect--;
                } else if (hasSelect < 10) {
                    list.get(position).setChoose(true);
                    hasSelect++;
                } else {
                    Toast.makeText(getActivity(), "一次最多允许发布9张图片",
                            Toast.LENGTH_SHORT).show();
                }
                photoAdapter.refreshView(position);
                selsectPhoto.clear();
                for (PhotoInfo photoInfoBean : list) {
                    if (photoInfoBean.isChoose()) {
                        selsectPhoto.add(photoInfoBean);
                    }
                }
                if (selsectPhoto.size() > 0) {
                    num.setVisibility(View.VISIBLE);
                    num.setText(selsectPhoto.size() + "");
                } else {
                    num.setVisibility(View.GONE);
                }
            }
        });
        gridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    UniversalImageLoadTool.resume();
                } else {
                    UniversalImageLoadTool.pause();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                onPhotoSelectClickListener
                        .onPhotoSelectClickListener(selsectPhoto);
                break;

            default:
                break;
        }
    }
}
