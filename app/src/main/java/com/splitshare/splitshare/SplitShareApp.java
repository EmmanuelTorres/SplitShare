package com.splitshare.splitshare;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by armando on 10/9/17.
 */

public class SplitShareApp extends Application {
    private static GoogleSignInAccount acct;
    public static GoogleSignInAccount getAcct() { return acct; }
    public static void setAcct(GoogleSignInAccount newAcct) { acct=newAcct; }
}
