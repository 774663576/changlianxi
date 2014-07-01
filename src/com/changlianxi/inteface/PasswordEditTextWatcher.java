package com.changlianxi.inteface;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.changlianxi.R;
import com.changlianxi.view.SearchEditText;

public class PasswordEditTextWatcher implements TextWatcher {
    private EditText edit;
    private Context mContext;

    public PasswordEditTextWatcher(EditText edit, Context context,
            boolean isPswd) {
        this.edit = edit;
        mContext = context;
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

    }

}
