package com.changlianxi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.UpdateCircleIdetailTask;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;

public class EditCircleActivity1 extends BaseActivity implements
        OnClickListener {
    private ImageView back;
    private Button btnFinish;
    private EditText editContext;
    private TextView tite;
    private Circle circle;
    private int tag = 0;
    private Dialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_circle_activity1);
        getDataFormPreActivity();
        initView();
        setValue();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        editContext = (EditText) findViewById(R.id.editContent);
        tite = (TextView) findViewById(R.id.titleTxt);
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    private void setValue() {
        if (tag == 1) {
            editContext.setText(circle.getName());
            tite.setText("圈子名称");
        } else if (tag == 2) {
            editContext.setText(circle.getDescription());
            tite.setText("圈子描述");
        }
    }

    private void getDataFormPreActivity() {
        circle = (Circle) getIntent().getExtras().getSerializable("circle");
        tag = getIntent().getIntExtra("tag", 0);

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

    private void saveInfo(final Circle newCircle) {
        pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
        UpdateCircleIdetailTask circleIdetailTask = new UpdateCircleIdetailTask(
                circle, newCircle);
        circleIdetailTask
                .setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
                    @Override
                    public void taskFinish(RetError result) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        if (result == RetError.NONE) {
                            if (tag == 1) {
                                circle.setName(newCircle.getName());
                            } else if (tag == 1) {
                                circle.setDescription(newCircle
                                        .getDescription());
                            }
                            Utils.showToast("修改成功", Toast.LENGTH_SHORT);
                            BroadCast.sendBroadCast(EditCircleActivity1.this,
                                    Constants.REFRESH_CIRCLE_LIST);
                            Intent intent = new Intent();
                            Bundle b = new Bundle();
                            b.putSerializable("circle", circle);
                            intent.putExtras(b);
                            setResult(Constants.EDIT_CIRCL, intent);
                            exit();
                        }

                    }

                    @Override
                    public void readDBFinish() {

                    }
                });
        circleIdetailTask.executeWithCheckNet();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_finish:
                Circle newCircle = new Circle(circle.getId());
                if (tag == 1) {
                    if (editContext.getText().toString().length() == 0) {
                        Utils.showToast("圈子名称是必须的哦！", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (StringUtils.calculatePlaces(editContext.getText()
                            .toString()) > 12) {
                        promptDialog();
                        return;
                    }
                    newCircle.setName(editContext.getText().toString());
                    newCircle.setDescription(circle.getDescription());
                } else if (tag == 2) {
                    newCircle.setName(circle.getName());
                    newCircle.setDescription(editContext.getText().toString());
                }
                saveInfo(newCircle);
                break;
            default:
                break;
        }
    }
}
