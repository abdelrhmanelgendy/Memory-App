package com.example.myapplication.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseConstants {
  public   static FirebaseUser getfirebaseUser()

    {
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser;
    }
}
