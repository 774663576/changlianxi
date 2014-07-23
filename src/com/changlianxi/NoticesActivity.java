package com.changlianxi;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 使用条款和隐私说明
 * 
 * @author teeker_bin
 * 
 */
public class NoticesActivity extends BaseActivity {
    private ImageView back;
    private TextView titleTxt;
    private WebView wb;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Utils.rightOut(NoticesActivity.this);
            }
        });
        dialog = DialogUtil.getWaitDialog(this, "加载中");
        dialog.show();
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("软件许可及服务协议");
        wb = (WebView) findViewById(R.id.webView1);
        wb.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                if (progress == 100) {
                    dialog.dismiss();
                }
            }
        });
        wb.loadUrl("http://www.changlianxi.com/pages/agreement");

    }

    /**设置页面统计
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }
}
