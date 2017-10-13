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
    // The unique id for the group
    private String groupId;
    // The name of the group
    private String groupName;
    // The users that belong to the group
    private ArrayList<SplitShareUser> groupUsers;

    public Group(String groupId, String groupName, ArrayList<SplitShareUser> groupUsers)
    {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupUsers = groupUsers;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setGroupUsers(ArrayList<SplitShareUser> groupUsers)
    {
        this.groupUsers = groupUsers;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public ArrayList<SplitShareUser> getGroupUsers()
    {
        return groupUsers;
    }

    // Creates a group with only one member (the person who created the group)
    // TODO: Everyone, find a way to use child.push().setValue(key, object) instead of pushing by
    // TODO: default TimeStampKey:Object format
    public void createGroup()
    {
        // Creates a reference to our groups tree inside Firebase
        // -> GROUPS
        DatabaseReference groupReference =
                SplitShareApp.firebaseDatabase.getReference("groups/");

        // Checks for existence of our group id
        // -> GROUPS
        // -> -> GROUP_ID (?)
        groupReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the account already exist, log it but don't change anything
                if (dataSnapshot.hasChild(groupId))
                {
                    Log.d("Group", "The group already exists");
                }
                else
                {
                    // Create a group since one with this id doesn't exist
                    Log.d("Group", "A new group has been created");

                    // Create a new, final reference to our new group's location
                    // -> GROUPS
                    // -> -> GROUP_ID

                    final DatabaseReference newGroup =
                            SplitShareApp.firebaseDatabase.getReference("groups/" + groupId);

                    // Entries that will exist inside the new group
                    HashMap<String, String> groupEntries = new HashMap<>();
                    groupEntries.put("Group_Name", groupName);
                    groupEntries.put("Group_Members", null);
                    newGroup.setValue(groupEntries);
                    groupEntries.clear();
                    // Member entries
                    // UserID: Thing
                    groupEntries.put("UserID", SplitShareApp.acct.getId());
                    newGroup.child("Members").push().setValue(groupEntries);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /*
     * Adds a member to the group if they don't already exist
     */
    public void addMember(final String userIdToAdd)
    {
        // Hierarchy of the groupReference object
        // -> GROUPS
        // -> -> GROUP_ID
        // -> -> -> MEMBERS
        final DatabaseReference groupReference =
                SplitShareApp.firebaseDatabase.getReference("groups/" + groupId + "/Members");

        // SELECT *
        // FROM members
        // WHERE UserID = "GroupMemberId"
        Query query = groupReference.orderByChild("UserID").startAt(userIdToAdd).endAt(userIdToAdd);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the user already exists in the group, put it in our log
                if (dataSnapshot.exists())
                {
                    Log.d("Group", "The group already contains a user id " + userIdToAdd);
                }
                else
                {
                    // If the user account does not exist, enter it into our
                    // Member child with the value Timestamp:UserID
                    HashMap<String, String> memberEntries = new HashMap<>();
                    memberEntries.put("UserID", userIdToAdd);
                    groupReference.push().setValue(memberEntries);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // TODO: Remove a member from a list with a given group member id
    // Problem: push() as used above pushes in a TimestampKey:Object format, making it stupid hard
    // and maybe even impossible to remove if we don't know the timestamp (key)
    //
    public void removeMember(String groupMemberId)
    {
    }
}
