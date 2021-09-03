package com.example.myapplication.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.helpers.LanguageHelper;
import com.example.myapplication.jsonConverter.UserConverter;
import com.example.myapplication.pojo.User;
import com.example.myapplication.ui.ActivityDisplayMode;
import com.example.myapplication.ui.ActivityLanguage;
import com.example.myapplication.ui.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private static final String SETTING_FRAGMENT_USER_DATA_ARGS = "userArgs";
    private static final String USER_DATA_FILE = "userData";
    private static final String USER_PROFILEPICTURE = "profilePicture";
    private static final String USER_NAME = "userName";
    private static final String TAG = "SettingsFragment";
    CircleImageView imageViewProfilePic;
    TextView txtName, txtCurrentLanguage, txtCurrentMode;

    ImageView btnOpenProfile, btnChangeLanguage, btnChangeMode;
    String userData;
    TextView txtViewAccount;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        firebaseAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        transitionAnimation();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void transitionAnimation() {
        getActivity().getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(100));
        getActivity().getWindow().setSharedElementExitTransition(new ChangeBounds().setDuration(100));
        Fade fade = new Fade();

        fade.excludeTarget(R.id.activitySettings_txtViewCurrentLanguage, true);
        fade.excludeTarget(R.id.activitySettings_txtViewCurrentMode, true);
        fade.excludeTarget(R.id.activitySettings_ModeButton, true);
        fade.excludeTarget(R.id.activitySettings_personalInfoButton, true);
        fade.excludeTarget(R.id.activitySettings_languageButton, true);
        setExitTransition(fade);
        setEnterTransition(fade);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (CheckInternetConnection.connection(getContext())) {
            getUserDataFromFireBase();
        } else {
            getStoredUserDataFromSharedPrefrence(getContext());

        }
    }

    private void getUserDataFromFireBase() {
        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(firebaseAuth.getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot != null) {
                            User currentUserData = snapshot.getValue(User.class);
                            storeUserDataIntoSharedPrefrence(getContext(), currentUserData.getUserName(), currentUserData.getProfilePicUrl());
                            putData(currentUserData.getUserName(), currentUserData.getProfilePicUrl());


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + LanguageHelper.getDeviceLanguage());
        getUpdatedData();

    }

    private void getUpdatedData() {
        getStoredUserDataFromSharedPrefrence(getContext());
    }

    public static SettingsFragment getInstance(String user) {

        SettingsFragment userFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SETTING_FRAGMENT_USER_DATA_ARGS, user);
        userFragment.setArguments(bundle);
        return userFragment;
    }


//    private void getUserData() {
//        Bundle arguments = getArguments();
//
//        if (arguments != null && arguments.getString(SETTING_FRAGMENT_USER_DATA_ARGS) != null) {
//            String userStringed = arguments.getString(SETTING_FRAGMENT_USER_DATA_ARGS);
//            User user = UserConverter.getUser(userStringed);
//            userData = userStringed;
//            if (user != null) {
//                putData(user.getUserName(), user.getProfilePicUrl());
////                storeUserDataIntoSharedPrefrence(getContext(), user.getUserName(), user.getProfilePicUrl());
//            }
//        } else {
//            putData(user.getUserName(), user.getProfilePicUrl());
//
////            getStoredUserDataFromSharedPrefrence(getContext());
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.activity_settings, container, false);
        inflateViews(inflate);
        return inflate;
    }

    private void inflateViews(View inflate) {

        txtViewAccount = inflate.findViewById(R.id.activitySettings_txtViewAccount);
        imageViewProfilePic = inflate.findViewById(R.id.activitySettings_profileImage);
        txtName = inflate.findViewById(R.id.activitySettings_txtViewName);
        txtCurrentLanguage = inflate.findViewById(R.id.activitySettings_txtViewCurrentLanguage);
        txtCurrentMode = inflate.findViewById(R.id.activitySettings_txtViewCurrentMode);
        btnChangeMode = inflate.findViewById(R.id.activitySettings_ModeButton);

        btnOpenProfile = inflate.findViewById(R.id.activitySettings_personalInfoButton);
        btnChangeLanguage = inflate.findViewById(R.id.activitySettings_languageButton);
        btnOpenProfile.setOnClickListener(this);
        btnChangeMode.setOnClickListener(this);
        btnChangeLanguage.setOnClickListener(this);


        DarkModeHelper.init(getContext());
        String currentMode = DarkModeHelper.getCurrentMode();
        Log.d("TAG21", "getCurrentMode: " + currentMode);
        if (currentMode.equals(DarkModeHelper.LIGHT_MODE)) {
            txtCurrentMode.setText(getResources().getString(R.string.Day));
        } else if (currentMode.equals(DarkModeHelper.NIGHT_MODE)) {
            txtCurrentMode.setText(getResources().getString(R.string.Night));
        }
        if (LanguageHelper.getDeviceLanguage().equals(LanguageHelper.ARABIC_CODE)) {
            txtCurrentLanguage.setText("العربيه");
        } else if (LanguageHelper.getDeviceLanguage().equals(LanguageHelper.ENGLISH_CODE)) {
            txtCurrentLanguage.setText("English");
        }

    }

    void putData(String name, String profileUrl) {
        txtName.setText(name);
        if (profileUrl.equals("")) {
            imageViewProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));

        } else {
            if (getContext()!=null) {
                Glide.with(getContext())
                        .load(profileUrl)
                        .into(imageViewProfilePic);
            }
        }

    }

    private void storeUserDataIntoSharedPrefrence(Context context, String name, String profileUrl) {
        if (context!=null) {
            Log.d(TAG, "storeUserNameIntoSharedPrefrence: " + name + "  " + profileUrl);
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(USER_NAME, name);
            edit.putString(USER_PROFILEPICTURE, profileUrl);
            edit.apply();
        }
    }

    private void getStoredUserDataFromSharedPrefrence(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(USER_NAME, "-1");
        String profileUrl = sharedPreferences.getString(USER_PROFILEPICTURE, "-1");
        if (userName != null) {
            putData(userName, profileUrl);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activitySettings_personalInfoButton:
                openProfile();
                break;

            case R.id.activitySettings_ModeButton:
                openChangeMode();
                break;

            case R.id.activitySettings_languageButton:
                openChangeLanguage();
                break;

        }
    }

    private void openChangeLanguage() {
        Intent profileIntent = new Intent(getActivity(), ActivityLanguage.class);
        startActivity(profileIntent);
    }

    private void openChangeMode() {

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), btnChangeMode, "modeShangeName");
        Intent profileIntent = new Intent(getActivity(), ActivityDisplayMode.class);
        startActivity(profileIntent);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openProfile() {

        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair(imageViewProfilePic, getResources().getString(R.string.profilePictureTransitionName));
        pairs[1] = new Pair(txtViewAccount, getResources().getString(R.string.AccountTransitionName));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
        Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
        profileIntent.putExtra(UserProfileActivity.USER_FRAGMENT_USER_DATA_ARGS, userData);
        if (DarkModeHelper.getCurrentMode().equals(DarkModeHelper.NIGHT_MODE)) {
            startActivity(profileIntent);
        } else {
            startActivity(profileIntent, options.toBundle());
        }
    }
}