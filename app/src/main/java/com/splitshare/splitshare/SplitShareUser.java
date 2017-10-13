package com.splitshare.splitshare;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by armando on 10/9/17.
 * Edited by Emmanuel on 13 October 2017
 */

public class SplitShareUser
{
    // The account used to sign in through Google's secure authentication
    // This does NOT hold the password
    private GoogleSignInAccount googleSignInAccount;

    private FirebaseDatabase firebaseDatabase;

    // The unique Google account id of the person who signed in
    private String accountId;
    // The name from the Google account id
    private String name;
    // The email belonging to the Google account
    private String email;

    // OPTIONAL: Photo URL from GoogleSignInAccount.getPhotoUrl()?

    /*
     * googleSignInAccount - Used to assign a unique user id using Google's ids
     * and assign a name to the user
     * firebaseDatabase - Used to interact with the database (input data, retrieve data)
     *
     */
    public SplitShareUser(GoogleSignInAccount googleSignInAccount,
                          FirebaseDatabase firebaseDatabase)
    {
        this.googleSignInAccount = googleSignInAccount;
        this.firebaseDatabase = firebaseDatabase;
        this.accountId = googleSignInAccount.getId();
        this.name = googleSignInAccount.getDisplayName();
        this.email = googleSignInAccount.getEmail();
    }

    public SplitShareUser(String i, String n)
    {
        accountId = i;
        name = n;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount)
    {
        this.googleSignInAccount = googleSignInAccount;
    }

    public void setFirebaseDatabase(FirebaseDatabase firebaseDatabase)
    {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public GoogleSignInAccount getGoogleSignInAccount()
    {
        return googleSignInAccount;
    }

    public FirebaseDatabase getFirebaseDatabase()
    {
        return firebaseDatabase;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public String getName()
    {
        return name;
    }

    public boolean createAccount()
    {
        // If the database reference is null, the connection to the server isn't good
        if (firebaseDatabase == null)
        {
            // Log the mistake for easy debugging
            Log.d("Account", "The database reference was null");

            return false;
        }

        if (firebaseDatabase.getReference("users/" + accountId) != null)
        {
            Log.d("Account", "The account is already created");

            return true;
        }

        HashMap<String, String> userData = new HashMap<>();
        userData.put("AccountID", accountId);
        userData.put("Name", name);
        userData.put("Email", email);

        firebaseDatabase.getReference("users/" + accountId).setValue(userData);

        return true;
    }
}
