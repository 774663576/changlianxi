package com.changlianxi;

import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.changlianxi.R;

/**
 * 纸质通讯录导入
 * 
 * @author teeker_bin
 * 
 */
public class PageActivity extends BaseActivity {
    private ImageView back;
    private TextView titleTxt;
    private WebView wb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Utils.rightOut(PageActivity.this);

            }
        });
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText("纸质通讯录导入说明");
        wb = (WebView) findViewById(R.id.webView1);
        // wb.loadUrl("http://i.changlianxi.com/pages/import_paper_address_book");//
        // 测试
        wb.loadUrl("http://www.changlianxi.com/pages/import_paper_address_book");// 线上
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
