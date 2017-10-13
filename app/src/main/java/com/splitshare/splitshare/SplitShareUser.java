package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by armando on 10/9/17.
 * Edited by Emmanuel on 13 October 2017
 */

public class SplitShareUser
{
    // The reference to the Firebase accounts group
    // -> USERS
    private DatabaseReference accountReference;

    // The unique Google account id of the person who signed in
    private String accountId;
    // The name from the Google account id
    private String name;
    // The email belonging to the Google account
    private String email;

    // OPTIONAL: Photo URL from GoogleSignInAccount.getPhotoUrl()?

    // accountReference is used for interfacing with the database
    public SplitShareUser(DatabaseReference accountReference)
    {
        this.accountReference = accountReference;
        this.accountId = SplitShareApp.acct.getId();
        this.name = SplitShareApp.acct.getDisplayName();
        this.email = SplitShareApp.acct.getEmail();
    }

    public SplitShareUser(String accountId, String name, String email)
    {
        this.accountId = accountId;
        this.name = name;
        this.email = email;
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
        Query query = accountReference.orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the account already exist, log it but don't change anything
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
                    accountReference.setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Returns true if the database contains an entry with the accountId
        return true;
    }

    public void getGroups()
    {
        /*
         * SQL Search
         * SELECT *
         * FROM groups
         * WHERE group_members CONTAINS "Emmanuel"
         */

        /*
         * SELECT *
         * FROM groups
         */
        DatabaseReference groupReference = SplitShareApp.firebaseDatabase.getReference("groups/");
        Query query = groupReference.orderByChild("Members");
        // Iterates through all the keys
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Log.d("Account", "The Members child exists");

                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Log.d("Account", "| The current snapshot has " + snapshot.getChildrenCount());

                        for (DataSnapshot children : snapshot.getChildren())
                        {
                            Log.d("Account", "|| " + children.getKey() + ": " + children.getValue());

                            if (children.getKey().equals("Members"))
                            {
                                for (DataSnapshot members: children.getChildren())
                                {
                                    Log.d("Account", "||| " + members.getKey() + ": " + members.getValue());

                                    for (DataSnapshot users : members.getChildren())
                                    {
                                        Log.d("Account", "|||| " + users.getKey() + ": " + users.getValue());

                                        if (users.getValue().equals(accountId))
                                        {
                                            Log.d("Account", "|||| MATCH FOUND");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    Log.d("Account", "The Members child does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
