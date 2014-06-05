package com.changlianxi.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/* 
 * 获取、设置控件信息 
 */
public class WigdtContorl {
	public static int delaultY;
	public static Visible visible;
	public static int moveY;

	/*
	 * 获取控件宽
	 */
	public static int getWidth(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredWidth());
	}

	/*
	 * 获取控件高
	 */
	public static int getHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredHeight());
	}

	/*
	 * 设置控件所在的位置X，并且不改变宽高， X为绝对位置，此时Y可能归0
	 */
	public static void setLayoutX(View view, int x) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, margin.topMargin, x + margin.width,
				margin.bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	/*
	 * 设置控件所在的位置Y，并且不改变宽高， Y为绝对位置，此时X可能归0
	 */
	public static void setLayoutY_UP(View view, int y, Context context, View v) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		if (view.getTop() - y <= moveY) {// margin小于moveY时赋值为moveY
			margin.setMargins(margin.leftMargin, moveY, margin.rightMargin,
					margin.bottomMargin);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					margin);
			view.setLayoutParams(layoutParams);
			visible.setVisible(true);
			return;
		}
		margin.setMargins(margin.leftMargin, view.getTop() - y,
				margin.rightMargin, margin.bottomMargin);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
		visible.setVisible(true);

	}

	public static void setLayoutY_Down(View view, int y, Context context, View v) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		if (view.getTop() - y > delaultY) {
			// margin大于delaulty时赋值为delaulty
			margin.setMargins(margin.leftMargin, delaultY, margin.rightMargin,
					margin.bottomMargin);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					margin);
			view.setLayoutParams(layoutParams);
			visible.setVisible(false);
			return;
		}
		margin.setMargins(margin.leftMargin, view.getTop() - y,
				margin.rightMargin, margin.bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
		visible.setVisible(false);

	}

	/*
	 * 设置控件所在的位置YY，并且不改变宽高， XY为绝对位置
	 */
	public static void setLayout(View view, int x, int y) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, y, x + margin.width, y + margin.height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	public static void setVisible(Visible vi) {
		visible = vi;
		visible.setVisible(false);
	}

	public interface Visible {
		public void setVisible(boolean flag);
	}

	/**
	 * 设定控件的宽高值
	 * 
	 * @param img
	 * @param context
	 * @param width
	 * @param marginTop
	 * @param marginLeft
	 * @param marginRight
	 * @param marginButtom
	 */
	public static void setViewWidth(ImageView img, Context context, int width,
			int marginTop, int marginLeft, int marginRight, int marginButtom) {
		int Screenwidth = Utils.getSecreenWidth(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				Screenwidth / width, Screenwidth / width);
		lp.setMargins(marginLeft, marginTop, marginRight, marginButtom);
		img.setLayoutParams(lp);
	}

	/**
	 * 设置首页圈子图标的大小
	 * 
	 * @param context
	 * @param avatar
	 * @param avatarBg
	 */
	public static void setAvatarWidth(Context context, ImageView avatar,
			ImageView avatarBg, int wh, int bgwh) {
		int width = Utils.getSecreenWidth(context) / wh;
		int bgwidth = width + width / bgwh;
		RelativeLayout.LayoutParams avataParams = new RelativeLayout.LayoutParams(
				width, width);
		avataParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		RelativeLayout.LayoutParams avatarBgParams = new RelativeLayout.LayoutParams(
				bgwidth, bgwidth);
		avatarBgParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		avatar.setLayoutParams(avataParams);
		avatarBg.setLayoutParams(avatarBgParams);
	}

	/**
	 * 设置宽高值
	 * 
	 * @param context
	 * @param avatar
	 */
	public static void setImageWidth(Context context, ImageView img) {
		int width = Utils.getSecreenWidth(context) / 4;
		LinearLayout.LayoutParams avataParams = new LinearLayout.LayoutParams(
				width, width);
		img.setLayoutParams(avataParams);

	}

	/**
	 * 设置listview的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeight(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int count = listAdapter.getCount();
		if (count == 0) {
			return;
		}
		int totalHeight = 0;
		View listItem = listAdapter.getView(0, null, listView);
		listItem.measure(0, 0); // 计算子项View 的宽高
		totalHeight = listItem.getMeasuredHeight(); // 统计所有子项的总高度

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = count > 6 ? totalHeight * 6 : totalHeight * count;
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}