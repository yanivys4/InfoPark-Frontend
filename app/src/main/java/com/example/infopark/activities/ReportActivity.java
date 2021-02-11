package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infopark.R;
import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.RESTApi.LoginResponse;
import com.example.infopark.RESTApi.ReportForm;
import com.example.infopark.RESTApi.RequestSavedLocation;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.RESTApi.SavedLocation;
import com.example.infopark.Utils.Utils;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private ProgressBar progressBar;

    private Context context;
    private SharedPreferences sharedPref;

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g.
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // Initialize the views.
        initializeViews();

        // Initialize the sharedPref
        context = ReportActivity.this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        initSpinners();
    }

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        fromASpinner = findViewById(R.id.fromASpinner);
        toASpinner = findViewById(R.id.toASpinner);
        fromBSpinner = findViewById(R.id.fromBSpinner);
        toBSpinner = findViewById(R.id.toBSpinner);
        maxHoursSpinner = findViewById(R.id.maxHoursSpinner);
        reportButton = findViewById(R.id.reportButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * This function is the onCLick method of the report button in the report form.
     * The function send the data entered to the BackEnd using the report method.
     * @param v the View
     */
    public void reportClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String uniqueID = getUserUniqueID();
        String fromA = fromASpinner.getSelectedItem().toString();
        String toA = toASpinner.getSelectedItem().toString();
        String fromB = fromBSpinner.getSelectedItem().toString();
        String toB = toBSpinner.getSelectedItem().toString();
        String maxHours = maxHoursSpinner.getSelectedItem().toString();

        ReportForm reportForm = new ReportForm(uniqueID, fromA, toA, fromB, toB, maxHours);

        report(reportForm);
    }

    /**
     * This function using the RestApi to set the report attempt to be approved by the user.
     * @param reportForm an object that contains the report data to be sent to the server.
     */
    private void report(ReportForm reportForm) {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<ResponseMessage> call = restApi.report(reportForm);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMessage> secondCall, @NonNull Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(ReportActivity.this, "Code:" + response.code());
                    return;
                }

                ResponseMessage responseMessage = response.body();
                assert responseMessage != null;
                if (!responseMessage.getSuccess()) {
                    Utils.showToast(ReportActivity.this, responseMessage.getDescription());
                } else {
                    Utils.showToast(ReportActivity.this, getString(R.string.report_success));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(ReportActivity.this, t.getMessage());
            }
        });
    }

    /**
     * This function return the user unique id that saved in the sharedPref.
     * @return string that represent the user unique id
     */
    private String getUserUniqueID() {
        return sharedPref.getString(getString(R.string.uniqueID), null);
    }

    /**
     * This function initialize the spinners
     */
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

    /**
     * This function set the on item selected listeners to the spinners
     */
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

    /**
     * This function disable all spinner option before some position.
     * @param spinner the spinner to disabled
     * @param pos the position
     */
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

    /**
     * This function create an string array that contain all day times in 30 minute jumps.
     * @return string array that contain all day times
     */
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

    /**
     * Factory method that returns an Intent for starting the RegisterActivity.
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_REPORT_ACTIVITY);
    }

    /**
     * This function finish the activity.
     * @param view the view
     */
    public void finishActivity(View view) {
        this.finish();
    }
}
