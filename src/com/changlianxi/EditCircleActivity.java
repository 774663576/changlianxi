package com.changlianxi;

import java.io.File;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.popwindow.SelectPicPopwindow.CameraPath;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.UpdateCircleIdetailTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 编辑圈子界面
 * 
 * @author teeker_bin
 * 
 */
public class EditCircleActivity extends BaseActivity implements
        OnClickListener, CameraPath {
    private Button btnSave;
    private EditText circleName;// 圈子名称
    private TextView titleName;
    private EditText circleDescription;// 圈子描述
    private ImageView circleLogo;
    private int cid;
    private Dialog pd;
    private String logoPath = "";
    private String upLoadPath = "";
    private ImageView back;
    private SelectPicPopwindow pop;
    private Circle circle;
    private boolean isCamera = false;
    private Bitmap logoBmp;
    private String selectPicPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_circle);
        circle = (Circle) getIntent().getSerializableExtra("circle");
        cid = circle.getId();
        findViewByID();
        setListener();
        filldata();
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

    private boolean compare() {
        return "".equals(upLoadPath)
                && circle.getName().equals(circleName.getText().toString())
                && circle.getDescription().equals(
                        circleDescription.getText().toString());
    }

    private void filldata() {
        circleDescription.setText(circle.getDescription());
        circleName.setText(circle.getName());
        titleName.setText(circle.getName());
        if ("".equals(circle.getLogo())) {
            circleLogo.setImageResource(R.drawable.pic_bg_no);
            return;
        }
        setLogo(circle.getLogo());
    }

    private void setLogo(String logo) {
        Bitmap logoBmp = getIntent().getExtras().getParcelable("logoBmp");
        if (logoBmp != null) {
            circleLogo.setImageBitmap(logoBmp);
        } else {
            circleLogo.setImageResource(R.drawable.pic_bg_no);
        }

    }

    private void findViewByID() {
        btnSave = (Button) findViewById(R.id.btnsave);
        titleName = (TextView) findViewById(R.id.titleTxt);
        circleName = (EditText) findViewById(R.id.circleName);
        circleDescription = (EditText) findViewById(R.id.circleDis);
        circleLogo = (ImageView) findViewById(R.id.circleLogo);
        back = (ImageView) findViewById(R.id.back);

    }

    private void setListener() {
        btnSave.setOnClickListener(this);
        circleLogo.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void promptDialog() {
        Dialog dialog = DialogUtil.promptDialog(this,
                "圈子名称长度超过限制，请控制在12个汉字以内，字母和数字两个字符算一个汉字", "确定",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {

                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    private void exitConfig() {
        Dialog dialog = DialogUtil.confirmDialog(this, "是否放弃编辑？", "是", "否",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        exit();

                    }

                    @Override
                    public void onCancleClick() {
                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsave:
                if (circleName.getText().toString().length() == 0) {
                    Utils.showToast("圈子名称是必须的哦！", Toast.LENGTH_SHORT);
                    return;
                }
                if (StringUtils
                        .calculatePlaces(circleName.getText().toString()) > 12) {
                    promptDialog();
                    return;
                }
                saveInfo();
                break;
            case R.id.circleLogo:
                Utils.hideSoftInput(this);
                pop = new SelectPicPopwindow(this, v);
                pop.show();
                pop.setCallBack(this);
                break;
            case R.id.back:
                if (!compare()) {
                    exitConfig();
                    return;
                }
                exit();
                break;

            default:
                break;
        }
    }

    /**
     * 保存修改信息
     * 
     * @param url
     */
    private void saveInfo() {
        pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
        Circle newCircle = new Circle(cid, circleName.getText().toString(),
                circleDescription.getText().toString(), upLoadPath);
        UpdateCircleIdetailTask circleIdetailTask = new UpdateCircleIdetailTask(
                circle, newCircle);
        circleIdetailTask
                .setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
                    @Override
                    public void taskFinish(RetError result) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Intent intent = new Intent(Constants.EDIT_CIRCLE_INFO);
                        intent.putExtra("cid", cid);
                        intent.putExtra("logoBmp", logoBmp);
                        BroadCast
                                .sendBroadCast(EditCircleActivity.this, intent);
                        BroadCast.sendBroadCast(EditCircleActivity.this,
                                Constants.REFRESH_CIRCLE_LIST);
                        exit();

                    }

                    @Override
                    public void readDBFinish() {

                    }
                });
        circleIdetailTask.executeWithCheckNet();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
                && resultCode == RESULT_OK && data != null) {
            logoPath = BitmapUtils.getPickPic(this, data);
            upLoadPath = BitmapUtils.startPhotoZoom(this, data.getData());
            isCamera = false;
        }// 拍摄图片
        else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (pop == null) {
                return;
            }
            logoPath = selectPicPath;
            upLoadPath = BitmapUtils.startPhotoZoom(this,
                    Uri.fromFile(new File(logoPath)));
            isCamera = true;
        } else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
                && data != null) {
            if (isCamera) {
                File file = new File(logoPath);
                if (file.isFile() && file.exists()) {
                    file.delete();
                }
            }
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                circleLogo.setImageBitmap(photo);
                logoBmp = photo;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!compare()) {
                exitConfig();
                return false;
            }
            exit();
        }
        return false;
    }

    @Override
    public void getCameraPath(String path) {
        selectPicPath = path;
    }
}
