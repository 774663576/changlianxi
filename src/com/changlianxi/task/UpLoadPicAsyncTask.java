package com.changlianxi.task;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.util.HttpUrlHelper;

public class UpLoadPicAsyncTask extends AsyncTask<String, Integer, JSONObject> {
    private UpLoadPic upload;// 上传图片完成接口
    private Map<String, Object> map;
    private String url;
    private String picPath = "";
    private String avatar;

    public UpLoadPicAsyncTask(Map<String, Object> map, String url,
            String picPath, String avatar) {
        this.map = map;
        this.url = url;
        this.picPath = picPath;
        this.avatar = avatar;
    }

    public void setCallBack(UpLoadPic upload) {
        this.upload = upload;
    }

    // 可变长的输入参数，与AsyncTask.exucute()对应
    @Override
    protected void onPreExecute() {
        // 任务启动，可以在这里显示一个对话框，这里简单处理
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        File file = new File(picPath);
        String result = HttpUrlHelper.upLoadPic(HttpUrlHelper.strUrl + url,
                map, file, avatar);
        JSONObject jsonobject = null;
        try {
            jsonobject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonobject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        upload.picUpLoadFinish(result);
    }

    public interface UpLoadPic {
        void picUpLoadFinish(JSONObject jsonobject);
    }
}
