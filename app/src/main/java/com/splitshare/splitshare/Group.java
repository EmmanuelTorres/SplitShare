package com.splitshare.splitshare;

import java.util.ArrayList;

/**
 * Created by dklug on 10/9/17.
 */

public class Group {
    private ArrayList<SplitShareUser> users;
    private boolean democracy = true;

    public Group() {
        users = new ArrayList<SplitShareUser>();
    }

    public Group(SplitShareUser g, boolean b)
    {
        users = new ArrayList<SplitShareUser>();
        democracy = b;
        users.add(g);
    }

    public void addMember(SplitShareUser g)
    {
        users.add(g);
    }
    public void removeMember(SplitShareUser g )
    {
        users.remove(g);
    }
}
