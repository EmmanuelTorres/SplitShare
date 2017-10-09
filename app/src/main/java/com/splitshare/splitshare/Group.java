package com.splitshare.splitshare;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;

/**
 * Created by dklug on 10/9/17.
 */

public class Group {
    List<GoogleSignInAccount> users;
    boolean democracy = true;

    Group(GoogleSignInAccount g, boolean b)
    {
        democracy = b;
        users.add(g);
    }

    void addMember(GoogleSignInAccount g)
    {
        users.add(g);
    }

    void removeMember(GoogleSignInAccount g )
    {
        users.remove(g);
    }
}
