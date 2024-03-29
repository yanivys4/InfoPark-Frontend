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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.infopark.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.RESTApi.LoginResponse;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This activity is the main activity of the app.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    private NavigationView sideNavigationView;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Context context;
    private SharedPreferences sharedPref;

    private GoogleMap map;
    private SupportMapFragment m_mapFragment;
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

        context = MainActivity.this;

        setContentView(R.layout.activity_main);
        initializeViews();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        // The entry point to the Places API.
        PlacesClient placesClient = Places.createClient(this);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        m_mapFragment = (SupportMapFragment) getSupportFragmentManager()
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

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    //============================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //============================================================================================
    /**
     * Initialize the views.
     */
    private void initializeViews() {

        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getBaseContext().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.INVISIBLE);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        sideNavigationView = findViewById(R.id.side_navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setItemTextAppearanceActive(R.style.bottom_selected_text);
        bottomNavigationView.setItemTextAppearanceInactive(R.style.bottom_normal_text);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.save_my_location_button) {
                    saveMyLocation();
                }
                if (item.getItemId() == R.id.get_info_button) {
                    retrieveInfo();
                }
                if (item.getItemId() == R.id.report_button) {
                    reportNewInfo();
                }
                return true;
            }
        });
        sideNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.login_logout){
                    logOutIn();
                }
                else if(itemId == R.id.help){
                    Intent startIntro = IntroActivity.makeIntent();
                    startActivity(startIntro);
                }
                return true;
            }
        });

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
        searchInput.setVisibility(View.INVISIBLE);
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
                    if (geoLocate()) {
                        searchMode = true;
                        //changeButtonsColor(false);
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
     *
     * @return boolean
     * return true if a location was found.
     */
    private boolean geoLocate() {

        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchInput.getText().toString();

        Geocoder geocoder = new Geocoder(context);
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
            setSearchLocationMarker(true);
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

            sideNavigationView.getMenu().getItem(0).setTitle(getBaseContext().getString(R.string.log_out));
            sideNavigationView.getMenu().getItem(0).setIcon(AppCompatResources.getDrawable(getBaseContext(),R.drawable.ic_logout));
        } else {
            sideNavigationView.getMenu().getItem(0).setTitle(getBaseContext().getString(R.string.log_in));
            sideNavigationView.getMenu().getItem(0).setIcon(AppCompatResources.getDrawable(getBaseContext(),R.drawable.ic_login));
        }
    }
    //============================================================================================

    /**
     * this function changes the buttons in the page according to the param given.
     *
     * @param valid boolean
     *              true for blue when the buttons are valid and false for gray.
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

        ImageView imageView = ((ImageView)m_mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton"));
        imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.my_location_button));
        imageView.requestLayout();
        imageView.getLayoutParams().height = 140;
        imageView.getLayoutParams().width = 140;

        //add location button click listener
        map.setOnMyLocationButtonClickListener(() -> {
            // change to blue only if also user logged in
            if (getIsLoggedIn()) {
                changeButtonsColor(true);
            }
            searchMode = false;
            if (searchLocationMarker != null) {
                searchLocationMarker.remove();
            }
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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                searchMode = true;
                if (searchLocation == null) {
                    searchLocation = new LatitudeLongitude(latLng.latitude, latLng.longitude);
                } else {
                    searchLocation.setLocation(latLng.latitude, latLng.longitude);
                }
                setSearchLocationMarker(false);
                changeButtonsColor(false);
            }
        });

    }
    //============================================================================================

    /**
     * Gets the current location of the device, and positions the map's camera.
     *
     * @param saveLocation boolean
     *                     if true that means the get device location is used  as a service
     *                     before save the current location of the device.
     * @param retrieveInfo boolean
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
                                if (savedLocation == null) {
                                    savedLocation = new LatitudeLongitude(-100, -100);
                                }
                                setSavedLocation();
                            }

                            if (retrieveInfo) {
                                retrieveInfo(currentLocation);
                            }
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLocation.getLatitude(),
                                            currentLocation.getLongitude()), DEFAULT_ZOOM));

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                if (!searchMode && getIsLoggedIn())
                    changeButtonsColor(true);
                // Get the current location of the device and set the position on the map.
                getDeviceLocation(false,false);
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
     *
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_MAIN_ACTIVITY);
    }
    //============================================================================================

    /**
     * this function sets the is logged in status of the user to false if the button is
     * on "log out" mode, start the login activity and finish this activity.
     */
    private void logOutIn() {
        // the user is logged in and wants to log out
        if ((getIsLoggedIn())){
            // update log out status
            setIsLoggedInFalse();
        }
        Intent intent = LoginActivity.makeIntent();
        startActivity(intent);
        finish();
    }
    //============================================================================================

    /**
     * save my location function
     *

     */
    private void saveMyLocation() {

        if (searchMode) {
            Utils.showToast(MainActivity.this, getString(R.string.cant_save_searched_location));
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));
        } else if (!locationPermissionGranted) {
            Utils.showToast(MainActivity.this, getString(R.string.permission_not_granted));
        } else {
            // get the device location with savLocation parameter true so the setSavedLocation method
            // will trigger.
            getDeviceLocation(true, false);
        }
    }
    //============================================================================================

    /**
     * add new info function (report)
     *
     */
    private void reportNewInfo() {
        if (searchMode) {
            Utils.showToast(MainActivity.this, getString(R.string.go_back_to_live));
        } else if (!getIsLoggedIn()) {
            Utils.showToast(MainActivity.this, getString(R.string.login_first));

        } else if (savedLocation != null && savedLocation.getLatitude() == -100) {
            Utils.showToast(MainActivity.this, getString(R.string.save_location_first));
        } else if (!locationPermissionGranted) {
            Utils.showToast(MainActivity.this, getString(R.string.permission_not_granted));
        } else {
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

                SavedLocation responseSavedLocation = response.body();
                if (!response.isSuccessful()) {
                    if (response.code() != 409) {
                        Utils.showToast(context, getString(R.string.network_error));
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                }
                if (responseSavedLocation != null) {
                    savedLocation = responseSavedLocation.getSavedLocation();
                    // when a user is first initialized the default values of the location is -100
                    // which is a latitude value that out of range.
                    // therefore there is still no saved location
                    if (savedLocation.getLatitude() != -100) {
                        setSavedLocationMarker();
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
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
                    if ("something_went_wrong".equals(Objects.requireNonNull(responseMessage).getDescription())) {
                        message = getString(R.string.something_went_wrong);
                    }
                    Utils.showToast(context, message);
                } else {
                    ResponseMessage responseMessage = response.body();
                    assert responseMessage != null;
                    Utils.showToast(MainActivity.this, getString(R.string.location_saved));
                    savedLocation.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                    setSavedLocationMarker();
                }
                progressBar.setVisibility(View.INVISIBLE);

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
                        .title(getString(R.string.saved_location))
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.little_car)));

    }
    //============================================================================================

    /**
     * This function sets the marker of the searched location.
     */
    private void setSearchLocationMarker(boolean default_zoom) {
        if (searchLocationMarker != null) {
            searchLocationMarker.remove();
        }
        final LatLng searchLocationLatLng = new LatLng(searchLocation.getLatitude(), searchLocation.getLongitude());
        searchLocationMarker = map.addMarker(
                new MarkerOptions()
                        .position(searchLocationLatLng)
                        .title(getString(R.string.searched_location))
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_marker)));
        float zoom = map.getCameraPosition().zoom;
        if(default_zoom){
            zoom = DEFAULT_ZOOM;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                searchLocationLatLng, zoom));
    }
    //============================================================================================

    /**
     * This function returns the boolean value of isLoggedIn flag.
     *
     * @return boolean
     * the is logged in value.
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
     *
     * @return string
     * the userUniqueId.
     */
    private String getUserUniqueID() {
        return sharedPref.getString(getString(R.string.uniqueID), null);
    }

    //============================================================================================
    private void retrieveInfo(LatitudeLongitude location) {

        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        RequestSavedLocation requestSavedLocation = new RequestSavedLocation(null, location);
        Call<ResponseInfo> call = restApi.getInfo(requestSavedLocation);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseInfo>() {

            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {

                if (!response.isSuccessful()) {
                    if (response.code() != 409) {
                        Utils.showToast(context, getString(R.string.network_error));
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    assert response.errorBody() != null;
                    ResponseInfo responseMessage = Utils.convertJsonToResponseObject(response.errorBody(),
                            ResponseInfo.class);
                    assert responseMessage != null;
                    String message = "";
                    if ("something_went_wrong".equals(responseMessage.getDescription())) {
                        message = getString(R.string.something_went_wrong);
                    } else {
                        message = getString(R.string.no_info);
                    }
                    Utils.showToast(context, message);
                }

                else {
                    ResponseInfo responseInfo = response.body();

                    // start the info activity with the results got from the backend
                    startInfoActivity(responseInfo);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(MainActivity.this, t.getMessage());
            }
        });
    }

    /**
     *
     * the function retrieve the relevant info from the backend according to the current location
     * or the searched location if in search mode.
     */
    private void retrieveInfo() {

        if (searchMode) {
            retrieveInfo(searchLocation);
        } else {
            getDeviceLocation(false, true);
        }

    }
    //============================================================================================

    /**
     * This function starts the info activity after it  encapsulates the responseInfo parameter.
     *
     * @param responseInfo the responseInfo given that has the info got after communication with the backend.
     */
    private void startInfoActivity(ResponseInfo responseInfo) {
        Intent startIntent = InfoActivity.makeIntent();

        startIntent.putExtra("responseInfo", responseInfo);
        startActivity(startIntent);
    }
}