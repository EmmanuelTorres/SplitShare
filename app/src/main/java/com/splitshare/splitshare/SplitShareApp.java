package com.splitshare.splitshare;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by armando on 10/9/17.
 */

public class SplitShareApp extends Application {
    public static GoogleSignInAccount acct;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static GoogleApiClient mGoogleApiClient;
}
