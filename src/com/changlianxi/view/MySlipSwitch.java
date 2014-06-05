package com.changlianxi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.changlianxi.R;

public class MySlipSwitch extends View implements OnTouchListener {

    // 开关开启时的背景，关闭时的背景，滑动按钮
    private Bitmap switch_on_Bkg, switch_off_Bkg;

    // 是否正在滑动
    // 当前开关状态，true为开启，false为关闭
    private boolean isSwitchOn = false;

    // 手指按下时的水平坐标X，当前的水平坐标X
    private float previousX, currentX;

    // 开关监听器
    private OnSwitchListener onSwitchListener;
    // 是否设置了开关监听器
    private boolean isSwitchListenerOn = false;

    public MySlipSwitch(Context context) {
        super(context);
        init();
    }

    public MySlipSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        switch_on_Bkg = BitmapFactory.decodeResource(getResources(),
                  R.drawable.icon_on);
        switch_off_Bkg = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_off);
        setOnTouchListener(this);
    }

    public void setSwitchState(boolean switchState) {
        isSwitchOn = switchState;
    }

    public boolean getSwitchState() {
        return isSwitchOn;
    }

    protected void updateSwitchState(boolean switchState) {
        isSwitchOn = switchState;
        invalidate();
    }

    @SuppressLint({ "DrawAllocation", "DrawAllocation" })
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        Matrix matrix = new Matrix();
        Paint paint = new Paint();

        // 手指滑动到左半边的时候表示开关为关闭状态，滑动到右半边的时候表示开关为开启状态
        if (currentX < (switch_on_Bkg.getWidth() / 2)) {
            canvas.drawBitmap(switch_off_Bkg, matrix, paint);
        } else {
            canvas.drawBitmap(switch_on_Bkg, matrix, paint);
        }
        if (isSwitchOn) {
            canvas.drawBitmap(switch_on_Bkg, matrix, paint);
        } else {
            canvas.drawBitmap(switch_off_Bkg, matrix, paint);

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(switch_on_Bkg.getWidth(),
                switch_on_Bkg.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            // 滑动
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                break;

            // 按下
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > switch_on_Bkg.getWidth()
                        || event.getY() > switch_on_Bkg.getHeight()) {
                    return false;
                }

                previousX = event.getX();
                currentX = previousX;
                break;

            // 松开
            case MotionEvent.ACTION_UP:
                // 松开前开关的状态
                boolean previousSwitchState = isSwitchOn;

                if (event.getX() >= (switch_on_Bkg.getWidth() / 2)) {
                    isSwitchOn = true;
                } else {
                    isSwitchOn = false;
                }

                // 如果设置了监听器，则调用此方法
                if (isSwitchListenerOn && (previousSwitchState != isSwitchOn)) {
                    onSwitchListener.onSwitched(isSwitchOn);
                }
                break;

            default:
                break;
        }

        // 重新绘制控件
        invalidate();
        return true;
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        onSwitchListener = listener;
        isSwitchListenerOn = true;
    }

    public interface OnSwitchListener {
        abstract void onSwitched(boolean isSwitchOn);
    }
}
