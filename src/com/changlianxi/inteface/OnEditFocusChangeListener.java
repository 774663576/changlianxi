package com.changlianxi.inteface;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.changlianxi.R;

public class OnEditFocusChangeListener implements OnFocusChangeListener {
    private EditText edit;
    private Context context;

    public OnEditFocusChangeListener(EditText edit, Context context) {
        this.edit = edit;
        this.context = context;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            ((EditText) v).setCompoundDrawables(null, null, null, null);

        } else {
            String str = edit.getText().toString();
            if (str.length() > 0) {
                Drawable del = context.getResources().getDrawable(
                        R.drawable.del);
                del.setBounds(0, 0, del.getMinimumWidth(),
                        del.getMinimumHeight());
                edit.setCompoundDrawables(null, null, del, null);
            } else {
                edit.setCompoundDrawables(null, null, null, null);

            }
        }
    }
}
