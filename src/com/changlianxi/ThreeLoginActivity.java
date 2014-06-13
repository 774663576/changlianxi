package com.changlianxi;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.task.UpLoadPicAsyncTask.UpLoadPic;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三方登录
 * 
 * @author LG
 * 
 */
public class ThreeLoginActivity extends BaseActivity implements Callback,
        OnClickListener, PlatformActionListener, PostCallBack {
    private final String PATH = "/users/ibindLogin2";
    private final String PATH2 = "/users/ibindRegister2";
    private String userName = ""; // 用户名
    private String uid = ""; // 用户uid
    private String sex = ""; // 用户性别
    private String headIcon; // 用户头像
    private String birth; // 用户生日
    private String doctorName; // 博士
    private String masterName; // 硕士
    private String collgeName; // 大学
    private String highName; // 高中
    private String juniorName; // 初中
    private String pramaryName; // 小学
    private String technicalName; // 中专
    private ImageView iv_back;
    private int loginType = 0;// 1 人人登录 2 QQ登录 3 新浪微博登录
    private JSONArray jsonAry = new JSONArray();
    private JSONObject jsonObj;
    private Bitmap mBitmap;
    private String mFileName;
    private Dialog progressDialog;
    private String tagmd5 = "";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    sinaLogin(tagmd5);
                    break;
                case 1:
                    renrenLogin(tagmd5);
                    break;
                case 2:
                    QQLogin(tagmd5);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu);
        ShareSDK.initSDK(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
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
    public void onComplete(Platform plat, int action,
            HashMap<String, Object> res) {
        System.out.println("resutl::::::::::::::::==");

        if (loginType == 1) {
            loginForRenRen(res);
        } else if (loginType == 3) {
            loginForSina(res);
        } else {
            loginForTencent(res); // 通过腾讯账号登录
        }
    }

    public void onError(Platform arg0, int arg1, Throwable arg2) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                loginType = 1;
                authorize(new Renren(this));
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                break;
            case R.id.button2:
                loginType = 2;
                authorize(new QZone(this));
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                break;
            case R.id.button3:
                loginType = 3;
                authorize(new SinaWeibo(this));
                progressDialog = DialogUtil.getWaitDialog(this, "请稍候");
                progressDialog.show();
                break;
            case R.id.back:
                finish();
                this.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;
            default:
                break;
        }
    }

    private void authorize(Platform plat) {
        if (plat.getName() == QZone.NAME && plat.isValid()) {
            plat.removeAccount();
        }
        if (plat.getName() == Renren.NAME && plat.isValid()) {
            plat.removeAccount();
        }
        if (plat.getName() == SinaWeibo.NAME && plat.isValid()) {
            plat.removeAccount();
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;
    }

    @Override
    public void taskFinish(String result) {

    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void regesterTask(Map<String, Object> map, String path) {
        PostAsyncTask task = new PostAsyncTask(ThreeLoginActivity.this, map,
                path);
        task.setTaskCallBack(new PostCallBack() {
            @Override
            public void taskFinish(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    intentVerifyActivity(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        task.execute();
    }

    /**
    * 腾讯账号登录
    * @param res
    */
    private void loginForTencent(HashMap<String, Object> res) {
        String gender = res.get("gender").toString();
        if (gender != null && gender.equals("男")) {
            sex = "1";
        } else if (gender != null && gender.equals("女")) {
            sex = "2";
        } else {
            sex = "3";
        }
        headIcon = res.get("figureurl_qq_1").toString().replace(" ", "");

        uid = res.get("figureurl_1").toString();
        uid = uid.substring(0, uid.lastIndexOf("/"));
        uid = uid.substring(uid.lastIndexOf("/") + 1);
        userName = res.get("nickname").toString();
        tagmd5 = MD5.MD5_32(StringUtils
                .reverseSort("722ee3054f64a1fdf789cf96f2c46633")
                + uid
                + "722ee3054f64a1fdf789cf96f2c46633" + uid);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("platform_id", "2");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tagmd5);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, PATH);
        task.setTaskCallBack(new PostCallBack() {
            @Override
            public void taskFinish(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int rt = object.getInt("rt");
                    if (rt == 1) {
                        loginSuccess(object);
                    } else {
                        String rString = object.getString("err");
                        if (rString.equals("NOT_EXIST_USER")) {
                            getFileName();
                        } else {
                            Utils.showToast("啊哦，第三方登陆没有成功，请查看下您的网络是否正常！",
                                    Toast.LENGTH_SHORT);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute();
    }

    private void QQLogin(String tag) {
        Map<String, Object> map = new HashMap<String, Object>();
        buildJson("D_GENDAR", sex);
        buildJson("D_AVATAR", headIcon);
        buildJson("D_NAME", userName);
        map.put("platform_id", "2");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tag);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        map.put("detail", jsonAry.toString());
        if (mFileName != null) {
            upLoadAvatarIcon(map, PATH2, mFileName, "avatar");
        } else {
            regesterTask(map, PATH2);
        }

    }

    private void renrenLogin(String tag) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("platform_id", "3");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tag);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        map.put("detail", jsonAry.toString());
        if (mFileName != null) {
            upLoadAvatarIcon(map, PATH2, mFileName, "avatar");
        } else {
            regesterTask(map, PATH2);
        }

    }

    private void sinaLogin(String tag) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("platform_id", "1");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tag);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        map.put("detail", jsonAry.toString());
        if (mFileName != null) {
            upLoadAvatarIcon(map, PATH2, mFileName, "avatar");
        } else {
            regesterTask(map, PATH2);
        }

    }

    private void upLoadAvatarIcon(Map<String, Object> map, String url,
            String picPath, String avatar) {
        UpLoadPicAsyncTask task = new UpLoadPicAsyncTask(map, url, picPath,
                avatar);
        task.setCallBack(new UpLoadPic() {

            @Override
            public void picUpLoadFinish(JSONObject jsonobject) {
                intentVerifyActivity(jsonobject);

            }
        });
        task.execute();
    }

    /**
    * 新浪微博登录
    * @param res
    */
    private void loginForSina(HashMap<String, Object> res) {
        String gender = res.get("gender").toString();
        if (gender != null && gender.equals("m")) {
            sex = "1";
        } else if (gender != null && gender.equals("f")) {
            sex = "2";
        } else {
            sex = "3";
        }
        headIcon = res.get("profile_image_url").toString().replace(" ", "");
        userName = res.get("screen_name").toString();
        uid = res.get("id").toString();
        tagmd5 = MD5.MD5_32(StringUtils.reverseSort("3270091145") + uid
                + "3270091145" + uid);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("platform_id", "1");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tagmd5);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, PATH);
        task.setTaskCallBack(new PostCallBack() {

            @Override
            public void taskFinish(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int rt = object.getInt("rt");
                    if (rt == 1) {
                        loginSuccess(object);
                    } else {
                        String rString = object.getString("err");
                        if (rString.equals("NOT_EXIST_USER")) {
                            getFileName();
                            buildJson("D_GENDAR", sex);
                            buildJson("D_AVATAR", headIcon);
                            buildJson("D_NAME", userName);
                        } else {
                            Utils.showToast("啊哦，第三方登陆没有成功，请查看下您的网络是否正常！",
                                    Toast.LENGTH_SHORT);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        task.execute();
    }

    /**
    * 人人登录
    * @param res
    */
    @SuppressWarnings("unchecked")
    private void loginForRenRen(HashMap<String, Object> res) {
        Map<String, String> basicString = (Map<String, String>) res
                .get("basicInformation");
        if (basicString.get("birthday") != null) {
            birth = basicString.get("birthday");
        }
        String sexStr = basicString.get("sex");
        if (sexStr != null) {
            if (sexStr.equals("MALE")) {
                sex = "1";
            } else if (sexStr.equals("FEMALE")) {
                sex = "2";
            }
        } else {
            sex = "3";
        }

        final List education = (List) res.get("education");
        userName = res.get("name").toString();
        uid = res.get("id").toString();
        List avatar1 = (List) res.get("avatar");
        Map<String, String> map4 = (Map<String, String>) avatar1.get(0);
        headIcon = map4.get("url");
        tagmd5 = MD5.MD5_32(StringUtils
                .reverseSort("c4a62f2aa83649f386becd20c3ec4065")
                + uid
                + "c4a62f2aa83649f386becd20c3ec4065" + uid);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("platform_id", "3");
        map.put("platform_uid", uid);
        map.put("platform_username", userName);
        map.put("version", Utils.getVersionName(this));
        map.put("tag", tagmd5);
        map.put("device", Utils.getModelAndRelease());
        map.put("os", Utils.getOS());
        PostAsyncTask task = new PostAsyncTask(this, map, PATH);
        task.setTaskCallBack(new PostCallBack() {
            @Override
            public void taskFinish(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int rt = object.getInt("rt");
                    if (rt == 1) {
                        loginSuccess(object);
                    } else {
                        String rString = object.getString("err");
                        if (rString.equals("NOT_EXIST_USER")) {
                            getFileName();
                            buildJson("D_GENDAR", sex);
                            buildJson("D_AVATAR", headIcon);
                            buildJson("D_NAME", userName);
                            buildJson("D_BIRTHDAY", birth);
                            for (int i = 0; i < education.size(); i++) {
                                Map<String, String> map3 = (Map<String, String>) education
                                        .get(i);
                                String edu1 = map3.get("educationBackground");
                                if (edu1.equals("DOCTOR")
                                        && map3.get("name") != null) {
                                    doctorName = map3.get("name");
                                    buildJson("D_PHD_COLLEGE", doctorName);
                                } else if (edu1.equals("MASTER")
                                        && map3.get("name") != null) {
                                    masterName = map3.get("name");
                                    buildJson("D_MASTER_COLLEGE", masterName);
                                } else if (edu1.equals("COLLEGE")
                                        && map3.get("name") != null) {
                                    collgeName = map3.get("name");
                                    buildJson("D_COLLEGE", collgeName);
                                } else if (edu1.equals("HIGHSCHOOL")
                                        && map3.get("name") != null) {
                                    highName = map3.get("name");
                                    buildJson("D_SENIOR_SCHOOL", highName);
                                } else if (edu1.equals("JUNIOR")
                                        && map3.get("name") != null) {
                                    juniorName = map3.get("name");
                                    buildJson("D_JUNIOR_SCHOOL", juniorName);
                                } else if (edu1.equals("PRIMARY")
                                        && map3.get("name") != null) {
                                    pramaryName = map3.get("name");
                                    buildJson("D_GRADE_SCHOOL", pramaryName);
                                } else if (edu1.equals("TECHNICAL")
                                        && map3.get("name") != null) {
                                    technicalName = map3.get("name");
                                    buildJson("D_TECHNICAL_SCHOOL",
                                            technicalName);
                                }
                            }

                        } else {
                            Utils.showToast("啊哦，第三方登陆没有成功，请查看下您的网络是否正常！",
                                    Toast.LENGTH_SHORT);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        task.execute();
    }

    private void buildJson(String key, String value) {
        try {
            jsonObj = new JSONObject();
            jsonObj.put("t", key);
            jsonObj.put("v", value);
            jsonAry.put(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginSuccess(JSONObject object) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String token = "";
        String uid = "";
        try {
            uid = object.getString("uid");
            token = object.getString("token");
            SharedUtils.setString("uid", uid);
            SharedUtils.setString("token", token);
            Intent it = new Intent();
            it.setClass(ThreeLoginActivity.this, MainActivity.class);
            startActivity(it);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void intentVerifyActivity(JSONObject object) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        try {
            int rt = object.getInt("rt");
            if (rt != 1) {
                return;
            }

            String token = "";
            String uid = "";
            uid = object.getString("uid");
            token = object.getString("token");
            SharedUtils.setString("uid", uid);
            SharedUtils.setString("token", token);
            Intent it = new Intent();
            it.putExtra("type", "firstbind");
            it.setClass(ThreeLoginActivity.this, VerifyActivity.class);
            startActivity(it);
            SharedUtils.setInt("loginType", 1);// 登录方式标记 1 注册登录 2 正常登录

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFileName() {
        new Thread(connectNet).start();
    }

    private Runnable connectNet = new Runnable() {
        @Override
        public void run() {
            File dirFile = new File(FileUtils.getCLXDir() + "avatar_icon/");
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            try {
                mFileName = FileUtils.getCLXDir() + "avatar_icon/"
                        + System.currentTimeMillis() + ".jpg";
                mBitmap = BitmapFactory.decodeStream(BitmapUtils
                        .getImageStream(headIcon));
                BitmapUtils.saveFile(mBitmap, mFileName);
                mHandler.sendEmptyMessage(loginType);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

}
