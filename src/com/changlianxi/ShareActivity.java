package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.showBigPic.ImagePagerActivity;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.MD5;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三方分享页面
 * @author LG
 *
 */
public class ShareActivity extends BaseActivity implements OnClickListener,
        PlatformActionListener, OnItemClickListener {
    private String content = "";
    private String imgLocalPath = "";
    private int from;
    private int gid;
    private ImageView iv_back;
    private TextView tv_get;// 获取的文字
    private ImageView iv_get;// 获取的图片
    private List<GrowthImage> gimg = new ArrayList<GrowthImage>(); // 获取的成长信息
    private GridView gridView;
    private List<GridModle> lists = new ArrayList<GridModle>();
    private MyAdapter adapter;
    private TextView title;
    private FinalBitmap fb;
    private Bitmap mBitmap;
    private String imgNetPath = "";
    private TextView txtShow;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        setContentView(R.layout.activity_share);
        findViewByID();
        fb = CLXApplication.getFb();
        fb.configLoadingImage(R.drawable.empty_photo);
        content = getIntent().getExtras().getString("content");
        from = getIntent().getExtras().getInt("from");
        gid = getIntent().getExtras().getInt("gid");
        gimg = (List<GrowthImage>) getIntent().getExtras().getSerializable(
                "shareImages");
        if (gimg.size() > 0) {
            if (gimg.size() == 1) {
                imgNetPath = StringUtils.JoinString(gimg.get(0).getImg(),
                        "_500x500");
            } else {
                imgNetPath = StringUtils.JoinString(gimg.get(0).getImg(),
                        "_500x500");
            }
        }
        tv_get.setText(content);
        setImage();
        createBitmapFile();
        initData();
    }

    private void setImage() {
        fb.configDisplayer(new Displayer() {

            @Override
            public void loadFailDisplay(View arg0, Bitmap arg1) {

            }

            @Override
            public void loadCompletedisplay(View arg0, Bitmap bmp,
                    BitmapDisplayConfig arg2) {
                iv_get.setImageBitmap(bmp);
                mBitmap = bmp;
                createBitmapFile();

            }
        });
        fb.display(iv_get, imgNetPath);
        // mBitmap = fb.getBitmapFromDiskCache(imgNetPath,
        // new BitmapDisplayConfig());
        // if (mBitmap != null) {
        // iv_get.setImageBitmap(mBitmap);
        // }
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

    private void createBitmapFile() {
        new Thread() {
            public void run() {
                BitmapUtils.saveFile(mBitmap, MD5.Md5(imgNetPath),
                        FileUtils.getCLXDir() + "share/");
                imgLocalPath = FileUtils.getCLXDir() + "share/"
                        + MD5.Md5(imgNetPath);
            }
        }.start();
    }

    private void findViewByID() {
        gridView = (GridView) findViewById(R.id.gridView1);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
        tv_get = (TextView) findViewById(R.id.text_word);
        iv_get = (ImageView) findViewById(R.id.img);
        iv_get.setOnClickListener(this);
        adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        gridView.setCacheColorHint(0);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(this);
        title = (TextView) findViewById(R.id.titleTxt);
        title.setText("分享");
        txtShow = (TextView) findViewById(R.id.txtShow);
        txtShow.setOnClickListener(this);
    }

    private void initData() {
        GridModle modle = null;
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_add);
        modle.setTitle("其他圈子");
        lists.add(modle);
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_friend);
        modle.setTitle("微信朋友圈");
        lists.add(modle);
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_wx);
        modle.setTitle("微信好友");
        lists.add(modle);
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_qq);
        modle.setTitle("QQ空间");
        lists.add(modle);
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_sina);
        modle.setTitle("新浪微博");
        lists.add(modle);
        modle = new GridModle();
        modle.setImgID(R.drawable.logo_qq_2);
        modle.setTitle("QQ好友");
        lists.add(modle);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.img:
                if (gimg.size() > 1) {
                    initentSelect();
                    return;
                }
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(imgNetPath);
                Intent intent = new Intent(this, ImagePagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                        (Serializable) imgUrl);
                intent.putExtras(bundle);
                intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 1);
                startActivity(intent);
                break;
            case R.id.txtShow:
                if (gimg.size() > 1) {
                    initentSelect();
                }
                break;
        }
    }

    private void initentSelect() {
        Intent intent = new Intent(this, SelectShareImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", (Serializable) gimg);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }

    class GridModle {
        int imgID;
        String title;

        public int getImgID() {
            return imgID;
        }

        public void setImgID(int imgID) {
            this.imgID = imgID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
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
                convertView = LayoutInflater.from(ShareActivity.this).inflate(
                        R.layout.share_gridview_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setImageResource(lists.get(position).getImgID());
            holder.title.setText(lists.get(position).getTitle());
            return convertView;
        }
    }

    class ViewHolder {
        ImageView img;
        TextView title;
    }

    /**
     * 微信朋友圈分享纯文字
     */
    private ShareParams getWechatMomentsShareParamsText(View v) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_TEXT);
        sp.title = "来自常联系的分享";
        sp.text = content;
        return sp;
    }

    /**
     * 微信朋友圈分享大图
     */
    private ShareParams getWechatMomentsShareParams(View v) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.title = "来自常联系的分享";
        sp.text = content;
        sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_IMAGE;
        sp.imagePath = imgLocalPath;
        return sp;
    }

    /**
     * 微信好友分享纯文字
     */
    private ShareParams getWechatShareParamsText(View v) {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_TEXT);
        sp.title = "来自常联系的分享";
        sp.text = content;
        return sp;
    }

    /**
     * 微信好友分享大图
     */
    private ShareParams getWechatShareParams(View v) {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.title = "来自常联系的分享";// 标题
        sp.text = content;// 正文
        sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_IMAGE;
        sp.imagePath = imgLocalPath;
        return sp;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Utils.showToast("成功分享:)", Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Utils.showToast("啊哦，没分享出去，再试一次！", Toast.LENGTH_SHORT);
                    break;
                case 2:
                    break;
                default:
                    break;
            }

        };
    };

    @Override
    public void onCancel(Platform plat, int arg1) {
        mHandler.sendEmptyMessage(2);
    }

    public void onComplete(Platform plat, int arg1, HashMap<String, Object> res) {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        mHandler.sendEmptyMessage(1);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        Platform plat = null;
        ShareParams sp = null;
        switch (position) {
            case 0:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("imgPath", imgNetPath);
                intent.putExtra("content", content);
                intent.putExtra("gid", gid);
                intent.putExtra("from", from);
                intent.putExtras(bundle);
                intent.setClass(this, ShareOthersActivity.class);
                startActivity(intent);
                Utils.leftOutRightIn(this);
                break;
            case 4:
                SinaWeibo.ShareParams sina = new SinaWeibo.ShareParams();
                Platform sinaPlatform = ShareSDK.getPlatform(
                        ShareActivity.this, SinaWeibo.NAME);
                sina.text = content;
                if ("".equals(content)) {
                    sina.text = "常联系";
                } else {
                    sina.text = content;
                }
                if (!"".equals(imgNetPath)) {
                    sina.imagePath = imgLocalPath;
                }
                sinaPlatform.SSOSetting(false);
                sinaPlatform.setPlatformActionListener(this);
                sinaPlatform.share(sina);
                break;
            case 3:
                QZone.ShareParams qqsp = new QZone.ShareParams();
                Platform qqs = ShareSDK.getPlatform(ShareActivity.this,
                        QZone.NAME);
                qqsp.title = "来自常联系的分享";
                qqsp.text = content;
                if ("".equals(content)) {
                    qqsp.text = "常联系";
                } else {
                    qqsp.text = content;
                }
                qqsp.site = "常联系";
                qqsp.siteUrl = "http://www.changlianxi.com/";
                if (!"".equals(imgNetPath)) {
                    qqsp.imageUrl = imgNetPath;
                }
                qqsp.titleUrl = qqs.getShortLintk(
                        "http://www.changlianxi.com/", true);
                qqs.setPlatformActionListener(this); // 设置分享事件回调
                qqs.share(qqsp);
                break;
            case 5:
                QQ.ShareParams qqsParams = new QQ.ShareParams();
                Platform qqPlatform = ShareSDK.getPlatform(ShareActivity.this,
                        QQ.NAME);
                qqsParams.title = "来自常联系的分享";
                qqsParams.titleUrl = qqPlatform.getShortLintk(
                        "http://www.changlianxi.com/", true);
                if ("".equals(content)) {
                    qqsParams.text = "常联系";
                } else {
                    qqsParams.text = content;
                }
                if (!"".equals(imgNetPath)) {
                    qqsParams.imageUrl = imgNetPath;
                }
                qqPlatform.setPlatformActionListener(this); // 设置分享事件回调
                qqPlatform.share(qqsParams);
                break;
            case 1:
                plat = ShareSDK.getPlatform(this, "WechatMoments");
                if (!"".equals(imgNetPath)) {
                    sp = getWechatMomentsShareParams(v);
                } else {
                    sp = getWechatMomentsShareParamsText(v);
                }
                plat.setPlatformActionListener(this);
                plat.share(sp);
                break;
            case 2:
                plat = ShareSDK.getPlatform(this, "Wechat");
                if (!"".equals(imgNetPath)) {
                    sp = getWechatShareParams(v);
                } else {
                    sp = getWechatShareParamsText(v);
                }
                plat.setPlatformActionListener(this);
                plat.share(sp);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2 || data == null) {
            return;
        }
        imgNetPath = StringUtils.JoinString(data.getStringExtra("imgs"),
                "_500x500");
        setImage();
        createBitmapFile();
    }
}
