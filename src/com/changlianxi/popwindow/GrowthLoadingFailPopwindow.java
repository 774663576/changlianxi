package com.changlianxi.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;

public class GrowthLoadingFailPopwindow implements OnClickListener {
    private PopupWindow popupWindow;
    private Context mContext;
    private View v;
    private View view;
    private FailGrowth callback;
    private LinearLayout bg;
    private Button btnSend;
    private Button btnDel;

    public GrowthLoadingFailPopwindow(Context context, View v) {
        this.mContext = context;
        this.v = v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.growth_loading_fail, null);
        initView();
        initPopwindow();
    }

    private void initView() {
        bg = (LinearLayout) view.findViewById(R.id.layoutBg);
        bg.getBackground().setAlpha(200);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        btnDel = (Button) view.findViewById(R.id.btnDel);
        btnSend.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        bg.setOnClickListener(this);
    }

    /**
     * 初始化popwindow
     */
    @SuppressWarnings("deprecation")
    private void initPopwindow() {
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        // 这个是为了点击�?返回Back”也能使其消失，并且并不会影响你的背景（很神奇的�?
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * popwindow的显�?
     */
    public void show() {
        popupWindow.showAtLocation(v, 0, 0, 0);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状�?
        popupWindow.update();
    }

    // 隐藏
    public void dismiss() {
        popupWindow.dismiss();
    }

    public void setCallback(FailGrowth callback) {
        this.callback = callback;
    }

    class ViewHolder {
        TextView text;
        LinearLayout laybg;
    }

    public interface FailGrowth {
        void ToSend();

        void DelGrowth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                callback.ToSend();
                break;
            case R.id.btnDel:
                callback.DelGrowth();
                break;
            default:
                break;
        }
        dismiss();

    }
}
