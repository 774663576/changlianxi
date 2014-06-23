package com.changlianxi.task;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.MD5;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;

public class RegisterTask extends AsyncTask<String, Integer, String> {
    private String cellPhone = "";
    private String email = "";
    private Context mContext;
    private RegisterFinish callBack;

    public RegisterTask(Context context, String cellPhone, String email) {
        this.cellPhone = cellPhone;
        this.email = email;
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cellphone", cellPhone);
        map.put("email", email);
        map.put("version", Utils.getVersionName(mContext));
        String tag = "".equals(cellPhone) ? cellPhone : email;
        map.put("tag",
                MD5.MD5_32(StringUtils.reverseSort(tag)
                        + Utils.getVersionName(mContext) + tag
                        + Utils.getVersionName(mContext)));
        String result = HttpUrlHelper.postData(map, "/users/iregister");
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callBack.registerFinish(result);

    }

    public void setCallBack(RegisterFinish callBack) {
        this.callBack = callBack;
    }

    public interface RegisterFinish {
        void registerFinish(String result);
    }
}
