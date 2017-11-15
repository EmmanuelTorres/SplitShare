package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    /*
     * Adds a group to the users table for the User
     */
    public void addGroupToUser(final String groupTimestamp, final String groupId)
    {
        // If the database reference is null, the connection to the server isn't good
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("Account", "The database reference was null");

            return;
        }

        // A query that looks for the person who is being added to a group
        Query query = accountReference.child("Groups");

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Get the users/userId/groups/groupId table
                DataSnapshot groupToAdd = dataSnapshot.child(groupTimestamp);

                // If the person doesn't already have this group in their Groups table
                if (!groupToAdd.exists())
                {
                    // Add them
                    Log.d("Account", "Adding group " + groupId + " to " + userName);

                    groupToAdd.getRef().setValue(groupId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
