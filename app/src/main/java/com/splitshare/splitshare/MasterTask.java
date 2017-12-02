package com.splitshare.splitshare;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by emmanuel on 10/9/17.
 */

public class MasterTask
{
    private String title;
    private String description;
    private String category;
    private Calendar startDate;
    private Calendar endDate;
    private Group group;
    private List<String> activeUsers;
    // TODO: List<double> paymentDistribution;
    // and paymentAmount is the sum total
    private double paymentAmount;
    public boolean costDue;
    private Cycle cycle;
    private boolean isForever;

    public MasterTask() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
    }

    public MasterTask(String title, String description, String category, Calendar startDate,
                      Calendar endDate, Group group, List<String> activeUsers, boolean costDue, double paymentAmount,
                      Cycle cycle, boolean forever)
    {
        this.title = title;
        this.description = description;
        this.category = category;
        setStartDate(startDate);
        setEndDate(endDate);
        this.group = group;
        this.activeUsers = new ArrayList<String>(activeUsers);
        this.costDue = costDue;
        this.paymentAmount = paymentAmount;
        this.cycle = cycle;
        this.isForever = forever;
    }

    public void addToDatabase()
    {
        DatabaseReference taskReference = SplitShareApp.firebaseDatabase.getReference("groups/" + group.getGroupTimestamp() + "/GroupTasks/" + title + "/");
        taskReference.setValue(this);
    }

    public void removeFromTask(String userId)
    {
        if (activeUsers.contains(userId))
        {
            activeUsers.remove(userId);

            addToDatabase();
        }
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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public Calendar getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Calendar startDate)
    {
        this.startDate = startDate;

        this.startDate.set(Calendar.HOUR_OF_DAY, 0);
        this.startDate.set(Calendar.MINUTE, 0);
        this.startDate.set(Calendar.SECOND, 0);
        this.startDate.set(Calendar.MILLISECOND, 0);
    }

    public Calendar getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Calendar endDate)
    {
        this.endDate = endDate;

        this.endDate.set(Calendar.HOUR_OF_DAY, 0);
        this.endDate.set(Calendar.MINUTE, 0);
        this.endDate.set(Calendar.SECOND, 0);
        this.endDate.set(Calendar.MILLISECOND, 0);
    }

    public Group getGroup()
    {
        return group;
    }

    public void setGroup(Group group)
    {
        this.group = group;
    }

    public List<String> getActiveUsers()
    {
        return activeUsers;
    }

    public void setActiveUsers(List<String> activeUsers)
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

    //this is for task population and convience
    public Task createDumbTask(Calendar date){
        return new Task(date, this.title, this.category, "", this.group);
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean forever) {
        isForever = forever;
    }

//    Task(Calendar d, String t, String c, String am, Group g)
//    {
//        date = d;
//        title = t;
//        category = c;
//        assignedMember = am;
//        group = g;
//        costDue = false;
//        feeCollectionMember = "";
//        fee = 0.0;
//    }

    public Cycle getCycle()
    {
        return cycle;
    }

    public void setCycle(Cycle cycle)
    {
        this.cycle = cycle;
    }

    public StoredMasterTask toStoredMasterTask() {
        SimpleDate sd = new SimpleDate(this.startDate.get(Calendar.MONTH), this.startDate.get(Calendar.DAY_OF_MONTH), this.startDate.get(Calendar.YEAR));
        SimpleDate ed = new SimpleDate(this.endDate.get(Calendar.MONTH), this.endDate.get(Calendar.DAY_OF_MONTH), this.endDate.get(Calendar.YEAR));
        return new StoredMasterTask(title, description, category, sd,
                ed, group, activeUsers, costDue, paymentAmount, cycle, isForever);
    }
}
