package com.example.infopark.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.infopark.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.RESTApi.RequestSavedLocation;
import com.example.infopark.RESTApi.ResponseInfo;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This activity is the main activity of the app.
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Name of the Intent Action that wills start this Activity.
     */
    private static final String ACTION_MAIN_ACTIVITY =
            "android.intent.action.ACTION_MAIN_ACTIVITY";
    private static final String TAG = MainActivity.class.getSimpleName();
    // View members
    private Button saveLocationButton;
    private Button reportButton;
    private Button logOutInButton;
    private TextView searchButton;
    private EditText searchInput;
    private boolean searchMode;
    private int searchTag = 0;
    private ProgressBar progressBar;

    private Context context;
    private SharedPreferences sharedPref;

    private GoogleMap map;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(31.7586, 35.1629);
    private static final int DEFAULT_ZOOM = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // The simple representation of the last known  location via Latitude and Longitude
    private LatitudeLongitude currentLocation;
    private LatitudeLongitude searchLocation;
    private LatitudeLongitude savedLocation;
    Marker savedLocationMarker;
    Marker searchLocationMarker;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g.,
     * the function initialize the location services, the map component and the
     * sign in status.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        super.onCreate(savedInstanceState);

        //loadLocale();
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
    //============================================================================================

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        saveLocationButton = findViewById(R.id.save_my_location_button);
        reportButton = findViewById(R.id.report_button);
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        logOutInButton = findViewById(R.id.logout_login_button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
    //============================================================================================

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
    //============================================================================================

    /**
     * this function sets the basic visualization of the search input and  initialize the on click
     * method for search.
     */
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
                    resourceId = context.getResources().getIdentifier("done_button",
                            "drawable", context.getPackageName());
                } else {
                    // make buttons gray
                    if(geoLocate()){
                        searchMode = true;
                        changeButtonsColor(false);
                    }
                    searchInput.setVisibility(View.GONE);
                    searchInput.setText("");
                    searchTag = 0;
                    resourceId = context.getResources().getIdentifier("search_button",
                            "drawable", context.getPackageName());

                }
                searchButton.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                        resourceId, null));
            }
        });
    }
    //============================================================================================

    /**
     * this function search a location (LatitudeLongitude) according to the string
     * in the searchInput. if a location was found the searchLocation is updated.
     * @return boolean
     *      return true if a location was found.
     */
    private boolean geoLocate() {

        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchInput.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        // there is at least one search match
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
            return true;
        } else {
            Utils.showToast(MainActivity.this, getString(R.string.address_not_exist));
            return false;
        }
    }
    //============================================================================================

    /**
     * This function handles the logged in status when the activity is first created. the function
     * changes the log in/out button and accordingly the tag of the button.
     */
    private void handleIsLoggedIn() {
        boolean isLoggedIn = getIsLoggedIn();

        if (isLoggedIn) {
            logOutInButton.setTag(1); // 1 means logout button appears
            logOutInButton.setText(getString(R.string.log_out));

        } else {
            logOutInButton.setTag(0); // 0 means login buttons appears
            logOutInButton.setTextColor(getColor(R.color.green));
            logOutInButton.setText(getString(R.string.log_in));
            // valid false means the color to change to is gray
            changeButtonsColor(false);
        }
    }
    //============================================================================================

    /**
     * this function changes the buttons in the page according to the param given.
     * @param valid boolean
     *              true for blue when the buttons are valid and false for gray.
     *
     */
    private void changeButtonsColor(boolean valid) {
        int resourceId;
        if (valid) {
            resourceId = context.getResources().getIdentifier("button_blue_background",
                    "drawable", context.getPackageName());
        } else {
            resourceId = context.getResources().getIdentifier("button_gray_background",
                    "drawable", context.getPackageName());
        }
        saveLocationButton.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                resourceId, null));
        reportButton.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                resourceId, null));
    }
    //============================================================================================

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param map the google map that should be worked with.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;

        //add location button click listener
        map.setOnMyLocationButtonClickListener(() -> {
            // change to blue only if also user logged in
            if (getIsLoggedIn()) {
                changeButtonsColor(true);
            }
            searchMode = false;
            return false;
        });
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // if user is logged in try to get saved location and out it on the map.
        if (getIsLoggedIn()) {
            getSavedLocation();
        }
        // Get the current location of the device and set the position on the map.
        getDeviceLocation(false,false);
    }
    //============================================================================================

    /**
     * Gets the current location of the device, and positions the map's camera.
     * @param saveLocation boolean
     *                     if true that means the get device location is used  as a service
     *                     before save the current location of the device.
     *@param retrieveInfo boolean
     *                     if true that means the get device location is used as a service
     *                     before retrieve info according to current location.
     */
    private void getDeviceLocation(boolean saveLocation, boolean retrieveInfo) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
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
                                setSavedLocation();
                            }

                            if(retrieveInfo){
                                retrieveInfo(currentLocation);
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
                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }
    //============================================================================================

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
    //============================================================================================

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;

            } else {
                System.out.println("permission not granted");
            }
        }
        updateLocationUI();
    }
    //============================================================================================

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
                if(!searchMode && getIsLoggedIn())
                changeButtonsColor(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                changeButtonsColor(false);

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    //============================================================================================

    /**
     * Factory method that returns an Intent for starting the MainActivity.
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_MAIN_ACTIVITY);
    }
    //============================================================================================

    /**
     * on click function that sets the is logged in status of the user to false if the button is
     * on "log out" mode, start the login activity and finish this activity.
     * @param view
     *          the view
     */
    public void logOutIn(View view) {
        // the button is in log out mode
        if ((Integer) logOutInButton.getTag() == 1) {
            // update log out status
            setIsLoggedInFalse();
        }
        Intent intent = LoginActivity.makeIntent();
        startActivity(intent);
        finish();
    }
    //============================================================================================

    /**
     * on click function of the save my location button.
     * @param view
     *      the view.
     */
    public void saveMyLocation(View view) {

        if (searchMode) {
            Utils.showToast(MainActivity.this,getString(R.string.cant_save_searched_location));
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));
        }else if(!locationPermissionGranted){
            Utils.showToast(MainActivity.this, getString(R.string.permission_not_granted));
        }
        else {
            // get the device location with savLocation parameter true so the setSavedLocation method
            // will trigger.
            getDeviceLocation(true,false);
        }
    }
    //============================================================================================

    /**
     * on click function of the add new info(report) button.
     * @param view
     */
    public void reportNewInfo(View view) {
        if (searchMode) {
            Utils.showToast(MainActivity.this, getString(R.string.go_back_to_live));
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));

        } else if (savedLocation.getLatitude() == -100) {
            Utils.showToast(MainActivity.this, getString(R.string.save_location_first));
        }
        else if(!locationPermissionGranted){
            Utils.showToast(MainActivity.this, getString(R.string.permission_not_granted));
        }
        else{
            Intent startIntent = ReportActivity.makeIntent();
            startActivity(startIntent);
        }
    }
    //============================================================================================

    /**
     * This function request a saved location from the backend with the user unique id.
     * if the saved location is not the default value (-100) the function updated the map with
     * the blue car sign.
     */
    private void getSavedLocation() {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);

        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(getUserUniqueID(), null);
        Call<SavedLocation> call = restApi.getSavedLocation(requestSavedLocation);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<SavedLocation>() {

            @Override
            public void onResponse(@NonNull Call<SavedLocation> call, @NonNull Response<SavedLocation> response) {
                progressBar.setVisibility(View.INVISIBLE);
                SavedLocation responseSavedLocation = response.body();

                if(responseSavedLocation != null){
                    savedLocation = responseSavedLocation.getSavedLocation();
                    // when a user is first initialized the default values of the location is -1.
                    // therefore there is still no saved location
                    if (savedLocation.getLatitude() != -100) {
                        setSavedLocationMarker();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<SavedLocation> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });

    }

    //============================================================================================

    /**
     * This function sets the current saved location on the backend and the saved location marker
     * as well.
     */
    private void setSavedLocation() {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(getUserUniqueID(), currentLocation);
        Call<ResponseMessage> call = restApi.setSavedLocation(requestSavedLocation);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseMessage>() {

            @Override
            public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(MainActivity.this, getString(R.string.network_error));
                    return;
                } else {
                    ResponseMessage responseMessage = response.body();
                    assert responseMessage != null;
                    Utils.showToast(MainActivity.this, getString(R.string.location_saved));
                    if (responseMessage.getSuccess()) {
                        savedLocation.setLocation(currentLocation.getLatitude(),currentLocation.getLongitude());
                        setSavedLocationMarker();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });
    }
    //============================================================================================

    /**
     * This function sets the marker of the saved location.
     */
    private void setSavedLocationMarker() {
        if (savedLocationMarker != null) {
            savedLocationMarker.remove();
        }

        savedLocationMarker = map.addMarker(
                new MarkerOptions()
                        .position(new LatLng(savedLocation.getLatitude(),
                                savedLocation.getLongitude()))
                        .title("saved location")
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.little_car)));

    }
    //============================================================================================

    /**
     * This function sets the marker of the saved location.
     */
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
    //============================================================================================

    /**
     * This function returns the boolean value of isLoggedIn flag.
     * @return boolean
     *      the is logged in value.
     */
    private boolean getIsLoggedIn() {
        return sharedPref.getBoolean(getString(R.string.loggedIn), false);
    }
    //============================================================================================

    /**
     * This function sets the isLoggedIn flag to false.
     */
    private void setIsLoggedInFalse() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), false);
        editor.apply();
    }
    //============================================================================================

    /**
     * This function gets the userUniqueId value.
     * @return string
     *      the userUniqueId.
     */
    private String getUserUniqueID() {
        return sharedPref.getString(getString(R.string.uniqueID), null);
    }
    //============================================================================================
    private void retrieveInfo(LatitudeLongitude location){

        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(null,location);
        Call<ResponseInfo> call = restApi.getInfo(requestSavedLocation);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseInfo>() {

            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(MainActivity.this, getString(R.string.network_error));
                    return;
                } else {
                    ResponseInfo responseInfo = response.body();
                    if(!responseInfo.getSuccess()){
                        Utils.showToast(MainActivity.this,getString(R.string.no_info));
                    }else{
                        // start the info activity with the results got from the backend
                        startInfoActivity(responseInfo);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });
    }
    /**
     * on click function of the get info button.
     * the function retrieve the relevant info from the backend according to the current location
     * or the searched location if in search mode.
     * @param view
     */
    public void retrieveInfoOnClick(View view) {

        if(searchMode){
            retrieveInfo(searchLocation);
        }else{
            getDeviceLocation(false,true);
        }

    }
    //============================================================================================

    /**
     * This function starts the info activity after it  encapsulates the responseInfo parameter.
     * @param responseInfo
     *      the responseInfo given that has the info got after communication with the backend.
     */
    private void startInfoActivity(ResponseInfo responseInfo){
        Intent startIntent = InfoActivity.makeIntent();
        startIntent.putExtra("responseInfo",responseInfo);
        startActivity(startIntent);
    }
}