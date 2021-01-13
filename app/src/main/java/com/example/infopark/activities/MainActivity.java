package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.infopark.R;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    /**
     * Name of the Intent Action that wills start this Activity.
     */
    private static final String ACTION_MAIN_ACTIVITY =
            "android.intent.action.ACTION_MAIN_ACTIVITY";

    private Button logOutButton;

    private MapFragment m_mapFragment;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.logout_button);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        PlacesClient placesClient = Places.createClient(this);
        m_mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if(m_mapFragment != null){
            m_mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean(getString(R.string.loggedIn), false);
        if(isLoggedIn){
            logOutButton.setVisibility(View.VISIBLE);
        }else{
            logOutButton.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        float lat = 31;
        float lng = 31;

        LatLng home = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(home)
                .title("Marker in home"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
    }

    public static Intent makeIntent(){
        return new Intent(ACTION_MAIN_ACTIVITY);
    }


    public void logOut(View view) {
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), false);
        editor.apply();

        Intent intent = LoginActivity.makeIntent();
        startActivity(intent);
        finish();
    }
}