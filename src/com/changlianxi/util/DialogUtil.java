package com.changlianxi.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.service.UpdateService;

public class DialogUtil {
    public static Dialog getWaitDialog(Context context, String str) {

        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.firset_dialog_view, null);
        TextView titleTxtv = (TextView) view.findViewById(R.id.tvLoad);
        titleTxtv.setText(str + "...");
        ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
        view.getBackground().setAlpha(100);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) WigdtContorl.getWidth(view);
        lp.height = (int) WigdtContorl.getHeight(view);

        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        return dialog;
    }

    /**
     * 确认对话框
     * 
     * @param context
     * @param title
     * @param content
     */
    public static Dialog confirmDialog(Context context, String content,
            String txtOk, String txtCancle, final ConfirmDialog callBack) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.confirm_dialog, null);
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancle = (Button) view.findViewById(R.id.btnCancle);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onOKClick();
                dialog.dismiss();

            }
        });
        btnCancle.setText(txtCancle);
        btnOk.setText(txtOk);
        btnCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                callBack.onCancleClick();
                dialog.dismiss();
            }
        });
        TextView txt = (TextView) view.findViewById(R.id.dialogContent);
        txt.setText(content);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        /* * 将对话框的大小按屏幕大小的百分比设置 */

        WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
        // 获取对话框当前的参数值//
        p.width = (int) (Utils.getSecreenWidth(context) * 0.8); //
        // 宽度设置为屏幕的0.8//
        p.y = -70;
        dialogWindow.setAttributes(p);
        return dialog;

    }

    /**
     *  确认对话框
     * 
     * @param context
     * @param title
     * @param content
     */
    public static Dialog promptDialog(Context context, String content,
            String txtOk, final ConfirmDialog callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog,
                null);
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.onOKClick();

            }
        });
        btnOk.setText(txtOk);
        TextView txt = (TextView) view.findViewById(R.id.dialogContent);
        txt.setText(content);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        /* * 将对话框的大小按屏幕大小的百分比设置 */

        WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
        // 获取对话框当前的参数值//
        p.width = (int) (Utils.getSecreenWidth(context) * 0.8); //
        // 宽度设置为屏幕的0.8//
        p.y = -70;
        dialogWindow.setAttributes(p);
        return dialog;

    }

    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        // loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;

    }

    /**
     * 新版本提示对话框
     * @param context
     * @param str
     * @param link
     */
    public static void newVersion(final Context context, String str,
            final String link) {
        Dialog dialog = confirmDialog(context, str, "立即下载", "下次再说",
                new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        Intent intent = new Intent();
                        intent.setAction("com.changlianxi.service.versionservice");
                        intent.putExtra("url", link);
                        context.startService(intent);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

}