package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/*
 * This class is responsible for creating/maintaining groups and interfacing with
 * the database
 */
public class Group
{
    // A reference to the 'groups' table in Firebase
    private DatabaseReference groupsReference;

    // The name of the group
    private String groupName;

    public Group(String groupName)
    {
        this.groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");
        this.groupName = groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    // Creates a group with only one member (the person who created the group)
    public void createGroup()
    {
        // If the reference to the groups part of our table is null, we return
        if (groupsReference == null)
        {
            Log.d("Group", "The reference to groups is null");

            return;
        }
        // Queries for the newest group that was added, AKA the group with the largest GroupID
        Query largestGroupId = groupsReference.orderByChild("GroupID").limitToLast(1);

        // A single value event listener to traverse the snapshot
        largestGroupId.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String newGroupId = "0";

                // If there exists a group with a GroupID
                if (dataSnapshot.exists())
                {
                    // No matter what, there is only one group that is returned due to our limit
                    // Gets the Firebase-generated timestamp of our group
                    DataSnapshot groupTimestamp = dataSnapshot.getChildren().iterator().next();

                    // Gets the group id of our snapshot, aka the largest group id in the table
                    String groupId = groupTimestamp.child("GroupID").getValue().toString();

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
                SplitShareApp.splitShareUser.addGroup(newGroupId, groupName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /*
     * Adds a member to the group if they don't already exist
     */
    public void addMember(final String groupId, final String userIdToAdd, final String nameToAdd)
    {
        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByKey();

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Iterates through all the groups in our table
                for (DataSnapshot currentGroup: dataSnapshot.getChildren())
                {
                    // Finds the one whose GroupID matches with the one we want to change
                    if (currentGroup.child("GroupID").getValue().toString().equals(groupId))
                    {
                        DataSnapshot userToAdd = currentGroup.child("GroupMembers").child(userIdToAdd);

                        // If the user we want to add does not exist inside the GroupMembers part of our table
                        if (!userToAdd.exists())
                        {
                            Log.d("Group", "Adding user " + userIdToAdd + " to group " + groupId);

                            // We add him to the group
                            userToAdd.getRef().setValue(nameToAdd);

                            SplitShareApp.splitShareUser.addGroup(groupId, groupName);
                        }

                        // Exit the class since there's no point in checking groups after the one we wanted
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // TODO: Remove a member from a list with a given group member id
    public void removeMember(final String groupId, final String userIdToRemove)
    {
        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByKey();

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Iterates through all the groups in our table
                for (DataSnapshot currentGroup: dataSnapshot.getChildren())
                {
                    // Finds the one whose GroupID matches with the one we want to change
                    if (currentGroup.child("GroupID").getValue().toString().equals(groupId))
                    {
                        DataSnapshot userToRemove = currentGroup.child("GroupMembers").child(userIdToRemove);

                        // If the user we want to remove exists inside the GroupMembers part of our table
                        if (userToRemove.exists())
                        {
                            Log.d("Group", "Removing user " + userIdToRemove + " from group " + groupId);

                            // We remove him
                            userToRemove.getRef().removeValue();

                            SplitShareApp.splitShareUser.removeGroup(groupId);
                        }

                        // Exit the class since there's no point in checking groups after the one we wanted
                        return;
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
