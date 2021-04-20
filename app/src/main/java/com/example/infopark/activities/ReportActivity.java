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
import com.example.infopark.RESTApi.ReportForm;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportActivity extends AppCompatActivity {
    private static final String ACTION_REPORT_ACTIVITY =
            "android.intent.action.ACTION_REPORT_ACTIVITY";
    private static final String TAG = ReportActivity.class.getSimpleName();
    private Button backButton;
    private Spinner fromSunThuSpinner;
    private Spinner toSunThuSpinner;
    private Spinner fromFriSpinner;
    private Spinner toFriSpinner;
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
    //============================================================================================

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        fromSunThuSpinner = findViewById(R.id.fromSunThuSpinner);
        toSunThuSpinner = findViewById(R.id.toSunThuSpinner);
        fromFriSpinner = findViewById(R.id.fromFriSpinner);
        toFriSpinner = findViewById(R.id.toFriSpinner);
        maxHoursSpinner = findViewById(R.id.maxHoursSpinner);
        reportButton = findViewById(R.id.reportButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
    //============================================================================================

    /**
     * This function is the onCLick method of the report button in the report form.
     * The function send the data entered to the BackEnd using the report method.
     * @param v the View
     */
    public void reportClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String uniqueID = getUserUniqueID();
        String fromSunThu = fromSunThuSpinner.getSelectedItem().toString();
        String toSunThu = toSunThuSpinner.getSelectedItem().toString();
        String fromFri = fromFriSpinner.getSelectedItem().toString();
        String toFri = toFriSpinner.getSelectedItem().toString();
        String maxHours = maxHoursSpinner.getSelectedItem().toString();

        ReportForm reportForm = new ReportForm(uniqueID, fromSunThu, toSunThu, fromFri, toFri, maxHours);

        report(reportForm);
    }
    //============================================================================================

    /**
     * This function using the RestApi to set the report attempt to be approved by the user.
     * @param reportForm an object that contains the report data to be sent to the server.
     */
    private void report(ReportForm reportForm) {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<ResponseMessage> call = restApi.report(reportForm);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMessage> secondCall, @NonNull Response<ResponseMessage> response) {
                if (!response.isSuccessful()) {
                    if (response.code() != 409) {
                        Utils.showToast(context, getString(R.string.network_error));
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    assert response.errorBody() != null;
                    ResponseMessage responseMessage = Utils.convertJsonToResponseObject(response.errorBody(),
                            ResponseMessage.class);
                    String message = "";
                    switch (Objects.requireNonNull(responseMessage).getDescription()){
                        case "something_went_wrong":
                            message = getString(R.string.something_went_wrong);
                            break;
                        case "already_report":
                            message = getString(R.string.already_report);
                            break;
                    }
                    Utils.showToast(context, message);
                    progressBar.setVisibility(View.INVISIBLE);

                }else{
                    Utils.showToast(context, getString(R.string.report_success));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(context,getString(R.string.network_error));
            }
        });
    }
    //============================================================================================

    /**
     * This function return the user unique id that saved in the sharedPref.
     * @return string that represent the user unique id
     */
    private String getUserUniqueID() {
        return sharedPref.getString(getString(R.string.uniqueID), null);
    }
    //============================================================================================

    /**
     * This function initialize the spinners
     */
    private void initSpinners() {
        String[] items = getDayTimesArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        fromSunThuSpinner.setAdapter(adapter);
        toSunThuSpinner.setAdapter(adapter);
        fromFriSpinner.setAdapter(adapter);
        toFriSpinner.setAdapter(adapter);

        fromSunThuSpinner.setSelection(14);
        fromFriSpinner.setSelection(14);

        setOnItemSelectedListenerToSpinners();

        String[] maxHoursArr = {"NONE", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, maxHoursArr);

        maxHoursSpinner.setAdapter(adapter2);
    }
    //============================================================================================

    /**
     * This function set the on item selected listeners to the spinners
     */
    private void setOnItemSelectedListenerToSpinners() {
        fromSunThuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                disableAllOptionsBeforePos(toSunThuSpinner, position);
                if (position < toSunThuSpinner.getAdapter().getCount() - 1)
                    toSunThuSpinner.setSelection(position + 1);
                else
                    toSunThuSpinner.setSelection(position);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        fromFriSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                disableAllOptionsBeforePos(toFriSpinner, position);
                if (position < toFriSpinner.getAdapter().getCount() - 1)
                    toFriSpinner.setSelection(position + 1);
                else
                    toFriSpinner.setSelection(position);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
    //============================================================================================

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
    //============================================================================================

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
    //============================================================================================

    /**
     * Factory method that returns an Intent for starting the RegisterActivity.
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_REPORT_ACTIVITY);
    }
    //============================================================================================

    /**
     * This function finish the activity.
     * @param view the view
     */
    public void finishActivity(View view) {
        this.finish();
    }
}
