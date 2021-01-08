package com.example.infopark.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.infopark.R;

public class SplashActivity extends Activity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        handler=new Handler();
        handler.postDelayed(() -> {
            Intent intent=RegisterActivity.makeIntent();
            startActivity(intent);
            finish();
        },3000);

    }
}
