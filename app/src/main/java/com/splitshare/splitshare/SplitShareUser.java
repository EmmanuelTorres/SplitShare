package com.splitshare.splitshare;

import android.util.Log;

import java.util.HashMap;

public class SplitShareUser extends User
{
    // Initializes a User class with the GoogleSignInAccount from when we signed in to the app
    public SplitShareUser()
    {
        super(SplitShareApp.acct.getId(), SplitShareApp.acct.getDisplayName(), SplitShareApp.acct.getEmail());
    }

    // Initializes a User class with our userId if passed through the parameter
    public SplitShareUser(String userId)
    {
        super(userId);
    }

    // Initializes a User class with our userId, userName, and userEmail values passed through the parameter
    public SplitShareUser(String userId, String userName, String userEmail)
    {
        super(userId, userName, userEmail);
    }

    /*
     * Creates a brand new account for the user
     * Since this doesn't implement any checks due to query-less Firebase interaction, this may
     * delete all groups that a user belongs to
     */
    public void createAccount()
    {
        if (super.getAccountReference() == null)
        {
            // Log the mistake for easy debugging
            Log.d("User-CreateAccount", "The database reference was null");

            return;
        }

        HashMap<String, String> userData = new HashMap<>();
        userData.put("UserID", super.getUserId());
        userData.put("Name", super.getUserName());
        userData.put("Email", super.getUserEmail());

        // Sets the above entries as the values for the user
        super.getAccountReference().setValue(userData);
    }
}
