package com.splitshare.splitshare;

import java.util.Calendar;
import java.util.List;

/**
 * Created by emmanuel on 10/9/17.
 */

public class MasterTask
{
    private String title;
    private String description;
    private int type;
    private Calendar startDate;
    private Calendar endDate;
    private long groupId;
    private List<User> activeUsers;
    // TODO: List<double> paymentDistribution;
    // and paymentAmount is the sum total
    private double paymentAmount;
    private Cycle cycle;

    public MasterTask(String title, String description, int type, Calendar startDate,
                      Calendar endDate, long groupId, List<User> activeUsers, double paymentAmount,
                      Cycle cycle)
    {
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
        this.activeUsers = activeUsers;
        this.paymentAmount = paymentAmount;
        this.cycle = cycle;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public Calendar getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Calendar startDate)
    {
        this.startDate = startDate;
    }

    public Calendar getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Calendar endDate)
    {
        this.endDate = endDate;
    }

    public long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(long groupId)
    {
        this.groupId = groupId;
    }

    public List<User> getActiveUsers()
    {
        return activeUsers;
    }

    public void setActiveUsers(List<User> activeUsers)
    {
        this.activeUsers = activeUsers;
    }

    public double getPaymentAmount()
    {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    public Cycle getCycle()
    {
        return cycle;
    }

    public void setCycle(Cycle cycle)
    {
        this.cycle = cycle;
    }
}
