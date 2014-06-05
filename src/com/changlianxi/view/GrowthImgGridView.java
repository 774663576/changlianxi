package com.changlianxi.view;

import android.view.MotionEvent;
import android.widget.GridView;

public class GrowthImgGridView extends GridView {
    private GetClickPosition callBack;

    public GrowthImgGridView(android.content.Context context,
            android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (this.callBack != null) {
                    this.callBack.getPosition(pointToPosition((int) ev.getX(),
                            (int) ev.getY()));
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setCallBack(GetClickPosition callBack) {
        this.callBack = callBack;
    }

    public interface GetClickPosition {
        void getPosition(int position);
    }
}
