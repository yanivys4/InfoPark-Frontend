package com.example.infopark.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.infopark.R;

import java.util.UUID;

public class SplashActivity extends Activity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Context context = SplashActivity.this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean isLoggedIn = getIsLoggedIn();

        // if loggedIn is true don't change it
        if (!isLoggedIn) {
            setIsLoggedInFalse();
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            // if the user is not logged in send it to login activity
            if (!isLoggedIn) {
                intent = LoginActivity.makeIntent();
            // otherwise send it directly to main activity
            } else {
                intent = MainActivity.makeIntent();
            }
            startActivity(intent);
            finish();
        }, 3000);

    }

    private boolean getIsLoggedIn(){
        return sharedPref.getBoolean(getString(R.string.loggedIn), false);
    }

    private void setIsLoggedInFalse(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), false);
        editor.apply();
    }
}
