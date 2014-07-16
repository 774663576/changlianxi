package com.changlianxi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.changlianxi.R;

public class MyCheckBox extends ImageView {
    public boolean isChecked = true;
    private CheckListener mCheckListener;

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    isChecked = true;
                    setImageResource(R.drawable.register_checkbox_select);
                } else {
                    isChecked = false;
                    setImageResource(R.drawable.register_checkbox_nomal);
                }
                mCheckListener.onCheckedChanged(isChecked);
            }
        });
    }

    public void setmCheckListener(CheckListener mCheckListener) {
        this.mCheckListener = mCheckListener;
    }

    public interface CheckListener {
        void onCheckedChanged(boolean isChecked);
    }
}
