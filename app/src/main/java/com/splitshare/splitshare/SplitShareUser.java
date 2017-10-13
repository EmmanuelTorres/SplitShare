package com.splitshare.splitshare;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;

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

    // The reference to the Firebase database
    private DatabaseReference accountReference;

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
     * accountReference - Used to interact with the database (input data, retrieve data)
     */
    public SplitShareUser(GoogleSignInAccount googleSignInAccount,
                          DatabaseReference accountReference)
    {
        this.googleSignInAccount = googleSignInAccount;
        this.accountReference = accountReference;
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

    public void setAccountReference(DatabaseReference accountReference)
    {
        this.accountReference = accountReference;
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

    public DatabaseReference getAccountReference()
    {
        return accountReference;
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
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("Account", "The database reference was null");

            return false;
        }

        // TODO: Check if account exists

        // If the database is not null and the account doesn't exist, we create a new
        // HashMap to enter data into Firebase
        // The values we hold are AccountID, Name, and Email
        HashMap<String, String> userData = new HashMap<>();
        userData.put("Name", name);
        userData.put("Email", email);

        accountReference.setValue(userData);

        // Returns true if the database contains an entry with the accountId
        return true;
    }
}
