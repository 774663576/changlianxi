package com.changlianxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.util.Utils;

public class InputCircleGroupNameActivity extends BaseActivity implements
        OnClickListener {
    private ImageView back;
    private TextView title;
    private Button btnOk;
    private EditText editGroupName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_circle_group_name);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.titleTxt);
        btnOk = (Button) findViewById(R.id.btn_finish);
        editGroupName = (EditText) findViewById(R.id.circle_group_name);
        title.setText("设置分组名称");
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.btn_finish:
                String name = editGroupName.getText().toString()
                        .replace(" ", "");
                if (name.length() == 0) {
                    Utils.showToast("分组名称不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("name", name);
                setResult(2, intent);
                exit();

                break;
            default:
                break;
        }
    }
}
