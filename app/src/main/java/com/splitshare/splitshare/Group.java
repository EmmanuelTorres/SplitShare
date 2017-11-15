package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class Group
{
    // The unique timestamp of the group
    private String groupTimestamp;
    // The unique id of the group
    private String groupId;
    // The name of the group
    private String groupName;

    // Default constructor
    public Group()
    {
        this.groupId = null;
        this.groupName = "Default Name";
    }

    // Creates a group with a groupId and sets the groupName to the Firebase value
    // With this constructor, we can do anything we want in this class
    public Group(String groupId)
    {
        this.groupId = groupId;
        this.groupName = "Default Name";
    }

    public Group(String groupTimestamp, String groupId, String groupName)
    {
        this.groupTimestamp = groupTimestamp;
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

    /*
     * Adds a User to the "GroupMembers" section of the Group
     */
    public void addMember(final String userIdToAdd, final String nameToAdd)
    {
        // Creates a new object that references the Firebase database
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/" + groupTimestamp);

        Log.d("Group-AddMember", "Adding member " + nameToAdd + " to group " + groupName);
        groupsReference.child("GroupMembers").child(userIdToAdd).setValue(nameToAdd);
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

                        groupUsers.add(new User(groupMember.getKey()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Log.d("Group-GetUsers", "Size " + groupUsers.size());

        for (User user: groupUsers)
        {
            Log.d("Group-GetUsers", user.getUserName());
        }

        return groupUsers;
    }
}