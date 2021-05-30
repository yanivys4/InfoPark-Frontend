package com.example.infopark.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infopark.R;

public class TestActivity extends AppCompatActivity {
    private static final String ACTION_TEST_ACTIVITY =
            "android.intent.action.ACTION_TEST_ACTIVITY";
    private static final String TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public static Intent makeIntent(){return new Intent(ACTION_TEST_ACTIVITY);}
}
