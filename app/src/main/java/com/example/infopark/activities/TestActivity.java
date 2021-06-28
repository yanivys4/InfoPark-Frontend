package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.infopark.R;
import com.example.infopark.Utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TestActivity extends AppCompatActivity {
    private static final String ACTION_TEST_ACTIVITY =
            "android.intent.action.ACTION_TEST_ACTIVITY";
    private static final String TAG = TestActivity.class.getSimpleName();
    private final Context context = TestActivity.this;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setItemTextAppearanceActive(R.style.bottom_selected_text);
        bottomNavigationView.setItemTextAppearanceInactive(R.style.bottom_normal_text);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.save_my_location_button) {
                    Utils.showToast(context, "save location");
                }
                if (item.getItemId() == R.id.get_info_button) {
                    Utils.showToast(context, "get info");
                }
                if (item.getItemId() == R.id.report_button) {
                    Utils.showToast(context, "report");
                }
                return true;
            }
        });
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent makeIntent(){return new Intent(ACTION_TEST_ACTIVITY);}
}
