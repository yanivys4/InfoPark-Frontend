package com.example.infopark.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infopark.R;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private static final String ACTION_REPORT_ACTIVITY =
            "android.intent.action.ACTION_REPORT_ACTIVITY";
    private static final String TAG = ReportActivity.class.getSimpleName();
    private ImageButton backButton;
    private Spinner fromASpinner;
    private Spinner toASpinner;
    private Spinner fromBSpinner;
    private Spinner toBSpinner;
    private Spinner maxHoursSpinner;
    private Button reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        backButton = findViewById(R.id.back_button);
        fromASpinner = findViewById(R.id.fromASpinner);
        toASpinner = findViewById(R.id.toASpinner);
        fromBSpinner = findViewById(R.id.fromBSpinner);
        toBSpinner = findViewById(R.id.toBSpinner);
        maxHoursSpinner = findViewById(R.id.maxHoursSpinner);
        reportButton = findViewById(R.id.reportButton);

        initSpinners();
    }

    public void reportClick(View v) {

    }

    private void initSpinners() {
        String[] items = getDayTimesArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        fromASpinner.setAdapter(adapter);
        toASpinner.setAdapter(adapter);
        fromBSpinner.setAdapter(adapter);
        toBSpinner.setAdapter(adapter);

        fromASpinner.setSelection(14);
        fromBSpinner.setSelection(14);

        setOnItemSelectedListenerToSpinners();

        String[] maxHoursArr = {"NONE", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, maxHoursArr);

        maxHoursSpinner.setAdapter(adapter2);
    }

    private void setOnItemSelectedListenerToSpinners() {
        fromASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                disableAllOptionsBeforePos(toASpinner, position);
                if (position < toASpinner.getAdapter().getCount() - 1)
                    toASpinner.setSelection(position + 1);
                else
                    toASpinner.setSelection(position);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        fromBSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                disableAllOptionsBeforePos(toBSpinner, position);
                if (position < toBSpinner.getAdapter().getCount() - 1)
                    toBSpinner.setSelection(position + 1);
                else
                    toBSpinner.setSelection(position);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void disableAllOptionsBeforePos(Spinner spinner, int pos) {
        String[] items = getDayTimesArray();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position <= pos) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position <= pos) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinner.setAdapter(adapter);
    }

    private String[] getDayTimesArray() {
        String[] halfHours = {"00","30",};
        List<String> times = new ArrayList<String>(); // <-- List instead of array

        for(int i = 0; i < 24; i++) {
            for(int j = 0; j < 2; j++) {
                String time = i + ":" + halfHours[j];
                if(i < 10) {
                    time = "0" + time;
                }
                times.add(time);
            }
        }

        return times.toArray(new String[0]);
    }

    public static Intent makeIntent() {
        return new Intent(ACTION_REPORT_ACTIVITY);
    }

    public void finishActivity(View view) {
        this.finish();
    }
}
