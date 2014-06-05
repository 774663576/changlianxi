package com.changlianxi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class GridViewItemLayout extends RelativeLayout {

	public GridViewItemLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GridViewItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GridViewItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		// int childHeightSize = getMeasuredHeight();
		// 高度和宽度一样
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
