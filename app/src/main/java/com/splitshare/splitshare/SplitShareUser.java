package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    // These values are all given by the Google sign in account
    // The unique userId of the user
    private String accountId;
    // The name of the user
    private String name;
    // The unique email of the user
    private String email;

    // OPTIONAL: Photo URL from GoogleSignInAccount.getPhotoUrl()?

    // Sets the data and initiates an accountReference
    public SplitShareUser()
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/");
        this.accountId = SplitShareApp.acct.getId();
        this.name = SplitShareApp.acct.getDisplayName();
        this.email = SplitShareApp.acct.getEmail();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    /*
     * Creates an account for the current user of the app
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

        // This query returns DataSnapshots of the 'users' table that have our accountId
        Query query = accountReference.child(accountId);

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
                    userData.put("UserID", accountId);
                    userData.put("Name", name);
                    userData.put("Email", email);

                    // Sets the above entries as the values for the user
                    accountReference.child(accountId).setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void addGroup(final String groupId, final String groupName)
    {
        // If the database reference is null, the connection to the server isn't good
        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("Account", "The database reference was null");

            return;
        }

        // This query returns DataSnapshots of the 'users' table that have our accountId
        Query query = accountReference.child(accountId);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    DataSnapshot groupToAdd = dataSnapshot.child("Groups").child(groupId);

                    if (!groupToAdd.exists())
                    {
                        Log.d("Account", "Adding group " + groupId + " to user " + accountId);

                        groupToAdd.getRef().setValue(groupName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
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
        Query query = accountReference.child(accountId);

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
                        Log.d("Account", "Removing group " + groupId + " from user " + accountId);

                        groupToAdd.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
