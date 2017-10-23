package com.splitshare.splitshare;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by lyphc on 10/16/2017.
 */

public class SplitShare extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }



}
