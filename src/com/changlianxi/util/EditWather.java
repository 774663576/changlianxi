package com.changlianxi.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.changlianxi.R;

/**
 * 手机号码分段显示
 * 
 * @author teeker_bin
 * 
 */
public class EditWather implements TextWatcher {
    private EditText edit;
    private Context mContext;

    public EditWather(EditText edit, Context context) {
        this.edit = edit;
        this.mContext = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.length() > 0) {
            Drawable del = mContext.getResources().getDrawable(R.drawable.del);
            del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
            edit.setCompoundDrawables(null, null, del, null);
        } else {
            edit.setCompoundDrawables(null, null, null, null);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        StringBuffer sb = new StringBuffer(s);
        if (count == 1) {
            if (s.length() == 4) {
                sb.insert(3, "-");
                edit.setText(sb.toString());
                edit.setSelection(5);
            }
            if (s.length() == 9) {
                sb.insert(8, "-");
                edit.setText(sb.toString());
                edit.setSelection(10);
            }

        } else if (count == 0) {
            if (s.length() == 4) {
                edit.setText(s.subSequence(0, s.length() - 1));
                edit.setSelection(3);
            }
            if (s.length() == 9) {
                edit.setText(s.subSequence(0, s.length() - 1));
                edit.setSelection(8);
            }

        }
    }

}
