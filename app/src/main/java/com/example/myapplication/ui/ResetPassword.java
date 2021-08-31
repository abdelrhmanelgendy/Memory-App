package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.dialogs.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference reference;
    TextInputEditText txtMail;
    Button btnReset;
    TextView txtError;
    private static final String TAG = "ResetPassword";
    final ArrayList<String> emailList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    TextView txtConnectionError;
    Animation flashingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
        btnReset.setOnClickListener(this);
        txtMail.addTextChangedListener(new TextWatcher() {
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

    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.resetActivity_btnReset) {
            if (CheckInternetConnection.connection(this)) {
                if (txtMail.getText().length() < 8) {
                    setError(getResources().getString(R.string.wrongEmailFormat));
                    return;

                }
                if (!txtMail.getText().toString().contains("@") || !txtMail.getText().toString().contains(".com")) {
                    setError(getResources().getString(R.string.wrongEmailFormat));
                    return;

                }


                progressDialog = new ProgressDialog(ResetPassword.this, false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 4000);
                getOldEmails();

            }
        } else {
            txtConnectionError.setAnimation(flashingAnimation);

            txtConnectionError.startAnimation(flashingAnimation);
        }

    }

    void getOldEmails() {
        reference = database.getReference("Emails");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot emails : snapshot.getChildren()) {

                    emailList.add(emails.getValue(String.class));

                }
                allEmails(emailList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void allEmails(ArrayList<String> emailList) {
        String userMail = txtMail.getEditableText().toString();

        boolean contains = emailList.contains(userMail);
        if (contains) {
            firebaseAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPassword.this, getResources().getString(R.string.pleasecheckyourMail), Toast.LENGTH_SHORT).show();
                        Intent signIntent = new Intent(ResetPassword.this, LoginActivity.class);
                        signIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(signIntent);


                    } else {

                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        if (task.getException().toString().trim().equals("com.google.firebase.FirebaseTooManyRequestsException: We have blocked all requests from this device due to unusual activity. Try again later.".trim())) {
                            setError(getResources().getString(R.string.Wehaveblockedallrequests));
                        }

                    }
                }
            });

        } else {
            progressDialog.dismiss();
            setError(getResources().getString(R.string.enteredemaildoesntexist));
        }
    }

    private void init() {
        database = FirebaseDatabase.getInstance();
        txtMail = findViewById(R.id.resetActivity_emailAdress);
        btnReset = findViewById(R.id.resetActivity_btnReset);
        txtError = findViewById(R.id.resetActivity_TVError);
        txtConnectionError = findViewById(R.id.activity_resetPassword_txtConnectionDialog);
        firebaseAuth = FirebaseAuth.getInstance();
        flashingAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing);

    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtConnectionError, this);

    }

    public void setError(String errorMessage) {
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(errorMessage);

    }

    public void unSetError() {
        txtError.setVisibility(View.GONE);


    }


    public boolean checkEmailExists(String userEmail) {

        boolean contains = emailList.contains(userEmail);
        if (contains) {
            return true;
        } else {
            return false;
        }

    }
}