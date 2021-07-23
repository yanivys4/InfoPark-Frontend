package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.infopark.R;
import com.example.infopark.Utils.IntroViewPagerAdapter;
import com.example.infopark.Utils.ScreenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class IntroActivity extends AppCompatActivity {
    private static final String ACTION_INTRO_ACTIVITY =
            "android.intent.action.ACTION_INTRO_ACTIVITY";
    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private TabLayout tabIndicator;
    private Button nextButton;
    private  Button getStartedButton;
    Animation buttonAnimation;
    private List<ScreenItem> screenList;
    private int position;
    private SharedPreferences sharedPref;


    //============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Context context = IntroActivity.this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_intro);
        position = 0;
        initializeViews();
        setupViewPager();
        setUpButtonAndIndicator();
    }
    //============================================================================================
    private void initializeViews(){

        screenPager = findViewById(R.id.screen_view_pager);
        nextButton = findViewById(R.id.next_button);
        nextButton.setText(getString(R.string.next));
        getStartedButton = findViewById(R.id.get_started_button);
        getStartedButton.setText(getString(R.string.get_started));
        tabIndicator = findViewById(R.id.tab_indicator);
        buttonAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

    }
    //============================================================================================
    private void setupViewPager(){
        // set up view pager
        screenList = new ArrayList<>();

        screenList.add(new ScreenItem(getString(R.string.welcome),"",R.drawable.infopark_logo));
        screenList.add(new ScreenItem(getString(R.string.click_for_info),getString(R.string.get_info_description),R.drawable.big_get_info_ic));
        screenList.add(new ScreenItem(getString(R.string.save_my_location),getString(R.string.save_location_description),R.drawable.big_save_ic));
        screenList.add(new ScreenItem(getString(R.string.add_new_info),getString(R.string.report_description),R.drawable.big_report_ic));
        // Go to last page if language is Hebrew

        introViewPagerAdapter = new IntroViewPagerAdapter(this,screenList);

        screenPager.setAdapter(introViewPagerAdapter);
        //screenPager.setCurrentItem(screenList.size() -1);
        // setup tabLayout with ViewPager
        tabIndicator.setupWithViewPager(screenPager);
    }
    //============================================================================================
    private void  loadLastScreen(){
        nextButton.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        getStartedButton.setVisibility(View.VISIBLE);
        getStartedButton.setAnimation(buttonAnimation);

    }
    //============================================================================================

    private void setUpButtonAndIndicator(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if(position < screenList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if(position == screenList.size()-1){
                    loadLastScreen();
                }
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent loginActivity = LoginActivity.makeIntent();
                 startActivity(loginActivity);
                 setIntroOpenedTrue();
                 finish();

            }
        });
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabIndicator.getSelectedTabPosition() == screenList.size()-1){
                    loadLastScreen();
                }
                else{
                    nextButton.setVisibility(View.VISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);
                    getStartedButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /**
     * This function set the firstUse shared pref variable to status
     */
    private void setIntroOpenedTrue(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.introOpened), true);
        editor.apply();
    }

    public static Intent makeIntent(){return new Intent(ACTION_INTRO_ACTIVITY);}
}
