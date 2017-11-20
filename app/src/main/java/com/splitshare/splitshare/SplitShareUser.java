package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
     * Creates a brand new account for the user of the app
     */
    public void createAccount()
    {
        final DatabaseReference accountReference = super.getAccountReference();

        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("User-CreateAccount", "The database reference was null");

            return;
        }

        // Variables need to be final to be used inside anonymous inner classes
        final String userId = super.getUserId();
        final String userName = super.getUserName();
        final String userEmail = super.getUserEmail();

        // Attach a listener to the reference that points to Users/UserId/
        accountReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.exists())
                {
                    Log.d("User-CreateAccount", "Creating a user account");

                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("UserID", userId);
                    userData.put("Name", userName);
                    userData.put("Email", userEmail);

                    accountReference.setValue(userData);
                }
                else
                {
                    Log.d("User-CreateAccount", "The user " + userEmail + " already exists");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /*
     * Adds all the groups belonging to the SplitShareUser to the UsersGroups list inside SplitShareApp
     */
    public void getGroups()
    {
        if (super.getAccountReference() == null)
        {
            // Log the mistake for easy debugging
            Log.d("User-CreateAccount", "The database reference was null");

            return;
        }

        super.getAccountReference().child("Groups").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot userGroup : dataSnapshot.getChildren())
                    {
                        String groupTimestamp = userGroup.getKey();
                        String groupName = userGroup.getValue().toString();

                        Log.d("User-GetGroups", "Adding the group " + groupName + " to UsersGroups");

                        MainActivity.navMenu.add(0,0,0,groupName);

                        SplitShareApp.usersGroups.add(new Group(groupTimestamp, groupName));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getTasks()
    {
        // The Groups belonging to a user
        for (Group currentGroup : SplitShareApp.usersGroups)
        {
            // Temporary reference to group's task inside a database
            DatabaseReference groupReference = SplitShareApp.firebaseDatabase.getReference("/groups/" + currentGroup.getGroupTimestamp() + "/GroupTasks/");

            groupReference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    // If the group has tasks
                    if (dataSnapshot.exists())
                    {
                        // Iterate through the tasks
                        for (DataSnapshot currentTask: dataSnapshot.getChildren())
                        {
                            StoredMasterTask task = currentTask.getValue(StoredMasterTask.class);

                            SplitShareApp.usersMasterTasks.add(task.toMasterTask());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}
