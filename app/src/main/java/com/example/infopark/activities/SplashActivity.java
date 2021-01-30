package com.example.infopark.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.infopark.R;

import java.util.UUID;

/**
This activity is the entrance of the application and appear each time the app is initially started.
 */
public class SplashActivity extends Activity {

    private SharedPreferences sharedPref;

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g.,
     * checking isLoggedIn state and starting the next activity.
     *
     * @param savedInstanceState
     *            object that contains saved state information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
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
       startNextActivity(isLoggedIn);
    }
    //============================================================================================
    /**
     * This function get the isLoggedIn shared pref variable
     * @return boolean
     * The isLoggedIn variable
     */
    private boolean getIsLoggedIn(){
        return sharedPref.getBoolean(getString(R.string.loggedIn), false);
    }
    //============================================================================================
    /**
     * This function set the isLoggedIn shared pref variable to false
     */
    private void setIsLoggedInFalse(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), false);
        editor.apply();
    }
    //============================================================================================
    /**
     * This function is wrapped by a handler that make sure it will execute after 3000 m/s.
     * the function gets the status of isLoggedIn and accordingly starts the right next activity.
     * @param isLoggedIn boolean the isLoggedIn status.
     *
     */
    private void startNextActivity(boolean isLoggedIn){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            // if the user is not logged in send it to login activity
            if (!isLoggedIn) {
                intent = LoginActivity.makeIntent();
                // otherwise send it directly to main activity
            } else {
                intent = ReportActivity.makeIntent();
            }
            startActivity(intent);
            finish();
        }, 3000);
    }
    //============================================================================================
}
