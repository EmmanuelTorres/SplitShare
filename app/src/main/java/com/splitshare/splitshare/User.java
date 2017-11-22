package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

public class User
{
    // A database reference to this specific user's place in the users/ table
    private DatabaseReference accountReference;

    // The unique userId of the user
    public String userId;
    // The userName of the user
    public String userName;
    // The unique userEmail of the user
    private String userEmail;

    public User() {
        this.userId = "0";
        this.userName = "John Doe";
        this.userEmail = "nobody@broken.com";
    }

    // Sets the data and initiates an accountReference
    public User(String userId)
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/" + userId);

        this.userId = userId;
        this.userName = accountReference.child("Name").toString();
        this.userEmail = accountReference.child("Email").toString();
    }

    // Sets the data and initiates an accountReference
    public User(String userId, String name)
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/" + userId);

        this.userId = userId;
        this.userName = name;
        this.userEmail = "nobody@broken.com";
    }

    public User(String userId, String userName, String userEmail)
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/" + userId);

        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public DatabaseReference getAccountReference()
    {
        return accountReference;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getUserEmail()
    {
        return userEmail;
    }


    /*
     * Removes a group from the user's "Groups" section inside of Firebase
     * Here's the crazy part: we can't check for duplicates but it technically doesn't
     * matter, and Firebase can sometimes tell its a duplicate and not change anything
     */
    public void removeFromGroup(final String groupTimestamp)
    {
        // If the database reference is null, the connection to the server isn't good
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("User-AddToGroup", "The database reference was null");

            return;
        }

        Log.d("User-AddToGroup", "Removing group " + groupTimestamp + " to user " + userName);
        accountReference.child("Groups").child(groupTimestamp).removeValue();
    }
}
