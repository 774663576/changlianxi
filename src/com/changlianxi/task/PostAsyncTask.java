package com.changlianxi.task;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;

import com.changlianxi.util.HttpUrlHelper;

/**
 * 请求网络数据异步线程类
 * 
 * @author teeker_bin
 * 
 */
public class PostAsyncTask extends AsyncTask<String, Integer, String> {
    private PostCallBack callBack;
    private Map<String, Object> map;
    private String url;

    public PostAsyncTask(Context context, Map<String, Object> map, String url) {
        this.map = map;
        this.url = url;
    }

    public void setTaskCallBack(PostCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected String doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }

        String result = HttpUrlHelper.postData(map, url);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // 任务结束
        callBack.taskFinish(result);
    }

    @Override
    protected void onPreExecute() {
        // 任务启动，可以在这里显示一个对话框，这里简单处理

    }

    /**
     * 回调
     * 
     */
    public interface PostCallBack {
        public void taskFinish(String result);

    }

}
