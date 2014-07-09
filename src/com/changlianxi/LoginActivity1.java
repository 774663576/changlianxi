package com.changlianxi;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoginActivity1 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_activity1, menu);
        return true;
    }
}
