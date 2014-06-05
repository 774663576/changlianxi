package com.changlianxi.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

public class UpDateNewVersionTask extends AsyncTask<String, Integer, String> {
    private String version = "";
    private String serverVersion = "";
    private String versionLink = "";
    private UpDateVersion callBack;
    private boolean flag;
    private String rt = "0";
    private String err = "";

    public UpDateNewVersionTask(Context mContext, boolean flag) {
        version = Utils.getVersionName(mContext);
        this.flag = flag;
    }

    @Override
    protected String doInBackground(String... params) {
        if (!Utils.isNetworkAvailable()) {
            return "netError";
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("token", SharedUtils.getString("token", ""));
        String result = HttpUrlHelper.postData(map, "/users/inewVersion");
        if (result == null || "".equals(result)) {
            return "0";
        }
        try {

            JSONObject json = new JSONObject(result);
            rt = json.getString("rt");
            if (!rt.equals("1")) {
                err = json.getString("err");
                return rt;
            }
            serverVersion = json.getString("android");
            versionLink = json.getString("androidLink");
            String inviteTemplate = json.getString("inviteTemplate");
            inviteTemplate = inviteTemplate.replace("[name]", "%1$s")
                    .replace("[circle]", "%2$s").replace("[friends]", "%3$s")
                    .replace("[code]", "%4$s");
            SharedUtils.setString("inviteTemplate", inviteTemplate);
            return rt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rt;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("netError") && flag) {
            Utils.showToast("杯具，网络不通，快检查下。 ", Toast.LENGTH_SHORT);
            this.callBack.getNewVersion(rt, "", "");
            return;
        }
        if (flag && !result.equals("1")) {
            String errorString = ErrorCodeUtil.convertToChines(err);
            Utils.showToast(errorString, Toast.LENGTH_SHORT);
            this.callBack.getNewVersion(rt, "", "");
            return;
        }
        if (version.equals(serverVersion)) {
            if (flag) {
                Utils.showToast("您现在用的已经是最新版，最最新版值得您期待！", Toast.LENGTH_SHORT);
            }
            this.callBack.getNewVersion("0", "", "");
            return;
        }
        this.callBack.getNewVersion(rt, "检测到新版本\n\n" + serverVersion,
                versionLink);
    }

    public UpDateVersion getCallBack() {
        return callBack;
    }

    public void setCallBack(UpDateVersion callBack) {
        this.callBack = callBack;
    }

    public interface UpDateVersion {
        void getNewVersion(String rt, String versionCode, String link);
    }
}
