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
 * This class is for holding information about the single person using the app
 * It provides useful tools for common tasks like setting variables, interacting with
 * the database, etc
 */
public class SplitShareUser
{
    // The reference to the Firebase accounts group
    // -> USERS
    private DatabaseReference accountReference;

    // A list of all the groups the SplitShareUser belong to
    private ArrayList<Integer> groups;

    // The unique Google account id of the person who signed in
    private String accountId;
    // The name from the Google account id
    private String name;
    // The email belonging to the Google account
    private String email;

    // OPTIONAL: Photo URL from GoogleSignInAccount.getPhotoUrl()?

    // Sets the data based on an accountReference
    public SplitShareUser(DatabaseReference accountReference)
    {
        this.accountReference = accountReference;
        this.accountId = SplitShareApp.acct.getId();
        this.name = SplitShareApp.acct.getDisplayName();
        this.email = SplitShareApp.acct.getEmail();
    }

    // Sets the data and initiates an accountReference
    public SplitShareUser()
    {
        this.accountReference = SplitShareApp.firebaseDatabase.getReference("users/");
        this.accountId = SplitShareApp.acct.getId();
        this.name = SplitShareApp.acct.getDisplayName();
        this.email = SplitShareApp.acct.getEmail();
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

    /*
     * Creates an account for the current user of the app
     * Included in this account are their unique user id, name, and email, all
     * come from their Google account
     */
    public void createAccount()
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

        // We need a listener if we want to actually check our query
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the account already exists, log it but don't change anything
                if (dataSnapshot.exists())
                {
                    Log.d("Account", "The account already exists");
                }
                else
                {
                    // Create an account since it doesn't exist at this point
                    Log.d("Account", "A new user account is created");

                    // Entries that will exist inside the new user entry
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
