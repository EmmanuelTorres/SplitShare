package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SmartGroup
{
    // The unique id of the group
    private String groupId;
    // The name of the group
    private String groupName;

    // Default constructor
    public SmartGroup()
    {
        this.groupId = null;
        this.groupName = "Default Name";
    }

    // Creates a group with a groupId and sets the groupName to the Firebase value
    // With this constructor, we can do anything we want in this class
    public SmartGroup(String groupId)
    {
        this.groupId = groupId;
        this.groupName = "Default Name";
    }

    public SmartGroup(String groupId, String groupName)
    {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    // Creates a group with only one member (the person using the app)
    public void createGroup()
    {
        // Creates a new object that references the Firebase database
        final DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");

        // If the connection to Firebase is bad
        if (groupsReference == null)
        {
            // We exit the function since there is no connection to Firebase
            Log.d("Group", "The reference to groups is null inside CreateGroup");

            return;
        }

        // Queries for the last group that was added, AKA the group with the largest GroupID
        Query largestGroupId = groupsReference.orderByChild("GroupID").limitToLast(1);

        // A single value event listener to traverse the snapshot
        largestGroupId.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // A variable newGroupId will always be used, regardless of if this is the first
                // group or not. It just belongs to the group being added and by default it is 0
                String newGroupId = "0";

                // This will handle what to set newGroupId to if this isn't the first group
                // If there exists a group with a GroupID
                if (dataSnapshot.exists())
                {
                    // No matter what, there is only one group that is returned due to our limit
                    // Gets the part of that table that we can modify (past the timestamp)
                    DataSnapshot groupValues = dataSnapshot.getChildren().iterator().next();

                    // Gets the group id of our snapshot, aka the largest group id in the table
                    String groupId = groupValues.child("GroupID").getValue().toString();

                    // Adds 1 to that for our new group
                    newGroupId = Integer.toString(Integer.parseInt(groupId) + 1);
                }

                // A HashMap of String keys and values can be used to insert things into the
                // table in key:value form
                HashMap<String, String> groupEntries = new HashMap<>();
                groupEntries.put("GroupName", groupName);
                groupEntries.put("GroupID", newGroupId);

                // Push will assign a new entry within the 'groups' table and the parent folder will
                // be a random timestamp value assigned by Firebase
                // We use that timestamped key to set the value of a new table, our groupEntries above
                String key = groupsReference.push().getKey();
                groupsReference.child(key).setValue(groupEntries);

                // We re-purpose groupEntries HashMap to initialize the group with a member (the user)
                groupEntries.clear();

                // Our first member will be us in the form of UserID:Name
                groupEntries.put(SplitShareApp.acct.getId(), SplitShareApp.acct.getDisplayName());
                groupsReference.child(key).child("GroupMembers").setValue(groupEntries);

                // Add this group to the user's id
                SplitShareApp.splitShareUser.addGroupToUser(key, newGroupId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Adds a person to the group instance
    // With this, we don't need the groupId
    public void addMember(final String userIdToAdd, final String nameToAdd)
    {
        // Creates a new object that references the Firebase database
        final DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");

        // The connection to Firebase is bad
        if (groupsReference == null)
        {
            // We exit the function since there is no connection to Firebase
            Log.d("Group", "The reference to groups is null inside addMember");

            return;
        }

        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByChild("GroupID").equalTo(groupId);

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If there exists a group with our instance's groupId
                if (dataSnapshot.exists())
                {
                    // Gets the part of that table that we can modify (past the timestamp)
                    DataSnapshot groupValues = dataSnapshot.getChildren().iterator().next();

                    // A snapshot specifically to the GroupMembers section of the table
                    // This makes it easier to add a member because we just get the reference
                    DataSnapshot userToAdd = groupValues.child("GroupMembers").child(userIdToAdd);

                    Log.d("SmartGroup-Add", groupValues.toString());
                    Log.d("SmartGroup-Add", userToAdd.toString());

                    // If the user we want to add does not exist inside the GroupMembers part of our table
                    if (!userToAdd.exists())
                    {
                        Log.d("Group", "Adding user " + userIdToAdd + " to group " + groupId);

                        // We add him to the group
                        userToAdd.getRef().setValue(nameToAdd);

                        // We also add the group to his part of the user table
                        SplitShareApp.splitShareUser.addGroupToUser(groupValues.getKey(), groupName);
                    }
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
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");

        // If the reference to the groups part of our table is null
        if (groupsReference == null)
        {
            // We exit the function since there is no connection to Firebase
            Log.d("Group", "The reference to groups is null inside removeMember");

            return;
        }

        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByChild("GroupID").equalTo(groupId);

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    // Gets the part of that table that we can modify (past the timestamp)
                    DataSnapshot groupValues = dataSnapshot.getChildren().iterator().next();

                    // If there is only one member
                    if (groupValues.child("GroupMembers").getChildrenCount() == 1)
                    {
                        // We delete the group
                        groupValues.getRef().removeValue();

                        // TODO: A function belonging to MasterTask that removes it

                        return;
                    }

                    // A snapshot specifically to the GroupMembers section of the table
                    // This makes it easier to add a member because we just get the reference
                    DataSnapshot userToRemove = groupValues.child("GroupMembers").child(userIdToRemove);

                    // If the user we want to remove exists inside the GroupMembers part of our table
                    if (userToRemove.exists())
                    {
                        Log.d("Group", "Removing user " + userIdToRemove + " from group " + groupId);

                        // We remove him
                        userToRemove.getRef().removeValue();

                        // We remove the group from his user
                        SplitShareApp.splitShareUser.removeGroup(groupId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}