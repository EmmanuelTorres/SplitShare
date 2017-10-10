package com.splitshare.splitshare;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

/**
 * Created by dklug on 10/9/17.
 */

public class Group {
    private ArrayList<GoogleSignInAccount> users;
    private boolean democracy = true;

    public Group() {
        users = new ArrayList<GoogleSignInAccount>();
    }

    public Group(GoogleSignInAccount g, boolean b)
    {
        users = new ArrayList<GoogleSignInAccount>();
        democracy = b;
        users.add(g);
    }

    public void addMember(GoogleSignInAccount g)
    {
        users.add(g);
    }
    public void removeMember(GoogleSignInAccount g )
    {
        users.remove(g);
    }
}
