package com.splitshare.splitshare;

import java.util.Date;
import java.util.List;

/**
 * Created by dklug on 10/9/17.
 */

public class Task {
    Date date;
    String title;
    String category;
    String assignedMember;
    Group group;
    boolean costDue;
    String feeCollectionMember;
    double fee;

    Task()
    {
        date = new Date(2017, 12, 4);
        title = "take out trash";
        category = "chores";
        assignedMember = "Joel Miyagi";
        costDue = true;
        feeCollectionMember = "Ebenezer Scrooge";
        fee = 4000.00;
    }

    /*
     * This constructor creates a Task object with an associated fee
     * d = date, t = title, c = category, am = assigned member, g = group,
     * cd = costDue, fcm = fee collection member, f = fee
     */
    Task(Date d, String t, String c, String am, Group g, boolean cd, String fcm, double f)
    {
        date = d;
        title = t;
        category = c;
        assignedMember = am;
        group = g;
        costDue = cd;
        feeCollectionMember = fcm;
        fee = f;
    }

    /*
     * This constructor creates a Task object WITHOUT an associated fee
     * d = date, t = title, c = category, am = assigned member, g = group,
     * cd = costDue, fcm = fee collection member, f = fee
     */
    Task(Date d, String t, String c, String am, Group g)
    {
        date = d;
        title = t;
        category = c;
        assignedMember = am;
        group = g;
        costDue = false;
        feeCollectionMember = "";
        fee = 0.0;
    }
}
