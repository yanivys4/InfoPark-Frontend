package com.example.infopark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    private TextView sunThuHours;
    private TextView friHours;
    private TextView maxHours;
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
        responseInfo = getIntent().getExtras().getParcelable("responseInfo");
        showInformation();
    }
    //============================================================================================

    /**
     * Initialize the views.
     */
    private void initializeViews(){
        sunThuHours = findViewById(R.id.sun_thu_hours);
        friHours = findViewById(R.id.fri_hours);
        maxHours = findViewById(R.id.max_hours);
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
        sunThuHours.setText(String.format("%s->%s", responseInfo.getFromSunThu(), responseInfo.getToSunThu()));
        friHours.setText(String.format("%s->%s", responseInfo.getFromFri(), responseInfo.getToFri()));
        maxHours.setText(responseInfo.getMaxHours());
    }
}
