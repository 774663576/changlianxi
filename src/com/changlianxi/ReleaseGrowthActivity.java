package com.changlianxi;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.chooseImage.PhotoInfo;
import com.changlianxi.chooseImage.RotateImageViewAware;
import com.changlianxi.chooseImage.UniversalImageLoadTool;
import com.changlianxi.data.Global;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.popwindow.SelectPicPopwindow.CameraPath;
import com.changlianxi.showBigPic.ReleaseGrowthImagePagerActivity;
import com.changlianxi.showBigPic.ReleaseGrowthImagePagerActivity.DelPic;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadNewGrowthTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.GrowthImgGridView;
import com.changlianxi.view.RoundAngleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 发布成长界面
 * @author teeker_bin
 *
 */
// key 771eee919e7e1179a1c249b38367c0d3
public class ReleaseGrowthActivity extends Activity implements OnClickListener,
        OnItemClickListener, DelPic, CameraPath {
    private EditText time;
    private EditText location;// 地点输入框
    private EditText content;// 内容输入框
    private int cid;
    private Dialog progressDialog;
    private Button btnUpload;
    private ImageView btnback;
    private GrowthImgGridView gridView;
    private List<PhotoInfo> listBmp = new ArrayList<PhotoInfo>();
    private MyAdapter adapter;
    private SelectPicPopwindow pop;
    private TextView titleTxt;
    private Calendar cal = Calendar.getInstance();
    private Growth growth;
    private List<PhotoInfo> list = new ArrayList<PhotoInfo>(); // 获取图片的地址
    private double latitude = 0;// 维度
    private double longitude = 0;// 经度
    private MKSearch mMKSearch;
    private BMapManager mapManager;
    private GeoPoint point;
    private String happenedTime = "";
    private int currentYear = 0;
    private String cameraPath = "";

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_growth_main);
        CLXApplication.addActivity(this);
        Bundle bundle = getIntent().getExtras();
        list = (List<PhotoInfo>) bundle.getSerializable("imgPath");
        listBmp.add(null);
        cid = getIntent().getExtras().getInt("cid");
        initView();
        setListener();
        initLBS();
        getSelectPicPath();
        currentYear = cal.get(Calendar.YEAR);
    }

    private void getSelectPicPath() {
        for (PhotoInfo m : list) {
            listBmp.add(listBmp.size() - 1, m);
        }
        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            String timestr = list.get(0).getDate();
            if (timestr != null && !"".equals(timestr)) {
                time.setText(DateUtils.timestampConvertToDate(timestr,
                        "yyyy-MM-dd"));
                happenedTime = DateUtils.timestampConvertToDate(timestr,
                        "yyyy-MM-dd HH:mm:ss");
            }
            latitude = list.get(0).getLatitude();
            longitude = list.get(0).getLongitude();
            if (latitude != 0 && longitude != 0) {
                mMKSearch = new MKSearch();
                point = new GeoPoint((int) (latitude * 1E6),
                        (int) (longitude * 1E6));
                mMKSearch.init(mapManager, new MyMKSearchListener());
                mMKSearch.reverseGeocode(point);
            }
            mapManager.start();
        }
    }

    private void initView() {
        time = (EditText) findViewById(R.id.time);
        location = (EditText) findViewById(R.id.location);
        content = (EditText) findViewById(R.id.content);
        time.setText(DateUtils.timestampConvertToDate(
                System.currentTimeMillis() / 1000 + "", "yyyy-MM-dd"));
        happenedTime = DateUtils.getCurrDateStr("yyyy-MM-dd HH:mm:ss");
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnback = (ImageView) findViewById(R.id.back);
        gridView = (GrowthImgGridView) findViewById(R.id.imgGridview);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("发布成长记录");
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        location.setText("正在获取地址...");
    }

    private void setListener() {
        time.setOnClickListener(this);
        btnback.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }

    /**
     * 设置页面统计
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        if (mapManager != null) {
            // 开启百度地图API
            mapManager.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapManager != null) {
            // 程序退出前需调用此方法
            mapManager.destroy();
            mapManager = null;
        }
    }

    private void initLBS() {
        mapManager = new BMapManager(this);
        mapManager.init("EDB67AD764D300895C95ABA02A4DDC58D5485CCD", null);
        mapManager.getLocationManager().requestLocationUpdates(
                new MyLocationListener());
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location arg0) {
            int jindu = (int) (arg0.getLatitude() * 1000000);
            int weidu = (int) (arg0.getLongitude() * 1000000);
            latitude = arg0.getLatitude();
            longitude = arg0.getLongitude();
            if (mMKSearch != null) {
                return;
            }
            mMKSearch = new MKSearch();
            mMKSearch.init(mapManager, new MyMKSearchListener());
            mMKSearch.reverseGeocode(new GeoPoint(jindu, weidu));
        }

    }

    class MyMKSearchListener implements MKSearchListener {
        @Override
        public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
            if (arg0 == null) {
                Utils.showToast("", Toast.LENGTH_SHORT);
                location.setText("获取地址失败");
            } else {
                location.setText(arg0.strAddr.replace("x026", ""));
            }
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {

        }

        @Override
        public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

        }

        @Override
        public void onGetPoiDetailSearchResult(int arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onGetRGCShareUrlResult(String arg0, int arg1) {
            // TODO Auto-generated method stub

        }

    }

    private void confirmDialog() {
        Dialog dialog = DialogUtil.confirmDialog(this, "放弃不发送了？精彩分享不要轻易放弃哦",
                "确定", "取消", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        finish();
                        Utils.rightOut(ReleaseGrowthActivity.this);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload:
                String contentStr = content.getText().toString()
                        .replace(" ", "");
                if (listBmp.size() == 1 && contentStr.length() == 0) {
                    Utils.showToast("图没有，字没有，空气是不能发送的哦", Toast.LENGTH_SHORT);
                    return;
                }
                List<GrowthImage> imgs = new ArrayList<GrowthImage>();
                for (int i = 0; i < listBmp.size() - 1; i++) {
                    PhotoInfo modle = listBmp.get(i);
                    GrowthImage img = new GrowthImage(0, 0, 0,
                            modle.getPath_absolute());
                    imgs.add(img);
                }
                String strLocotion = location.getText().toString();
                growth = new Growth(cid, 0, Global.getIntUid(), contentStr,
                        strLocotion.equals("正在获取地址...") ? "" : strLocotion,
                        happenedTime, DateUtils.timestampConvertToDate(
                                System.currentTimeMillis() / 1000 + "",
                                "yyyy-MM-dd HH:mm:ss"));
                growth.setCoordinate(latitude + "," + longitude);
                growth.setImages(imgs);// To
                growth.setUpLoading(true);
                uploadForAdd(contentStr);
                BroadCast.sendBroadCast(ReleaseGrowthActivity.this,
                        Constants.FINISH_DAY_ALBUM);
                BroadCast.sendBroadCast(ReleaseGrowthActivity.this,
                        Constants.FINISH_MONTH_ALBUM);
                exitSuccess();
                btnUpload.setClickable(false);
                break;
            case R.id.back:
                if (listBmp.size() > 1
                        || !"".equals(content.getText().toString())) {
                    confirmDialog();
                } else {
                    finish();
                    Utils.rightOut(this);
                }
                break;
            case R.id.time:
                showDateDialog();
                break;
            default:
                break;
        }
    }

    // 日期选择对话框的 DateSet 事件监听器
    class DateListener implements OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {

            if (year > currentYear) {
                Utils.showToast("不能选择未来日期", Toast.LENGTH_SHORT);
                return;
            }
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    }

    private void showDateDialog() {
        java.util.Date de = DateUtils.stringToDate(time.getText().toString());
        cal.setTime(de);
        new DatePickerDialog(this, new DateListener(), cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    // 当 DatePickerDialog 关闭，更新日期显示
    private void updateDate() {// yyyy-MM-dd HH:mm:ss
        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String date = df.format(cal.getTime());
        happenedTime = date;
        time.setText(DateUtils.interceptDateStr(date, "yyy-MM-dd"));

    }

    private void uploadForAdd(String content) {
        UpLoadNewGrowthTask task = new UpLoadNewGrowthTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (result != RetError.NONE) {
                    BroadCast.sendBroadCast(ReleaseGrowthActivity.this,
                            Constants.GROWTH_UPLOADING_FAIL);
                    return;
                }
                BroadCast.sendBroadCast(ReleaseGrowthActivity.this,
                        Constants.REFUSH_ALBUM);
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putSerializable("growth", growth);
                intent.putExtras(b);
                intent.setAction(Constants.GROWTH_UPLOADING_SUCCESS);
                BroadCast.sendBroadCast(ReleaseGrowthActivity.this, intent);

            }

            @Override
            public void readDBFinish() {
            }
        });
        task.executeWithCheckNet(growth);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD_MORE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                @SuppressWarnings("unchecked")
                List<PhotoInfo> list = (List<PhotoInfo>) bundle
                        .getSerializable("imgPath");
                for (PhotoInfo m : list) {
                    listBmp.add(listBmp.size() - 1, m);
                }
            }
        }
        // 拍摄图片
        else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (resultCode != RESULT_OK) {
                return;
            }
            File file = new File(cameraPath);
            if (!file.exists()) {
                Utils.showToast("图片获取失败，请重新获取", Toast.LENGTH_SHORT);
                return;
            }
            PhotoInfo modle = new PhotoInfo();
            modle.setPath_absolute(cameraPath);
            listBmp.add(listBmp.size() - 1, modle);
        }
        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listBmp.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ReleaseGrowthActivity.this)
                        .inflate(R.layout.growth_publich_grid_item, null);
                holder.img = (RoundAngleImageView) convertView
                        .findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == listBmp.size() - 1) {
                UniversalImageLoadTool.disPlay("file://" + "",
                        new RotateImageViewAware(holder.img, ""),
                        R.drawable.add_pic);
            } else {
                String path = listBmp.get(position).getPath_absolute();
                UniversalImageLoadTool.disPlay("file://" + path,
                        new RotateImageViewAware(holder.img, path),
                        R.drawable.empty_photo);
            }
            return convertView;
        }
    }

    class ViewHolder {
        RoundAngleImageView img;
    }

    private void exitSuccess() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("growth", growth);
        intent.putExtras(b);
        intent.setAction(Constants.ADD_LOCAL_GROWTH);
        BroadCast.sendBroadCast(this, intent);
        BroadCast.sendBroadCast(ReleaseGrowthActivity.this,
                Constants.CHANGE_GROWTHFRAGMENTANDALBUMFRAGMENT);
        finish();
        Utils.rightOut(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        if (position == listBmp.size() - 1) {
            if (listBmp.size() == 10) {
                Utils.showToast("一次最多允许发布9张图片", Toast.LENGTH_SHORT);
                return;
            }
            Utils.hideSoftInput(this);
            pop = new SelectPicPopwindow(this, arg1, cid, listBmp.size() - 1);
            pop.show();
            pop.setCallBack(this);
            return;
        }
        List<String> imgUrl = new ArrayList<String>();
        for (int i = 0; i < listBmp.size() - 1; i++) {
            imgUrl.add(listBmp.get(i).getPath_absolute());
        }
        Intent intent = new Intent(this, ReleaseGrowthImagePagerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                (Serializable) imgUrl);
        intent.putExtras(bundle);
        intent.putExtra(Constants.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("type", 1);
        startActivity(intent);
        ReleaseGrowthImagePagerActivity.setCallBack(this);
    }

    @Override
    public void del(int position) {
        listBmp.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listBmp.size() > 1 || !"".equals(content.getText().toString())) {
                confirmDialog();
            } else {
                finish();
                Utils.rightOut(this);
            }
        }
        return false;
    }

    @Override
    public void getCameraPath(String path) {
        cameraPath = path;
    }
}
