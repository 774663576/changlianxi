package com.changlianxi.inteface;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.changlianxi.R;

public class MyEditTextWatcher implements TextWatcher {
    private EditText edit;
    private Context mContext;
    private OnTextLengthChange mOnTextLengthChange;

    public MyEditTextWatcher(EditText edit, Context context,
            OnTextLengthChange mOnTextLengthChange) {
        this.edit = edit;
        mContext = context;
        this.mOnTextLengthChange = mOnTextLengthChange;

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.length() > 0) {
            Drawable del = mContext.getResources().getDrawable(R.drawable.del);
            del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
            edit.setCompoundDrawables(null, null, del, null);
            if (mOnTextLengthChange != null) {
                mOnTextLengthChange.onTextLengthChanged(false);
            }
        } else {
            edit.setCompoundDrawables(null, null, null, null);
            if (mOnTextLengthChange != null) {
                mOnTextLengthChange.onTextLengthChanged(true);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public OnTextLengthChange getmOnTextLengthChange() {
        return mOnTextLengthChange;
    }

    public void setmOnTextLengthChange(OnTextLengthChange mOnTextLengthChange) {
        this.mOnTextLengthChange = mOnTextLengthChange;
    }

    public interface OnTextLengthChange {
        void onTextLengthChanged(boolean isBlank);
    }
}
