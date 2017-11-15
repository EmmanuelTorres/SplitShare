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

    /*
     * groupsReference is a shortcut to our groups/ section of the database
     * groupName is used when creating groups
     */
    public Group(String groupName)
    {
        this.groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");
        this.groupName = groupName;
    }

    // Sets the group name to the value given in the parameter
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    // Returns the name of the group
    public String getGroupName()
    {
        return groupName;
    }

    // Creates a group with only one member (the person who created the group)
    public void createGroup()
    {
        // If the reference to the groups part of our table is null
        if (groupsReference == null)
        {
            // We return (exit the void function) because nothing can be done
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
                // A variable newGroupId will always be used, regardless of if this is the first
                // group or not. It just belongs to the group being added and by default it is 0
                String newGroupId = "0";

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
                SplitShareApp.splitShareUser.addGroupToUser(newGroupId, newGroupId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /*
     * Adds a member to the group if they don't already exist
     */
    public void addMember(final String groupId, final String userIdToAdd, final String nameToAdd)
    {
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

                    // A snapshot specifically to the GroupMembers section of the table
                    // This makes it easier to add a member because we just get the reference
                    DataSnapshot userToAdd = groupValues.child("GroupMembers").child(userIdToAdd);

                    // If the user we want to add does not exist inside the GroupMembers part of our table
                    if (!userToAdd.exists())
                    {
                        Log.d("Group", "Adding user " + userIdToAdd + " to group " + groupId);

                        // We add him to the group
                        userToAdd.getRef().setValue(nameToAdd);

                        // We also add the group to his part of the user table
                        SplitShareApp.splitShareUser.addGroupToUser(groupValues.getKey(), groupId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Removes a member from a group
    public void removeMember(final String groupId, final String userIdToRemove)
    {
        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByChild("GroupID").equalTo(groupId);

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
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

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /*
     * Gets the group name
     * The function is static because we might want to access it from outer classes
     */
    public static String getGroupName(final String groupId)
    {
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/");

        // The connection to Firebase is bad
        if (groupsReference == null)
        {
            Log.d("Groups-GetGroupName", "The connection to Firebase is null");

            return null;
        }

        // Anonymous inner classes won't allow the changing of a variable that needs to be
        // final, but we can work around this by allocating space for a String and modifying that
        final String[] groupName = new String[1];

        // Queries for an existing group with the given groupId
        Query existingGroup = groupsReference.orderByChild("GroupID").equalTo(groupId);

        // An event listener is needed to traverse the snapshot
        existingGroup.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If there exists a group with a given group id
                if (dataSnapshot.exists())
                {
                    groupName[0] = dataSnapshot.child("GroupName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        return (groupName[0] == null) ? null : groupName[0];
    }
}
