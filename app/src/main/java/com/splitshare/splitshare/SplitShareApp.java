package com.splitshare.splitshare;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by armando on 10/9/17.
 */

public class SplitShareApp extends Application {
    public static GoogleSignInAccount acct;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseDatabase firebaseDatabase;
    public static GoogleApiClient mGoogleApiClient;
    public static SplitShareUser splitShareUser;
    public static ArrayList<Group> usersGroups = new ArrayList<Group>();
    public static ArrayList<MasterTask> usersMasterTasks = new ArrayList<MasterTask>();
    public static ArrayList<Task> populatedTask = new ArrayList<Task>();
}
