package com.example.infopark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.infopark.R;

public class InfoActivity extends AppCompatActivity {

    private static final String ACTION_INFO_ACTIVITY =
            "android.intent.action.ACTION_INFO_ACTIVITY";
    private static final String TAG = InfoActivity.class.getSimpleName();

    // Views members

    private TextView sunThuHours;
    private TextView friHours;
    private TextView maxHours;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        initializeViews();

    }

    private void initializeViews(){
        sunThuHours = findViewById(R.id.sun_thu_hours);
        friHours = findViewById(R.id.fri_hours);
        maxHours = findViewById(R.id.max_hours);
    }
    public static Intent makeIntent(){
        return new Intent(ACTION_INFO_ACTIVITY);
    }

    public void finishActivity(View view) {
        this.finish();
    }
}
