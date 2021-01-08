package com.example.infopark.activities;

import android.content.Intent;
import android.os.Bundle;
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


    private MapFragment m_mapFragment;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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



}