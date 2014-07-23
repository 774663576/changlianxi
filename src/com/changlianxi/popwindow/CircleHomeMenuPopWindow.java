package com.changlianxi.popwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.changlianxi.MessageListActivity;
import com.changlianxi.MyCardActivity;
import com.changlianxi.R;
import com.changlianxi.SettingActivity;
import com.changlianxi.util.Utils;

public class CircleHomeMenuPopWindow implements OnClickListener {
    private PopupWindow popupWindow;
    private Context mContext;
    private View v;
    private View view;
    private Button btnMyCard;
    private Button btnMessageCenter;
    private Button btnSetting;

    public CircleHomeMenuPopWindow(Context context, View v) {
        this.mContext = context;
        this.v = v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.circle_home_menu_layout, null);
        initView();
        initPopwindow();
    }

    private void initView() {
        btnMessageCenter = (Button) view.findViewById(R.id.btnMessageCenter);
        btnMyCard = (Button) view.findViewById(R.id.btnMyCard);
        btnSetting = (Button) view.findViewById(R.id.btnSetting);
        setListener();
    }

    private void setListener() {
        btnMessageCenter.setOnClickListener(this);
        btnMyCard.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
    }

    /**
     * 初始化popwindow
     */
    @SuppressWarnings("deprecation")
    private void initPopwindow() {
        popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * popwindow的显示
     */
    public void show() {
        popupWindow.showAsDropDown(v);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }

    // 隐藏
    public void dismiss() {
        popupWindow.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMessageCenter:
                mContext.startActivity(new Intent(mContext,
                        MessageListActivity.class));
                break;
            case R.id.btnSetting:
                mContext.startActivity(new Intent(mContext,
                        SettingActivity.class));
                break;
            case R.id.btnMyCard:
                mContext.startActivity(new Intent(mContext,
                        MyCardActivity.class));
                break;
            default:
                break;
        }
        dismiss();
        Utils.leftOutRightIn(mContext);
    }
}
