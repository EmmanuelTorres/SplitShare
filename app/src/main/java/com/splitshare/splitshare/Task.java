package com.splitshare.splitshare;

import java.util.Date;
import java.util.List;

/**
 * Created by dklug on 10/9/17.
 */

public class Task {
    Date date;
    List<String> people;
    Group group;
    Task()
    {
        date = new Date();
        people.add("$PERSON");
    }
    Task(Date d, List<String> p, Group g)
    {
        date = d;
        people = p;
        group = g;
    }
}
