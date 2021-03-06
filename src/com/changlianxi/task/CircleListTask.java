package com.changlianxi.task;

import com.changlianxi.data.CircleList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.SharedUtils;

public class CircleListTask extends BaseAsyncTask<CircleList, Void, RetError> {

    private CircleList circleList = null; // TODO
    private boolean refushNet = false;// 从网络刷新数据
    private boolean refushNotify = false;// 刷新圈子提示信息
    private boolean readDB = true;

    public CircleListTask(boolean readDB, boolean refushNet,
            boolean refushNotify) {
        this.readDB = readDB;
        this.refushNet = refushNet;
        this.refushNotify = refushNotify;
    }

    // 可变长的输入参数，与AsyncTask.exucute()对应
    @Override
    protected RetError doInBackground(CircleList... params) {
        if (isCancelled() || params == null) {
            return null;
        }
        circleList = params[0];
        if (readDB) {
            circleList.read(DBUtils.getDBsa(1));
            callBack.readDBFinish();
        }
        if (!refushNet) {
            return RetError.NONE;
        }
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        RetError retError = circleList.refresh(circleList.getLastReqTime());
        if (SharedUtils.getInt("loginType", 0) == 1
                && circleList.getCircles().size() == 1) {
            circleList.initCircles();
            circleList.setStatus(com.changlianxi.data.AbstractData.Status.NEW);
            SharedUtils.setInt("loginType", 2);
        }
        if (refushNotify) {
            circleList.getCirclesNotify();
        }
        if (retError == RetError.NONE) {
            circleList.write(DBUtils.getDBsa(2));
        }
        return retError;
    }

    @Override
    protected void onPostExecute(RetError result) {
        super.onPostExecute(result);
    }

}
