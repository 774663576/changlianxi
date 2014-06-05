package com.changlianxi.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.Toast;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.util.Utils;

public abstract class BaseAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    public PostCallBack<Result> callBack;
    public boolean isNet = true;
    public boolean isPrompt = true;

    public void setTaskCallBack(PostCallBack<Result> callBack) {
        this.callBack = callBack;
    }

    private void checkNet() {
        if (!Utils.isNetworkAvailable()) {
            isNet = false;
        }
    }

    @SuppressLint("NewApi")
    public void executeWithCheckNet(Params... params) {
        checkNet();
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            super.execute(params);
        } else {
            super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (!isPrompt) {
            if (callBack != null) {
                // 任务结束
                callBack.taskFinish(result);
            }
            return;
        }
        if (result instanceof RetError) {
            if (result == null || result != RetError.NONE) {
                if (result == RetError.UNKOWN
                        || result == RetError.NEED_MORE_PARAMS
                        || result == RetError.PERMISSION_DENIED
                        || result == RetError.INVALID
                        || result == RetError.INVALID_OPERATION) {
                    Utils.showToast("啊哦，很抱歉没有成功，请确认是否是网络的缘故！",
                            Toast.LENGTH_SHORT);

                } else {
                    Utils.showToast(RetError.toText(((RetError) result)),
                            Toast.LENGTH_SHORT);
                }
            }
        }
        if (callBack != null) {
            // 任务结束
            callBack.taskFinish(result);
        }
    }

    /**
     * 回调
     * 
     */
    public interface PostCallBack<Result> {
        public void taskFinish(Result result);// 网络读取

        public void readDBFinish();
    }
}
