package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivitySignUpBinding;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.dialogs.ProgressDialog;
import com.example.myapplication.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding activitySignUpBinding;
    TextInputEditText txtUserName, txtPassword, txtEmail, txtAge;
    TextView tv_error;
    Button btnSigmUp;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    public static final String EMAIL_ADDRESS = "user_email";
    TextView txtViewConnectioDialog;
    Animation flashingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        faddingTransitoin();
        super.onCreate(savedInstanceState);
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        init();
        btnSigmUp.setOnClickListener(this);

    }

    private void init() {
        txtViewConnectioDialog = (TextView) activitySignUpBinding.SignUpActivityTxtViewConnectioDialog;
        txtUserName = activitySignUpBinding.SignUpActivityETUserName;
        txtPassword = activitySignUpBinding.loginActivityETPassword;
        txtEmail = activitySignUpBinding.SignUpActivityETEmailAddress;
        txtAge = activitySignUpBinding.SignUpActivityETAge;
        btnSigmUp = activitySignUpBinding.SignUpActivityBtnSignUp;
        firebaseAuth = FirebaseAuth.getInstance();
        tv_error = activitySignUpBinding.signUpActivityTVError;
        flashingAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing);
    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtViewConnectioDialog, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignUpActivity_btn_signUp) {
            if (validateEmail() && validateuserName() && validatePassword() && validateAge()) {

                signUp();
                unSetError();


            }
        }
    }

    private void signUp() {
        if (CheckInternetConnection.connection(getApplicationContext())) {
            String emailAddress = txtEmail.getEditableText().toString();
            String password = txtPassword.getEditableText().toString();
            Task<AuthResult> userWithEmailAndPassword = firebaseAuth.createUserWithEmailAndPassword(emailAddress, password);
            userWithEmailAndPassword.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        addUserToDataBase();

                    } else {
                        setError(task.getException().toString());

                    }
                }
            });
        } else {
            txtViewConnectioDialog.setAnimation(flashingAnimation);
            txtViewConnectioDialog.startAnimation(flashingAnimation);
        }

    }

    private void addUserToDataBase() {
        ProgressDialog dialog = new ProgressDialog(this, false);
        dialog.show();
        String useremail = txtEmail.getEditableText().toString();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent signIn = new Intent(SignUpActivity.this, DashBoard.class);
                signIn.putExtra(EMAIL_ADDRESS, useremail);
                signIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signIn);
                finish();
            }
        }, 3000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateCreation = formatter.format(date);

        String name = txtUserName.getEditableText().toString();
        String password = txtPassword.getEditableText().toString();
        String age = txtAge.getEditableText().toString();
        String AccountCreatedDate = dateCreation;
        String email = txtEmail.getEditableText().toString();
        String PicUrl = "";
        firebaseDatabase = FirebaseDatabase.getInstance();


        User user = new User(name, email, password, age, AccountCreatedDate, PicUrl, firebaseAuth.getCurrentUser().getUid(), new ArrayList<>());
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
                            Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
//                            firebaseAuth.signOut();
                        }
                    }
                });


    }


    private boolean emailExists(String email) {
        return false;
    }

    private boolean userNameExists(String userName) {
        return false;
    }


    private boolean validateEmail() {
        String email = txtEmail.getEditableText().toString();
        if (email.length() < 6) {
            setError("please enter a valid email address");
            return false;
        }
        if (!email.contains(".com") | !email.contains("@")) {
            setError("please enter a valid email address");
            return false;
        } else {
            if (emailExists(email)) {
                setError("email already exists");
                return false;
            } else {

                return true;
            }
        }


    }

    private boolean validatePassword() {
        String password = txtPassword.getEditableText().toString();
        if (password.length() < 6) {
            setError("passord must be bigger than 6 characters");
            return false;
        }

        return true;

    }

    private boolean validateuserName() {
        String userName = txtUserName.getEditableText().toString();
        if (userName.length() < 5) {
            setError("please enter a valid name");
            return false;
        } else {
            if (userNameExists(userName)) {
                setError("user name already exists try another one");
                return false;
            } else {
                return true;

            }
        }


    }

    private boolean validateAge() {
        String age = txtAge.getEditableText().toString();

        if (age.length() < 1) {
            setError("please enter your age");
            return false;
        } else {
            int userAge = Integer.parseInt(age);
            if (userAge < 6 || userAge > 70) {
                setError("please enter a valid age");
                return false;
            }
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

    void faddingTransitoin() {
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

}