package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

public class User
{
    // A database reference to this specific user's place in the users/ table
    private DatabaseReference accountReference;

    // The unique userId of the user
    private String userId;
    // The userName of the user
    private String userName;
    // The unique userEmail of the user
    private String userEmail;

    // Sets the data and initiates an accountReference
    public User(String userId)
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/" + userId);

        this.userId = userId;
        this.userName = accountReference.child("Name").toString();
        this.userEmail = accountReference.child("Email").toString();
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
     * Adds a group to the user's "Groups" section inside of Firebase
     * Here's the crazy part: we can't check for duplicates but it technically doesn't
     * matter, and Firebase can sometimes tell its a duplicate and not change anything
     */
    public void addToGroup(final String groupTimestamp, final String groupId)
    {
        // If the database reference is null, the connection to the server isn't good
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("User-AddToGroup", "The database reference was null");

            return;
        }

        // Adds a group timestamp:id value to the Groups part of the user's table
        // Functionally replaces the commented code below
        Log.d("User-AddToGroup", "Adding group " + groupTimestamp + " to user " + userName);
        accountReference.child("Groups").child(groupTimestamp).setValue(groupId);

//        // A query that looks for the person who is being added to a group
//        Query query = accountReference.child("Groups");
//
//        query.addListenerForSingleValueEvent(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                // Get the users/userId/groups/groupId table
//                DataSnapshot groupToAdd = dataSnapshot.child(groupTimestamp);
//
//                // If the person doesn't already have this group in their Groups table
//                if (!groupToAdd.exists())
//                {
//                    // Add them
//                    Log.d("Account", "Adding group " + groupId + " to " + userName);
//
//                    groupToAdd.getRef().setValue(groupId);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
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
