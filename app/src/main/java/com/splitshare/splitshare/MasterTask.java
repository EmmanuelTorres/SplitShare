package com.splitshare.splitshare;

import com.google.firebase.database.DatabaseReference;

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
    private List<User> activeUsers;
    // TODO: List<double> paymentDistribution;
    // and paymentAmount is the sum total
    private double paymentAmount;
    private Cycle cycle;

    public MasterTask() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
    }

    public MasterTask(String title, String description, int type, Calendar startDate,
                      Calendar endDate, String groupId, List<User> activeUsers, double paymentAmount,
                      Cycle cycle)
    {
        this.title = title;
        this.description = description;
        this.category = category;
        setStartDate(startDate);
        setEndDate(endDate);
        this.group = group;
        this.activeUsers = activeUsers;
        this.paymentAmount = paymentAmount;
        this.cycle = cycle;
    }

    public void addToDatabase()
    {
        DatabaseReference taskReference = SplitShareApp.firebaseDatabase.getReference("groups/" + group.getGroupTimestamp() + "/GroupTasks/" + title + "/");
        taskReference.setValue(this);
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

    //this is for task population and convience
    public Task createDumbTask(Calendar date){
        return new Task(date, this.title,this.category, "", this.group);
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
        return new StoredMasterTask(title, description, type, sd,
                ed, groupId, activeUsers, paymentAmount, cycle);
    }
}
