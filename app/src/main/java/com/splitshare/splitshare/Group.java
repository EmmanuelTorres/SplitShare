package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Group
{
    // The unique timestamp of the group
    private String groupTimestamp;
    // The name of the group
    private String groupName;

    // Default constructor
    public Group()
    {
        this.groupTimestamp = null;
        this.groupName = "Default Name";
    }

    // Creates a group with a groupTimestamp and sets the groupName to the Firebase value
    // With this constructor, we can do anything we want in this class
    public Group(String groupName)
    {
        this.groupTimestamp = null;
        this.groupName = groupName;
    }

    public String getGroupTimestamp() { return groupTimestamp; }
    public String getGroupName() { return groupName; }

    public Group(String groupTimestamp, String groupName)
    {
        this.groupTimestamp = groupTimestamp;
        this.groupName = groupName;
    }

    // Creates a group with only one member (the person using the app)
    public void createGroup()
    {
        // Creates a new object that references the Firebase database
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");

        // If the connection to Firebase is bad
        if (groupsReference == null)
        {
            // We exit the function since there is no connection to Firebase
            Log.d("Group", "The reference to groups is null inside CreateGroup");

            return;
        }

        // Gets a random timestamp from Firebase that will be used as our unique identifier for the group
        groupTimestamp = groupsReference.push().getKey();

        // A HashMap of String keys and values can be used to insert things into the
        // table in key:value form
        HashMap<String, String> groupEntries = new HashMap<>();
        groupEntries.put("GroupID", groupTimestamp);
        groupEntries.put("GroupName", groupName);

        // Changes the reference from Groups/ to Groups/GroupTimestamp/
        groupsReference = groupsReference.child(groupTimestamp);

        // Push will assign a new entry within the Groups/GroupTimestamp table and the parent folder
        // will be a random timestamp value assigned by Firebase
        // We use that timestamped key to set the value of a new table, our groupEntries above
        groupsReference.setValue(groupEntries);

        // We re-purpose groupEntries HashMap to initialize the group with a member (the user)
        groupEntries.clear();

        // Our first member will be us in the form of UserID:Name
        groupEntries.put(SplitShareApp.acct.getId(), SplitShareApp.acct.getDisplayName());
        groupsReference.child("GroupMembers").setValue(groupEntries);

        // Add this group to the user's id
        completeAddMember(SplitShareApp.splitShareUser.getUserId());
    }

    public void completeAddMember(String ID)
    {
        // Variables need to be final to be used inside anonymous inner classes
        final String userId = ID;

        final DatabaseReference accountReference = SplitShareApp.firebaseDatabase.getReference("users/" + userId);

        if (accountReference == null)
        {
            // Log the mistake for easy debugging
            Log.d("Group-CompleteAddMember", "The database reference was null");

            return;
        }

        // Attach a listener to the reference that points to Users/UserId/
        accountReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.exists())
                {
                    Log.d("Group-CompleteAddMember", "User doesn't exist, can't add to group.");
                }
                else
                {
                    // add group to user's list of groups
                    accountReference.child("Groups").child(groupTimestamp).setValue(groupName);
                    Log.d("Group-CompleteAddMember", "The user " + userId + " successfully added to group " + groupName);

                    // add to actual group
                    DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/" + groupTimestamp);
                    groupsReference.child("GroupMembers").child(userId).setValue(dataSnapshot.child("Name").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    // Removes a member from a group
    public void removeMember(final String userIdToRemove)
    {
        // Creates a new object that references the Firebase database
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/" + groupTimestamp);

        Log.d("Group-RemoveMember", "Removing member " + userIdToRemove + " from group " + groupName);
        groupsReference.child("GroupMembers").child(userIdToRemove).removeValue();
    }

    /*
     * Returns an ArrayList of Users belonging to the Group
     */
    public ArrayList<User> getUsers()
    {
        // Creates a new object that references the Firebase database
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/" + groupTimestamp);

        // The ArrayList that users will be added to
        final ArrayList<User> groupUsers = new ArrayList<>();

        // A query to get all GroupMembers belonging to our Group
        Query query = groupsReference.child("GroupMembers");

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot groupMembers)
            {
                // If the GroupMembers portion of our table exists
                if (groupMembers.exists())
                {
                    Log.d("Group-GetUsers", groupMembers.toString());

                    // Loop through all the Users
                    for (DataSnapshot groupMember: groupMembers.getChildren())
                    {
                        Log.d("Group-GetUsers", groupMember.getKey());

                        groupUsers.add(new User(groupMember.getKey(), (String) groupMember.getValue()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return groupUsers;
    }

    @Override
    public String toString() {
        return groupName;
    }
}