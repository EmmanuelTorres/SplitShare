package com.splitshare.splitshare;

import java.util.Calendar;
import java.util.List;

/**
 * Created by emmanuel on 10/9/17.
 */

public class MasterTask {
    private String title;
    private String description;
    private int type;
    private Calendar startDate;
    private Calendar endDate;
    private long groupId;
    private List<User> activeUsers;
    private double paymentAmount;
    private Cycle cycle;

    public MasterTask()
    {
        // Constructor
    }
}
