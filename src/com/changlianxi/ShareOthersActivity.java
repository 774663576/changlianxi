package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.showBigPic.ImagePagerActivity;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.changlianxi.view.GrowthImgGridView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 分享到其他圈子
 * @author LG
 *
 */
public class ShareOthersActivity extends BaseActivity implements PostCallBack,
        OnClickListener, ImageLoadingListener {
    private List<Circle> circles = new ArrayList<Circle>();
    private CircleList circleList = null;
    private String content = "";
    private TextView tv_get;
    private ImageView iv_get;
    private int from;
    private int gid;
    private ImageView iv_back;
    private String PATH = "/growth/ishare";
    private String gimg;
    private GrowthImgGridView gridview;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_others);
        from = getIntent().getExtras().getInt("from");
        gid = getIntent().getExtras().getInt("gid");
        gridview = (GrowthImgGridView) findViewById(R.id.gridview);
        gridview.setCacheColorHint(0);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);   
        tv_get = (TextView) findViewById(R.id.text_word);
        iv_get = (ImageView) findViewById(R.id.img);
        iv_get.setOnClickListener(this);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("分享");
        circleList = new CircleList(circles);
        circleList.read(DBUtils.getDBsa(1));
        gimg = getIntent().getStringExtra("imgPath");
        for (int i = circles.size() - 1; i >= 0; i--) {
            circles.get(i).setNewMyDetailEditCnt(0);
            circles.get(i).setNewGrowthCnt(0);
            circles.get(i).setNewMemberCnt(0);
            circles.get(i).setNewGrowthCommentCnt(0);
            circles.get(i).setNewDynamicCnt(0);
            if (circles.get(i).isNew() || from == circles.get(i).getId()
                    || circles.get(i).getId() == 0) {
                circles.remove(i);
            }
        }
        CircleAdapter saImageItems = new CircleAdapter(this, circles);
        // 添加并且显示
        gridview.setAdapter(saImageItems);
        content = getIntent().getExtras().getString("content");

        UniversalImageLoadTool.disPlayListener(gimg, new RotateImageViewAware(
                iv_get, gimg), R.drawable.empty_photo, this);
        tv_get.setText(content);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    final int position, long arg3) {
                Dialog dialog = DialogUtil.confirmDialog(
                        ShareOthersActivity.this, "确认要分享到这个圈子么？", "确定", "取消",
                        new ConfirmDialog() {
                            @Override
                            public void onOKClick() {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("uid", SharedUtils.getString("uid", ""));
                                map.put("token",
                                        SharedUtils.getString("token", ""));
                                map.put("gid", gid); // 待分享id
                                map.put("from", from); // 源圈子id
                                map.put("to", circles.get(position).getId()); // 目标圈子id
                                PostAsyncTask task = new PostAsyncTask(
                                        ShareOthersActivity.this, map, PATH);
                                task.setTaskCallBack(ShareOthersActivity.this);
                                task.execute();
                            }

                            @Override
                            public void onCancleClick() {

                            }
                        });
                dialog.show();

            }
        });
    }

    /**
     * 设置页面统计
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    public void taskFinish(String result) {
        JSONObject object;
        try {
            object = new JSONObject(result);
            int rt = object.getInt("rt");
            if (rt == 1) {
                Utils.showToast("成功分享:)", Toast.LENGTH_SHORT);
            } else {
                Utils.showToast("啊哦，没分享出去，再试一次！", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;
            case R.id.img:
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(gimg);
                Intent intent = new Intent(this, ImagePagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                        (Serializable) imgUrl);
                intent.putExtras(bundle);
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
        iv_get.setImageBitmap(mBitmap);

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
