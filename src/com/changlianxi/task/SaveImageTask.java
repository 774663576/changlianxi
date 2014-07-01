package com.changlianxi.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.Utils;

public class SaveImageTask extends AsyncTask<Void, Void, Void> {
    private SaveImge callBack;
    private Bitmap bmp;

    public void setCallBack(SaveImge callBack) {
        this.callBack = callBack;
    }

    public SaveImageTask(Bitmap bmp) {
        this.bmp = bmp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String name = FileUtils.getFileName() + ".jpg";
        String fileName = FileUtils.getClxImgSavePath() + name;
        BitmapUtils.createImgToFile(bmp, fileName);
        bmp.recycle();
        Utils.fileScan(CLXApplication.getInstance(), fileName);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        callBack.saveFinish();
    }

    public interface SaveImge {
        void saveFinish();
    }
}
