package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityEmailVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailVerification extends AppCompatActivity {

    ActivityEmailVerificationBinding emailVerificationBinding;
    TextView tv_email, tv_timer;
    Button btn_checkVerification, btn_verifyLater, btnResend;
    FirebaseAuth firebaseAuth;
    public static final String EMAIL_ADDRESS_TO_VERIFY = "emailAddress";
    public static final String ACTION_TO_VERIFY = "actionVerify";
    public static final String PASSWORD_TO_VERIFY = "password";
    String emailAddress, password;
    Intent outPutPasswordAndEMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailVerificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_verification);

        init();
        String emailAddress = firebaseAuth.getCurrentUser().getEmail();

        tv_email.setText(emailAddress);
        sendVerification();
        btn_checkVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmailVerification.this, "sefwef", Toast.LENGTH_SHORT).show();
                if (!getIntent().getAction().equals(ACTION_TO_VERIFY)) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(EmailVerification.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    firebaseAuth.signOut();
                    Task<AuthResult> authResultTask = firebaseAuth.signInWithEmailAndPassword(emailAddress, password);
                    authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                boolean emailVerified = firebaseAuth.getCurrentUser().isEmailVerified();
                                Log.d("TAG101", "onComplete: " + emailVerified);

                            }
                            else
                            {
                                Log.d("TAG101", "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
        btn_verifyLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailVerification.this, DashBoard.class).putExtra("laterV", true));


            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerification();
            }
        });

    }

    private void sendVerification() {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailVerification.this, "we sent a verification email to your account,\nplease check", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("TAG", "onComplete: " + task.getException().getMessage().toString());
                }
            }
        });
    }

    private void init() {
        tv_email = emailVerificationBinding.emailVerificationTVEmail;
        tv_timer = emailVerificationBinding.emailVerificationTVTimer;
        btn_checkVerification = emailVerificationBinding.emailVerificationBtnResend;
        btn_verifyLater = emailVerificationBinding.emailVerificationBtnLater;
        firebaseAuth = FirebaseAuth.getInstance();
        btnResend = emailVerificationBinding.emailVerificationBtnResend;
        if (getIntent() != null) {
            if (getIntent().getAction().equals(ACTION_TO_VERIFY)) {
                password = getIntent().getStringExtra(PASSWORD_TO_VERIFY);
                emailAddress = getIntent().getStringExtra(EMAIL_ADDRESS_TO_VERIFY);

            }
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();

    }
}