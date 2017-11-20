package com.splitshare.splitshare;

import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

/**
 * Created by dklug on 10/9/17.
 */

public class Task implements Comparable{
    public Calendar date;
    public String title;
    public String category;
    public String assignedMember;
    public Group group;
    public boolean costDue;
    public String feeCollectionMember;
    public double fee;

    Task()
    {
        date = Calendar.getInstance();
        title = "take out trash";
        category = "chores";
        assignedMember = "Joel Miyagi";
        costDue = true;
        feeCollectionMember = "Ebenezer Scrooge";
        fee = 4000.00;
        group = new Group("the_test_group");

        date.set(2017, 6, 4);
    }

    /*
     * This constructor creates a Task object with an associated fee
     * d = date, t = title, c = category, am = assigned member, g = group,
     * cd = costDue, fcm = fee collection member, f = fee
     */
    Task(Calendar d, String t, String c, String am, Group g, boolean cd, String fcm, double f)
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
    Task(Calendar d, String t, String c, String am, Group g)
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
    String getDate() {
        String day = String.format("%02d", date.get(Calendar.DATE));
        String month = String.format("%02d", date.get(Calendar.MONTH) + 1); // +1 because Calendars ammirite?
        String year = String.format("%4d", date.get(Calendar.YEAR));
        return month + "/" + day + "/" + year;
    }

    public int compareTo(Object o) throws ClassCastException{
        if (!(o instanceof Task))
            throw new ClassCastException("Must compare Task with Task!");
        Task other = (Task) o;
        if (other.date.getTimeInMillis() > date.getTimeInMillis()) {
            return -1;
        } else if (other.date.getTimeInMillis() < date.getTimeInMillis()) {
            return 1;
        } else {
            return 0;
        }
    }
}
