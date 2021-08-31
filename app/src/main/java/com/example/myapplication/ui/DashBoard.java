package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databases.local.MemoryDao;
import com.example.myapplication.databases.local.MemoryDatabase;
import com.example.myapplication.fragments.FavoriteFragment;
import com.example.myapplication.fragments.HomeFragement;

import com.example.myapplication.fragments.HomeFragmentListener;
import com.example.myapplication.fragments.SettingsFragment;

import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.helpers.LanguageHelper;
import com.example.myapplication.jsonConverter.ListOfMemoryConverter;
import com.example.myapplication.jsonConverter.UserConverter;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.pojo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

public class DashBoard extends AppCompatActivity implements HomeFragmentListener {

    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    FragmentTransaction fragmentTransaction;
    private static final String TAG = "DashBoard";
    Intent splashScreenIntent;
    User currentUser;
    List<Memory> allMemory;
    MemoryDao memoryDao;
    TextView txtViewConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String deviceLanguage = LanguageHelper.getPersistData(getApplicationContext());

        if (deviceLanguage.equals(LanguageHelper.ARABIC_CODE)) {

            LanguageHelper.setLocale(DashBoard.this, LanguageHelper.ARABIC_CODE);


        } else {

            LanguageHelper.setLocale(DashBoard.this, LanguageHelper.ENGLISH_CODE);

        }
        super.onCreate(savedInstanceState);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "memories");
        if (!file.exists()) {
            file.mkdir();

        }
        memoryDao = MemoryDatabase.getInstance(getApplicationContext()).memoryDao();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_dash_board);
        if (getIntent() != null) {
            splashScreenIntent = getIntent();

        }
        init();
        deleteCache(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {

                    case R.id.menu_home:

                        String userData = splashScreenIntent.getStringExtra(SplashActivity.SPLASH_USER_KEY);
                        Log.d(TAG, "onCreate00: " + userData);
                        HomeFragement homeFragement = HomeFragement.getInstance(userData);
                        fragmentTransaction.replace(R.id.dashboardFramLayout, homeFragement);
                        fragmentTransaction.commit();

                        break;

                    case R.id.menue_favorite:
                        if (allMemory == null) {
                            ListOfMemoryConverter listOfMemoryConverter = new ListOfMemoryConverter();
                            String s = listOfMemoryConverter.fromMemoriesToString(allMemory);
                            FavoriteFragment favoriteFragment = FavoriteFragment.newInstance(s);
                            fragmentTransaction.replace(R.id.dashboardFramLayout, favoriteFragment);
                            fragmentTransaction.commit();
                            return true;
                        } else {

                            ListOfMemoryConverter listOfMemoryConverter = new ListOfMemoryConverter();
                            String s = listOfMemoryConverter.fromMemoriesToString(allMemory);
                            FavoriteFragment favoriteFragment = FavoriteFragment.newInstance(s);
                            fragmentTransaction.replace(R.id.dashboardFramLayout, favoriteFragment);
                            fragmentTransaction.commit();
                        }
                        break;

                    case R.id.menue_user:
                        String userData2 = splashScreenIntent.getStringExtra(SplashActivity.SPLASH_USER_KEY);
                        if (userData2 == null) {
                            if (currentUser == null) {
                                Log.d(TAG, "onNavigationItemSelected: " + currentUser);
                                SettingsFragment settingsFragment = new SettingsFragment();
                                fragmentTransaction.replace(R.id.dashboardFramLayout, settingsFragment);
                                fragmentTransaction.commit();
                            } else {
                                Log.d(TAG, "onNavigationItemSelected: " + currentUser);
                                SettingsFragment settingsFragment = SettingsFragment.getInstance(UserConverter.getStringedUser(currentUser));
                                fragmentTransaction.replace(R.id.dashboardFramLayout, settingsFragment);
                                fragmentTransaction.commit();

                            }
                        } else {
                            SettingsFragment settingsFragment = SettingsFragment.getInstance(userData2);
                            fragmentTransaction.replace(R.id.dashboardFramLayout, settingsFragment);
                            fragmentTransaction.commit();
                        }

                        break;
                }
                return true;
            }
        });


    }

    private void init() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String userData = splashScreenIntent.getStringExtra(SplashActivity.SPLASH_USER_KEY);
        Log.d(TAG, "onCreate00: " + userData);
        HomeFragement homeFragement = HomeFragement.getInstance(userData);
        fragmentTransaction.add(R.id.dashboardFramLayout, homeFragement, "");
        fragmentTransaction.commit();

        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.dashboardFramLayout);


    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
        txtViewConnection = findViewById(R.id.dashBoard_dailogConnection);
        InitializeConnectionDialog.changeConnetion(txtViewConnection, getApplicationContext());
//        if (firebaseAuth.getCurrentUser() != null) {
//            if (getIntent().getBooleanExtra("laterV", false)) {
//
//            } else {
//                if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
//                    Intent intent = new Intent(DashBoard.this, EmailVerification.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra(EmailVerification.EMAIL_ADDRESS_TO_VERIFY, getIntent().getStringExtra(EmailVerification.EMAIL_ADDRESS_TO_VERIFY));
//                    intent.putExtra(EmailVerification.PASSWORD_TO_VERIFY, getIntent().getStringExtra(EmailVerification.PASSWORD_TO_VERIFY));
//                    intent.setAction(EmailVerification.PASSWORD_TO_VERIFY);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        }


    }

    boolean isSure = false;

    @Override
    public void onBackPressed() {
        int currentFrgagment = bottomNavigationView.getSelectedItemId();
        if (currentFrgagment != R.id.menu_home) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.dashboardFramLayout, new HomeFragement());
            fragmentTransaction.replace(R.id.dashboardFramLayout, new HomeFragement());
            fragmentTransaction.commit();
        } else {
            if (!isSure) {
                Toast.makeText(this, getResources().getString(R.string.pressAgain), Toast.LENGTH_LONG).show();
                isSure = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSure = false;
                    }
                }, 3000);

            } else {
                finishAffinity();
            }

        }
    }

    public static void deleteCache(Context context) {
        try {
            File file = new File(context.getFilesDir() + "/" + "ResizedImages");
            boolean b = file.delete();
            Log.d(TAG, "deleteCache: " + file.isDirectory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGetUserData(User user) {
        currentUser = user;
        Log.d(TAG, "onNavigationItemSelected1: " + user);


    }

    @Override
    public void onGetAllMemories(List<Memory> memories) {
        allMemory = memories;


    }
}