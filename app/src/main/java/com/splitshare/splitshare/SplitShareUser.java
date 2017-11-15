package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * This class is responsible for holding the information of the person who is currently
 * using the SplitShare application
 */
public class SplitShareUser
{
    // A reference to the users table of Firebase
    // Used as a shortcut but not necessary
    private DatabaseReference accountReference;

    // The unique userId of the user
    private String userId;
    // The userName of the user
    private String userName;
    // The unique userEmail of the user
    private String userEmail;

    // OPTIONAL: Photo URL from GoogleSignInAccount.getPhotoUrl()?

    // Sets the data and initiates an accountReference
    public SplitShareUser()
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/");
        this.userId = SplitShareApp.acct.getId();
        this.userName = SplitShareApp.acct.getDisplayName();
        this.userEmail = SplitShareApp.acct.getEmail();
    }

    /*
     * Creates an account for the person who is using the Android application
     * Function is not static because we should not be able to create accounts without Firebase
     * authentication.
     */
    public void createAccount()
    {
        // If the database reference is null
        if (accountReference == null)
        {
            // The connection to the server isn't good
            Log.d("Account", "The database reference was null");

            return;
        }

        // Returns a query checking to see if the person exists within the database
        Query query = accountReference.child(userId);

        // We need a listener if we want to actually check our query
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the account doesn't already exist
                if (!dataSnapshot.exists())
                {
                    Log.d("Account", "A new user account is created");

                    // Create an account with these entries in the form of
                    // KEY:VALUE
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("UserID", userId);
                    userData.put("Name", userName);
                    userData.put("Email", userEmail);

                    // Sets the above entries as the values for the user
                    accountReference.child(userId).setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
        Query query = accountReference.child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If there exists a person with that user id
                if (dataSnapshot.exists())
                {
                    // Get the users/userId/groups/groupId table
                    DataSnapshot groupToAdd = dataSnapshot.child("Groups").child(groupTimestamp);

                    // If the person doesn't already have this group in their Groups table
                    if (!groupToAdd.exists())
                    {
                        // Add them
                        Log.d("Account", "Adding group " + groupId + " to " + userName);

                        groupToAdd.getRef().setValue(groupId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void removeGroup(final String groupId)
    {
        // If the database reference is null, the connection to the server isn't good
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("Account", "The database reference was null");

            return;
        }

        // This query returns DataSnapshots of the 'users' table that have our accountId
        Query query = accountReference.child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    DataSnapshot groupToAdd = dataSnapshot.child("Groups").child(groupId);

                    if (groupToAdd.exists())
                    {
                        Log.d("Account", "Removing group " + groupId + " from user " + userId);

                        groupToAdd.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public ArrayList<Group> getGroups()
    {
        return null;
    }
}
