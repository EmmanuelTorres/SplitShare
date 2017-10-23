package com.splitshare.splitshare;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by dklug on 10/13/17.
 */

public class DataHandler {

    public static void set(String path, final String variableToAdd)
    {
        // Establishes a reference
        final DatabaseReference pathReference =
                SplitShareApp.firebaseDatabase.getReference(path);

        if (pathReference == null)
        {
            // Log the mistake for easy debugging
            Log.d(path, "The database reference was null");

            return;
        }

        pathReference.setValue(variableToAdd);
    }

    public static void setCareful(String path, final String variableToAdd)
    {
        // If the database reference is null, the connection to the server isn't good
        final DatabaseReference pathReference =
                SplitShareApp.firebaseDatabase.getReference(path);

        if (pathReference == null)
        {
            // Log the mistake for easy debugging
            Log.d(path, "The database reference was null");

            return;
        }

        // A listener is needed to check if an account already exists
        Query query = pathReference.orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the variable already exist, log it but don't change anything
                if (dataSnapshot.exists())
                {
                    Log.d(variableToAdd, "Already exists");
                }
                else
                {
                    // Create an variable since it doesn't exist at this point
                    Log.d(variableToAdd, "is created");

                    // Sets the above entries as the values
                    pathReference.setValue(variableToAdd);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void set(String path, final HashMap<String, String> userData)
    {
        // A listener is needed to check if an account already exists
        // Establishes a reference to the user account
        final DatabaseReference pathReference =
                SplitShareApp.firebaseDatabase.getReference(path);

        if (pathReference == null)
        {
            // Log the mistake for easy debugging
            Log.d(path, "The database reference was null");

            return;
        }

        pathReference.setValue(userData);
    }

    public static void setCareful(String path, final HashMap<String, String> userData)
    {
        // If the database reference is null, the connection to the server isn't good
        final DatabaseReference pathReference =
                SplitShareApp.firebaseDatabase.getReference(path);

        if (pathReference == null)
        {
            // Log the mistake for easy debugging
            Log.d(path, "The database reference was null");

            return;
        }

        // A listener is needed to check if an account already exists
        Query query = pathReference.orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // If the variable already exist, log it but don't change anything
                if (dataSnapshot.exists())
                {
                    Log.d("userData", "Already exists");
                }
                else
                {
                    // Create an variable since it doesn't exist at this point
                    Log.d("userData", "is created");

                    // Sets the above entries as the values
                    pathReference.setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void remove(String path)
    {
        final DatabaseReference pathReference =
                SplitShareApp.firebaseDatabase.getReference(path);
        pathReference.removeValue();
    }

}
