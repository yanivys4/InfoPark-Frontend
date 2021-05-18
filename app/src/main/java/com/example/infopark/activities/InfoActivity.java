package com.example.infopark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.infopark.R;
import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.RESTApi.RequestSavedLocation;
import com.example.infopark.RESTApi.ResponseInfo;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.Utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This activity shows the information requested according to the current or manually
 * searched location.
 */
public class InfoActivity extends AppCompatActivity {

    private static final String ACTION_INFO_ACTIVITY =
            "android.intent.action.ACTION_INFO_ACTIVITY";
    private static final String TAG = InfoActivity.class.getSimpleName();

    // Views members
    private TableLayout regularSignTable;
    private TextView sunThuHours;
    private TextView friHours;
    private TableLayout maxHoursTable;
    private TextView maxHours;
    private TableLayout regionalSignTable;
    private TextView fromToRegional;
    private TextView parkingSignNumber;
    private TableLayout unloadingChargingTable;
    private TextView fromToUnloadingCharging;
    private TableLayout weightLimitTable;
    private TextView weightLimit;
    private ResponseInfo responseInfo;

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g.,
     * the function gets the information that has been sent via the intent created this activity.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        initializeViews();
        hideAllInfoFields();
        responseInfo = (ResponseInfo) getIntent().getSerializableExtra("responseInfo");
        showInformation();
    }
    //============================================================================================

    /**
     * Initialize the views.
     */
    private void initializeViews(){
        regularSignTable = findViewById(R.id.regularTableLayout);
        maxHoursTable = findViewById(R.id.maxHoursTable);
        regionalSignTable = findViewById(R.id.regionalSignTable);
        unloadingChargingTable = findViewById(R.id.unloadingChargingTable);
        weightLimitTable = findViewById(R.id.weightLimitTable);
        sunThuHours = findViewById(R.id.sun_thu_hours);
        friHours = findViewById(R.id.fri_hours);
        maxHours = findViewById(R.id.max_hours);
        fromToRegional = findViewById(R.id.from_to_regional);
        parkingSignNumber = findViewById(R.id.parking_sign_number);
        fromToUnloadingCharging = findViewById(R.id.from_to_unloading_charging);
        weightLimit = findViewById(R.id.weight_limit);
    }
    //============================================================================================
    /**
     * Factory method that returns an Intent for starting the InfoActivity.
     * @return Intent
     */
    public static Intent makeIntent(){
        return new Intent(ACTION_INFO_ACTIVITY);
    }
    //============================================================================================

    /**
     * on click function that is related to the back button and finish the activity.
     * @param view the view
     */
    public void finishActivity(View view) {
        this.finish();
    }
    //============================================================================================

    /**
     * This function stores the information in the layout text containers.
     */
    private void showInformation(){
        if(responseInfo.getRegularSignInfo()!=null){
            regularSignTable.setVisibility(View.VISIBLE);
            sunThuHours.setText(String.format("%s --> %s", responseInfo.getRegularSignInfo()
                    .getFromSunThu(), responseInfo.getRegularSignInfo().getToSunThu()));
            friHours.setText(String.format("%s --> %s", responseInfo.getRegularSignInfo().
                    getFromFri(), responseInfo.getRegularSignInfo().getToFri()));
        }
        if(responseInfo.getHoursLimitInfo()!=null){
            maxHoursTable.setVisibility(View.VISIBLE);
            maxHours.setText(responseInfo.getHoursLimitInfo().getMaxHours());
        }
        if(responseInfo.getRegionalParkingSignInfo() != null){
            regionalSignTable.setVisibility(View.VISIBLE);
            fromToRegional.setText(String.format("%s --> %s", responseInfo.getRegionalParkingSignInfo()
                    .getFromRegionalSign(), responseInfo.getRegionalParkingSignInfo().getToRegionalSign()));
            parkingSignNumber.setText(responseInfo.getRegionalParkingSignInfo().getParkingSignNumber());

        }
        if(responseInfo.getUnloadingChargingInfo()!=null){
            unloadingChargingTable.setVisibility(View.VISIBLE);
            fromToUnloadingCharging.setText(String.format("%s --> %s", responseInfo.getUnloadingChargingInfo()
                    .getFromUnloadingCharging(), responseInfo.getUnloadingChargingInfo().getToUnloadingCharging()));
        }
        if(responseInfo.getWeightLimitInfo()!=null){
            weightLimitTable.setVisibility(View.VISIBLE);
            weightLimit.setText(responseInfo.getWeightLimitInfo().getMaxWeight());
        }

    }

    private void hideAllInfoFields() {
        regularSignTable.setVisibility(View.GONE);
        maxHoursTable.setVisibility(View.GONE);
        regionalSignTable.setVisibility(View.GONE);
        unloadingChargingTable.setVisibility(View.GONE);
        weightLimitTable.setVisibility(View.GONE);
    }
}
