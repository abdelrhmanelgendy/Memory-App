package com.example.myapplication.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.pojo.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int RC_SIGN_IN = 53;
    Button btnLogin, btn_signUp;
    TextInputEditText et_email, et_pass;
    ActivityLoginBinding loginBinding;
    FirebaseAuth firebaseAuth;
    TextView tv_error;
    CheckInternetConnection internetConnection;
    ConnectivityManager connectivityManager;
    TextView TV_forgetPass;
    ConstraintLayout imageViewLogo;
    LoginButton loginButton;
    CallbackManager mCallbackManager;
    GoogleSignInClient mGoogleClient;
    private static final String TAG = "LoginActivity";
    TextView txtViewConnectionDialog;
    Animation flashingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        faddingTransitoin();
        super.onCreate(savedInstanceState);


        flashingAnimation= AnimationUtils.loadAnimation(this,R.anim.teext_view_flashing);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        txtViewConnectionDialog=(TextView) loginBinding.signInActivityTxtConnectionDialog;
        mCallbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(this);
        init();
        initFaceBookSing();


        btnLogin.setOnClickListener(this);
        btn_signUp.setOnClickListener(this);
        TV_forgetPass.setOnClickListener(this);
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                unSetError();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                unSetError();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initMode() {
        DarkModeHelper.init(this);
        String currentMode = DarkModeHelper.getPersistData();
        if (currentMode.equals(DarkModeHelper.NIGHT_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


    }

    void faddingTransitoin() {
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    private void initFaceBookSing() {

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "pu blic_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.getMessage().toString());
            }


        });
    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: " + firebaseAuth.getCurrentUser().getDisplayName());
                            Log.d(TAG, "onComplete: " + firebaseAuth.getCurrentUser().getPhotoUrl());
                            Log.d(TAG, "onComplete: " + firebaseAuth.getCurrentUser().getEmail());
                            Log.d(TAG, "onComplete: " + firebaseAuth.getCurrentUser().getPhoneNumber());


                            //add user to fireBase
                            addFaceBookDataToFireBase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());


                        }
                    }
                });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    private void init() {
        imageViewLogo = loginBinding.loginActivityIMGLogo;
        btn_signUp = loginBinding.loginActivityBtnSignUp;
        btnLogin = loginBinding.loginActivityBtnLogin;
        et_email = loginBinding.loginActivityETEmailAddress;
        et_pass = loginBinding.loginActivityETPassword;
        firebaseAuth = FirebaseAuth.getInstance();
        tv_error = loginBinding.loginActivityTVError;
        internetConnection = new CheckInternetConnection();
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        TV_forgetPass = loginBinding.btnForgetPassword;

        googleSignInOptions();

        loginBinding.loginActivityBtnGoogleLogin.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InitializeConnectionDialog.endCheck();
    }

    private void googleSignInOptions() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginActivity_btn_login:
                signIn();
                break;
            case R.id.loginActivity_btn_signUp:
                sighUp();
                break;
            case R.id.btn_forgetPassword:
                forgetPassword();
                break;
            case R.id.loginActivity_btn_googleLogin:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle() {
        if (CheckInternetConnection.connection(getApplicationContext())) {
            Intent signInIntent = mGoogleClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else
        {
            txtViewConnectionDialog.setAnimation(flashingAnimation);
            txtViewConnectionDialog.startAnimation(flashingAnimation);
        }
    }

    private void forgetPassword() {

        Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
        startActivity(intent);
    }

    private void sighUp() {


        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, imageViewLogo, ViewCompat.getTransitionName(imageViewLogo));

        if (DarkModeHelper.getCurrentMode().equals(DarkModeHelper.NIGHT_MODE)) {
            startActivity(signUpIntent);
        } else {
            startActivity(signUpIntent, activityOptionsCompat.toBundle());
        }
    }

    private void signIn() {
        if (CheckInternetConnection.connection(this)) {
            String email_address = et_email.getEditableText().toString();
            String password = et_pass.getEditableText().toString();
            if (validateEmailAddress() && validatePassword()) {
                Task<AuthResult> authResultTask = firebaseAuth.signInWithEmailAndPassword(email_address, password);

                authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            OpenUi();
                        } else {
                            if (!internetConnection.connection(connectivityManager)) {
                                tv_error.setVisibility(View.VISIBLE);
                                setError(getResources().getString(R.string.ConnectionCheck));
                            } else {
                                unSetError();
//                            tv_error.setText("");
                                setError(getResources().getString(R.string.worngEmailOrPass));
                            }


                        }
                    }
                });
            }
        }
        else
        {
            txtViewConnectionDialog.setAnimation(flashingAnimation);

            txtViewConnectionDialog.startAnimation(flashingAnimation);
        }
    }



    private void OpenUi() {

        Intent intent = new Intent(LoginActivity.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EmailVerification.EMAIL_ADDRESS_TO_VERIFY, et_email.getText().toString());
        intent.putExtra(EmailVerification.PASSWORD_TO_VERIFY, et_pass.getText().toString());
        startActivity(intent);
        finish();
//        } else if (getIntent().getAction().equals(AddNewMemory.LOGIN_ACTIVITY_LISTED_IMAGE_ACTION)) {
//            String listedImages = getIntent().getStringExtra(AddNewMemory.LOGIN_ACTIVITY_LISTED_IMAGE);
//            Intent intent = new Intent(LoginActivity.this, AddNewMemory.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(AddNewMemory.LOGIN_ACTIVITY_LISTED_IMAGE, listedImages);
//            intent.setAction(AddNewMemory.LOGIN_ACTIVITY_LISTED_IMAGE_ACTION);
//            Log.d("TAG243", "OpenUi: "+listedImages);
//            startActivity(intent);
//            finish();
//        }
    }

    boolean validateEmailAddress() {
        String email = et_email.getEditableText().toString();

        if (email.length() < 6) {

            tv_error.setVisibility(View.VISIBLE);
            setError(getResources().getString(R.string.pleaseEnterAValidEmail));
            return false;
        } else if (!email.contains(".com")) {
            tv_error.setVisibility(View.VISIBLE);
            setError(getResources().getString(R.string.pleaseEnterAValidEmail));
            return false;
        } else {

            return true;

        }

    }

    boolean validatePassword() {
        String password = et_pass.getEditableText().toString();

        if (password.length() < 6) {
            tv_error.setVisibility(View.VISIBLE);
            tv_error.setText(getResources().getString(R.string.pleaseEnterAValidPass));
            return false;
        } else {

            return true;

        }

    }

    public void setError(String errorMessage) {
        tv_error.setVisibility(View.VISIBLE);
        tv_error.setText(errorMessage);

    }

    public void unSetError() {
        tv_error.setVisibility(View.GONE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtViewConnectionDialog,this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    void addFaceBookDataToFireBase(FirebaseUser user) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateCreation = formatter.format(date);
        String name = user.getDisplayName();


        String AccountCreatedDate = dateCreation;
        String email = user.getEmail();
        String PicUrl = user.getPhotoUrl().toString();
        String password = "null";
        String age = "null";
        firebaseDatabase = FirebaseDatabase.getInstance();


        User newUser = new User(name, email, password, age, AccountCreatedDate, PicUrl, firebaseAuth.getCurrentUser().getUid(), new ArrayList<>());
        reference = firebaseDatabase.getReference("UsersData" + "/" + firebaseAuth.getUid());
        reference.setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
        reference = firebaseDatabase.getReference("Emails" + "/" + firebaseAuth.getUid());
        reference.setValue(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
        reference = firebaseDatabase.getReference("UserNames" + "/" + firebaseAuth.getUid());
        reference.setValue(name)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            User user = new User(name, email, password, age, AccountCreatedDate, PicUrl,
//                            firebaseAuth.getCurrentUser().getUid(), new ArrayList<>());

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            saveUserDataToFireBase(user);

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void saveUserDataToFireBase(FirebaseUser user) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateCreation = formatter.format(date);
        String AccountCreatedDate = dateCreation;

        User newUser = new User(user.getDisplayName(), user.getEmail(), "", "18", AccountCreatedDate, user.getPhotoUrl().toString(), user.getUid(), new ArrayList<>());
        reference = firebaseDatabase.getReference("UsersData" + "/" + firebaseAuth.getUid());
        reference.setValue(newUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference = firebaseDatabase.getReference("Emails" + "/" + firebaseAuth.getUid());
                            reference.setValue(user.getEmail())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });
                            reference = firebaseDatabase.getReference("UserNames" + "/" + firebaseAuth.getUid());
                            reference.setValue(user.getDisplayName())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                openDashBoard();

                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void openDashBoard() {

        Intent intent = new Intent(LoginActivity.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
