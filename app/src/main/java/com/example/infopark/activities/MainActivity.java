package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.infopark.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.RESTApi.RequestSavedLocation;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.RESTApi.SavedLocation;
import com.example.infopark.Utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap map;
    private Button saveLocationButton;
    private Button reportButton;
    private Button logOutInButton;
    private TextView searchButton;
    private EditText searchInput;
    private boolean searchMode;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(31.7586, 35.1629);

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // The simple representation of the last known location location via Latitude and Longitude
    private LatitudeLongitude currentLocation;
    private LatitudeLongitude searchLocation;
    Marker savedLocationMarker;
    Marker searchLocationMarker;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    /**
     * Name of the Intent Action that wills start this Activity.
     */
    private static final String ACTION_MAIN_ACTIVITY =
            "android.intent.action.ACTION_MAIN_ACTIVITY";
    private Context context;
    private SharedPreferences sharedPref;

    private int searchTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

         */

        super.onCreate(savedInstanceState);

        context = MainActivity.this;

        setContentView(R.layout.activity_main);
        initializeViews();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        // The entry point to the Places API.
        PlacesClient placesClient = Places.createClient(this);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        MapFragment m_mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (m_mapFragment != null) {
            m_mapFragment.getMapAsync(this);
        }

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        handleIsLoggedIn();
        handleSearch();
    }

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        saveLocationButton = findViewById(R.id.save_my_location_button);
        reportButton = findViewById(R.id.report_button);
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        logOutInButton = findViewById(R.id.logout_login_button);
    }

    private void handleSearch() {
        searchInput.setVisibility(View.GONE);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resourceId;
                // the search button is closed
                if (searchTag == 0) {
                    searchInput.setVisibility(View.VISIBLE);
                    searchTag = 1;
                    resourceId = context.getResources().getIdentifier("done_button", "drawable", context.getPackageName());
                } else {
                    searchMode = true;
                    // make buttons gray
                    changeButtonsColor(false);
                    // temporary

                    geoLocate();
                    searchInput.setVisibility(View.GONE);
                    searchTag = 0;
                    resourceId = context.getResources().getIdentifier("search_button", "drawable", context.getPackageName());

                }
                searchButton.setBackground(ResourcesCompat.getDrawable(context.getResources(), resourceId, null));
            }

        });
    }


    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchInput.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            double addressLatitude = address.getLatitude();
            double addressLongitude = address.getLongitude();

            if (searchLocation == null) {
                searchLocation = new LatitudeLongitude(addressLatitude, addressLongitude);
            } else {
                searchLocation.setLocation(addressLatitude, addressLongitude);
            }
            setSearchLocationMarker();
        } else {
            Utils.showToast(MainActivity.this, "Address not exist!");
        }
    }

    private void handleIsLoggedIn() {
        boolean isLoggedIn = getIsLoggedIn();

        if (isLoggedIn) {
            logOutInButton.setTag(1); // 1 is logout button
            logOutInButton.setText(getString(R.string.log_out));

        } else {
            logOutInButton.setTag(0); // 0 is login button
            logOutInButton.setTextColor(getColor(R.color.green));
            logOutInButton.setText(getString(R.string.log_in));
            // valid false which mean the color to change to is gray
            changeButtonsColor(false);
        }
    }

    private void changeButtonsColor(boolean valid) {
        int resourceId;
        if (valid) {
            resourceId = context.getResources().getIdentifier("button_blue_background", "drawable", context.getPackageName());
        } else {
            resourceId = context.getResources().getIdentifier("button_gray_background", "drawable", context.getPackageName());
        }
        saveLocationButton.setBackground(ResourcesCompat.getDrawable(context.getResources(), resourceId, null));
        reportButton.setBackground(ResourcesCompat.getDrawable(context.getResources(), resourceId, null));
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        //add location button click listener
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // change to blue only if also user logged in
                if (getIsLoggedIn()) {
                    changeButtonsColor(true);
                }
                searchMode = false;
                return false;
            }
        });
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // if user is logged in try to get saved location and out it on the map.
        if (getIsLoggedIn()) {
            getSavedLocation();
        }
        // Get the current location of the device and set the position of the map.
        getDeviceLocation(false);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation(boolean saveLocation) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        System.out.println("getDeviceLocation triggered");
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {

                                if (currentLocation == null) {
                                    // first initialized
                                    currentLocation = new LatitudeLongitude(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                } else {
                                    currentLocation.setLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                }

                                if (saveLocation) {
                                    setSavedLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                                }
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;

                } else {
                    System.out.println("premission not granted");
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static Intent makeIntent() {
        return new Intent(ACTION_MAIN_ACTIVITY);
    }

    public void logOut(View view) {

        final int status = (Integer) view.getTag();
        if (status == 1) {
            // update log out status
            setIsLoggedInFalse();
        }
        Intent intent = LoginActivity.makeIntent();
        startActivity(intent);
        finish();
    }

    private boolean getIsLoggedIn() {
        return sharedPref.getBoolean(getString(R.string.loggedIn), false);
    }

    private String getUserUniqueID() {
        return sharedPref.getString(getString(R.string.uniqueID), null);
    }

    private void setIsLoggedInFalse() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), false);
        editor.apply();
    }

    public void saveMyLocation(View view) {

        if (searchMode) {
            Utils.showToast(MainActivity.this, "can't save searched location");
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));
        } else {
            getDeviceLocation(true);
        }
    }

    private void setSavedLocationMarker(double latitude, double longitude) {
        if (savedLocationMarker != null) {
            savedLocationMarker.remove();
        }
        final LatLng savedLocation = new LatLng(latitude, longitude);
        savedLocationMarker = map.addMarker(
                new MarkerOptions()
                        .position(savedLocation)
                        .title("saved location")
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.little_car)));

    }

    private void setSearchLocationMarker() {
        if (searchLocationMarker != null) {
            searchLocationMarker.remove();
        }
        final LatLng searchLocationLatLng = new LatLng(searchLocation.getLatitude(), searchLocation.getLongitude());
        searchLocationMarker = map.addMarker(
                new MarkerOptions()
                        .position(searchLocationLatLng)
                        .title("searched location")
                        .alpha(0.7f));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                searchLocationLatLng, DEFAULT_ZOOM));
    }

    private void getSavedLocation() {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);

        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(getUserUniqueID(), null);
        Call<SavedLocation> call = restApi.getSavedLocation(requestSavedLocation);
        call.enqueue(new Callback<SavedLocation>() {

            @Override
            public void onResponse(@NonNull Call<SavedLocation> call, @NonNull Response<SavedLocation> response) {
                SavedLocation responseSavedLocation = response.body();

                assert responseSavedLocation != null;
                LatitudeLongitude savedLocation = responseSavedLocation.getSavedLocation();
                // when a user is first initialized the default values of the location is -1.
                // therefore there is still no saved location
                if (savedLocation.getLatitude() != -100) {
                    setSavedLocationMarker(savedLocation.getLatitude(), savedLocation.getLongitude());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SavedLocation> call, Throwable t) {
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });

    }

    private void setSavedLocation(double latitude, double longitude) {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(getUserUniqueID(), new LatitudeLongitude(latitude, longitude));
        Call<ResponseMessage> call = restApi.setSavedLocation(requestSavedLocation);
        call.enqueue(new Callback<ResponseMessage>() {

            @Override
            public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
                if (!response.isSuccessful()) {
                    Utils.showToast(MainActivity.this, "Code:" + response.code());
                    return;
                } else {
                    ResponseMessage responseMessage = response.body();
                    Utils.showToast(MainActivity.this, responseMessage.getDescription());
                    if (responseMessage.getSuccess()) {
                        setSavedLocationMarker(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, Throwable t) {
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });
    }

    public void reportNewInfo(View view) {
        if (searchMode) {
            Utils.showToast(MainActivity.this, "please go back to live location first");
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));
        } else {
            Intent startIntent = ReportActivity.makeIntent();
            startActivity(startIntent);
        }
    }
}