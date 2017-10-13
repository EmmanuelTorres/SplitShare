package com.splitshare.splitshare;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by armando on 10/9/17.
 * Edited by Emmanuel on 13 October 2017
 */

public class SplitShareUser
{
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
    public SplitShareUser(DatabaseReference accountReference)
    {
        this.accountReference = accountReference;
        this.accountId = SplitShareApp.acct.getId();
        this.name = SplitShareApp.acct.getDisplayName();
        this.email = SplitShareApp.acct.getEmail();
    }

    public SplitShareUser(String i, String n)
    {
        accountId = i;
        name = n;
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

        // A listener is needed to check if an account already exists
        accountReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the account already exist, log it but don't change anything
                if (dataSnapshot.hasChild(accountId))
                {
                    Log.d("Account", "The account already exists");
                }
                else
                {
                    // Create an account since it doesn't exist at this point
                    Log.d("Account", "A new user account is created");

                    // Entries that will exist inside the new user entry
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("Name", name);
                    userData.put("Email", email);

                    // Sets the above entries as the values for the user
                    accountReference.setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Returns true if the database contains an entry with the accountId
        return true;
    }
}
