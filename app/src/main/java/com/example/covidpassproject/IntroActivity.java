package com.example.covidpassproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnsignin;
    Button btnsignup;
    Animation btnAnim ;
    TextView tvSkip;

    // Permission variables
    public static final int PERM_REQUEST = 1234;
    ActivityResultLauncher<String[]> mPermResultLauncher;
    public static boolean isLocGranted = false;
    public static boolean isInternetGranted = false;
    public static boolean isCameraGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // when this activity is about to be launch we need to check if its openened before or not
        // Disabling this for now
        // Reinstalling the app every build is very impractical
        // TODO: Build something meaningful in main activity!!!
        //if (restorePrefData()) {
        //    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class );
        //    startActivity(mainActivity);
        //    finish();
        //}

        setContentView(R.layout.activity_intro);


        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnsignin = findViewById(R.id.btn_signin);
        btnsignup = findViewById(R.id.btn_signup);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("QR Code","Scan your Qr Code to Verify that You are Vaccinated",R.raw.qr_code));
        mList.add(new ScreenItem("Nearest Location","Know the nearest Hospital for you to get Vaccinated as Easy as it can be",R.raw.location));
        mList.add(new ScreenItem("Secure Data","No one can Access your Data Except you",R.raw.virus_logo));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);


                }

                if (position == mList.size()-1) { // when we rech to the last screen

                    // TODO : show the GETSTARTED Button and hide the indicator and the next button

                    loaddLastScreen();


                }



            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //open main activity

                Intent signin = new Intent(getApplicationContext(),SignIn.class);
                startActivity(signin);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();



            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //open main activity

                Intent signup = new Intent(getApplicationContext(),SignUP.class);
                startActivity(signup);

                finish();



            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });

        mPermResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if((result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null)) {
                    isLocGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if((result.get(Manifest.permission.INTERNET) != null)) {
                    isInternetGranted = result.get(Manifest.permission.INTERNET);
                }

                if((result.get(Manifest.permission.CAMERA) != null)) {
                    isCameraGranted = result.get(Manifest.permission.CAMERA);
                }
            }
        });
        RequestAllPermissions();
    }

    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;



    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();


    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnsignin.setVisibility(View.VISIBLE);
        btnsignup.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnsignin.setAnimation(btnAnim);
        btnsignup.setAnimation(btnAnim);
    }

    private void RequestAllPermissions() {
        isLocGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        isInternetGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        isCameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        List<String> needed_permissions = new ArrayList<String>();

        if(!isLocGranted) {
            needed_permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(!isInternetGranted) {
            needed_permissions.add(Manifest.permission.INTERNET);
        }

        if(!isCameraGranted) {
            needed_permissions.add(Manifest.permission.CAMERA);
        }

        if(!needed_permissions.isEmpty()) {
            mPermResultLauncher.launch(needed_permissions.toArray(new String[0]));
        }
    }
}
