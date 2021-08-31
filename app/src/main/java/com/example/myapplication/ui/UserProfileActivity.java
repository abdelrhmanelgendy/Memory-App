package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.helpers.FireBaseConstants;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.jsonConverter.MemoryConverter;
import com.example.myapplication.pojo.CurrentLocation;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAGF51";
    public static final String USER_FRAGMENT_USER_DATA_ARGS = "userArgs";
    private static final String USER_DATA_FILE = "userData";
    private static final String USER_PROFILEPICTURE = "profilePicture";
    private static final String USER_NAME = "userName";
    private static final int PICK_IMAGE_REQ_CODE = 71;
    private static final int READ_EXTERNAL_STORAGE_REQ = 13;
    CircleImageView imgProfile;
    ProgressBar circularProgressBar;

    TextView txtChangeImage;
    TextInputEditText Et_txtName, Et_txtOldPass, Et_txtNewPass;
    Button btnLogout;
    ImageView btnDone, btnCancel;
    User userData;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    String oldUserName;
    Uri imgUri;
    StorageTask uploadTask;
    String OldimgUri;
    Resources resources;
    TextView txtViewDialogConnection;
    Animation flashingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);
        resources = getResources();
        transitionAnimation();
        initViews();
        initVars();
        flashingAnimation = AnimationUtils.loadAnimation(this, R.anim.teext_view_flashing);

        getUserData();
        circularProgressBar.setProgress(0);
        circularProgressBar.setVisibility(View.INVISIBLE);
    }


    private void transitionAnimation() {
//        getWindow().setSharedElementExitTransition(new ChangeBounds().setDuration(400));
//        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        Fade fade = new Fade();
        fade.excludeTarget(R.id.userFragment_ET_name, false);
        fade.excludeTarget(R.id.userFragment_ET_NewPassword, false);
        fade.excludeTarget(R.id.userFragment_ET_oldPassword, false);
        fade.excludeTarget(R.id.userFragment_btn_done, false);
        fade.excludeTarget(R.id.userFragment_btn_logout, false);
        fade.excludeTarget(R.id.userFragment_btn_cancel, false);
        fade.excludeTarget(R.id.userFragment_TV_changePhoto, false);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);


    }

    private void getUserData() {
        Intent intent = getIntent();

//        if (intent != null && intent.getStringExtra(USER_FRAGMENT_USER_DATA_ARGS) != null) {
//            String userStringed = intent.getStringExtra(USER_FRAGMENT_USER_DATA_ARGS);
//            User user = UserConverter.getUser(userStringed);
//            if (user != null) {
//                putData(user.getUserName(), user.getProfilePicUrl());
//            }
//        } else {
        getStoredUserDataFromSharedPrefrence(this);
//        }
    }

    private void initVars() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    private void initViews() {
        btnCancel = findViewById(R.id.userFragment_btn_cancel);
        imgProfile = findViewById(R.id.userFragment_imgProfilePic);
        circularProgressBar = findViewById(R.id.userFragment_progressBarImage);
        txtChangeImage = findViewById(R.id.userFragment_TV_changePhoto);
        Et_txtName = findViewById(R.id.userFragment_ET_name);
        Et_txtOldPass = findViewById(R.id.userFragment_ET_oldPassword);
        Et_txtNewPass = findViewById(R.id.userFragment_ET_NewPassword);
        btnLogout = findViewById(R.id.userFragment_btn_logout);
        btnDone = findViewById(R.id.userFragment_btn_done);
        txtViewDialogConnection = findViewById(R.id.userProfile_dailogConnection);

        txtChangeImage.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtViewDialogConnection, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.userFragment_btn_done:
                updateUserData();
                break;
            case R.id.userFragment_btn_cancel:
                userCancelOp();

                break;
            case R.id.userFragment_btn_logout:
                logOut();
                break;
            case R.id.userFragment_imgProfilePic:
                openUserPicture();
                break;
            case R.id.userFragment_TV_changePhoto:
                changeProfilePicture();
                break;
        }

    }

    private void openUserPicture() {
        if (userData != null && !userData.getProfilePicUrl().equals("")) {
            int randomi = new Random(100).nextInt(50);
            Log.d(TAG, "openUserPicture: " + OldimgUri);
            Memory memory = new Memory("ProfilePicture" + randomi, "", new CurrentLocation(0, 0), new ArrayList<ImageToUpload>(), OldimgUri, 0, "", 0);
            String s = new MemoryConverter().convertToGson(memory);
            Intent intent = new Intent(UserProfileActivity.this, ShowMainImageFromMemoryViewer.class);
            intent.putExtra(MemoryViewer.JGSONED_MEMORY, s);
            startActivity(intent);
        }
    }

    private void userCancelOp() {

        if (DarkModeHelper.getCurrentMode().equals(DarkModeHelper.LIGHT_MODE)) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    private void changeProfilePicture() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQ);
        } else {
            openGallery();
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
    }

    private void updateUserData() {
        if (CheckInternetConnection.connection(this)) {
            if (userNameChanged()) {
                //update UserName
                updateUserName();
            }
            if (userProfilePictureChanged()) {
                //update UserName
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Uploadinprogresspleasewait), Toast.LENGTH_SHORT).show();
                } else {
                    updateUserPicture();
                }
            }
            if (passwordChanged()) {
                changePassword();
            }

        } else {
            txtViewDialogConnection.setAnimation(flashingAnimation);
            txtViewDialogConnection.startAnimation(flashingAnimation);

        }
    }

    private void changePassword() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        String oldPassword = Et_txtOldPass.getText().toString();
        String newPassword = Et_txtNewPass.getText().toString();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        firebaseAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.Passwordchanged), Toast.LENGTH_SHORT).show();
                                updateUserPassword(newPassword);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.wrongpassword), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateUserPassword(String newPassword) {
        firebaseDatabase.getReference("UsersData").child(FireBaseConstants.getfirebaseUser().getUid()).child("password")
                .setValue(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userCancelOp();
                        } else {
                            String exceptionMessage = task.getException().getMessage();

                        }
                    }
                });

    }

    private boolean passwordChanged() {
        if (Et_txtOldPass.getText().toString().length() > 0) {
            if (Et_txtOldPass.getText().toString().length() < 6) {
                Et_txtOldPass.setError("wrong old password");
                return false;
            }
            if (Et_txtNewPass.getText().toString().length() < 6) {
                Et_txtNewPass.setError("new password should be at least 6 chars");
                return false;
            }
        }

        if (Et_txtOldPass.getText().toString().length() > 0 && Et_txtNewPass.getText().toString().length() > 0) {
            return true;
        }

        return false;
    }

    private void updateUserPicture() {


        String fileName = "userProfilePicture";

        StorageReference userData = firebaseStorage.getReference(FireBaseConstants.getfirebaseUser().getUid()).child("userData")
                .child(fileName);
        uploadTask = userData.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(), resources.getString(R.string.imagesaved), Toast.LENGTH_SHORT).show();
                circularProgressBar.setProgress(0);
                circularProgressBar.setVisibility(View.INVISIBLE);
                updateUserProfilePicturData();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                long totalByteCount = snapshot.getTotalByteCount();
                long bytesTransferred = snapshot.getBytesTransferred();
                int progress = (int) ((bytesTransferred * 100) / totalByteCount);
                Log.d(TAG, "onProgress: " + progress);
                if (circularProgressBar.getVisibility() == View.INVISIBLE) {
                    circularProgressBar.setVisibility(View.VISIBLE);
                }
                circularProgressBar.setProgress(progress);


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }
            }
        });


    }

    private void updateUserProfilePicturData() {
        firebaseStorage.getReference(FireBaseConstants.getfirebaseUser().getUid())
                .child("userData")
                .child("userProfilePicture")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                firebaseDatabase.getReference("UsersData").child(FireBaseConstants.getfirebaseUser().getUid()).child("profilePicUrl")
                        .setValue(uri.toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    imgUri = null;
                                    OldimgUri = uri.toString();
                                    storeUserDataIntoSharedPrefrence(getApplicationContext(), oldUserName, uri.toString());


                                } else {
                                    String exceptionMessage = task.getException().getMessage();
                                }
                            }
                        });
            }
        });


    }

    private boolean userProfilePictureChanged() {
        if (imgUri == null) {
            return false;
        }
        return true;
    }

    private void updateUserName() {
        String newUserName = Et_txtName.getText().toString();
        firebaseDatabase.getReference("UsersData").child(FireBaseConstants.getfirebaseUser().getUid()).child("userName")
                .setValue(newUserName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            oldUserName = newUserName;
                            storeUserNameIntoSharedPrefrence(getApplicationContext(), newUserName);
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.usernamechanged), Toast.LENGTH_SHORT).show();
                        } else {
                            String exceptionMessage = task.getException().getMessage();
                        }
                    }
                });

    }

    private boolean userNameChanged() {
        String newUserName = Et_txtName.getText().toString();
        if (newUserName.equals(oldUserName)) {
            return false;
        }
        return true;
    }

    private void logOut() {
        firebaseAuth.signOut();
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signInIntent);

    }

    private void getStoredUserDataFromSharedPrefrence(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(USER_NAME, "-1");
        String profileUrl = sharedPreferences.getString(USER_PROFILEPICTURE, "-1");
        if (userName != null) {
            putData(userName, profileUrl);
        }
    }

    private void storeUserDataIntoSharedPrefrence(Context context, String name, String profileUrl) {
        Log.d(TAG, "storeUserNameIntoSharedPrefrence: " + name + "  " + profileUrl);
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(USER_NAME, name);
        edit.putString(USER_PROFILEPICTURE, profileUrl);
        edit.apply();
    }

    private void storeUserNameIntoSharedPrefrence(Context context, String name) {
        Log.d(TAG, "storeUserNameIntoSharedPrefrence: " + name);
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(USER_NAME, name);
        edit.apply();
    }

    void putData(String name, String profileUrl) {
        OldimgUri = profileUrl;
        oldUserName = name;
        Et_txtName.setText(name);
        if (profileUrl.equals("")) {
            imgProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));

        } else {
            Glide.with(getApplicationContext()).load(profileUrl).placeholder(getResources().getDrawable(R.drawable.ic_profile)).into(imgProfile);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.pleaseacceptpermissiontochoosefromgallery), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    imgUri = data.getData();
                    imgProfile.setImageURI(imgUri);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (DarkModeHelper.getCurrentMode().equals(DarkModeHelper.LIGHT_MODE)) {
            finishAfterTransition();
        } else {
            finish();
        }

    }

}