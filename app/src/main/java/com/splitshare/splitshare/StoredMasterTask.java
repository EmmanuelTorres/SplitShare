package com.splitshare.splitshare;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.List;

/**
 * Created by armando on 11/17/17.
 */

public class StoredMasterTask {
    public String title;
    public String description;
    public int type;
    public SimpleDate startDate;
    public SimpleDate endDate;
    public String groupId;
    public List<User> activeUsers;
    // TODO: List<double> paymentDistribution;
    // and paymentAmount is the sum total
    public double paymentAmount;
    public boolean costDue;
    public Cycle cycle;

    public  StoredMasterTask() {

    }

    public StoredMasterTask(String title, String description, int type, SimpleDate startDate,
                      SimpleDate endDate, String groupId, List<User> activeUsers, boolean costDue, double paymentAmount,
                      Cycle cycle)
    {
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
        this.activeUsers = activeUsers;
        this.costDue = costDue;
        this.paymentAmount = paymentAmount;
        this.cycle = cycle;
    }

    public void addToDatabase()
    {
        DatabaseReference taskReference = SplitShareApp.firebaseDatabase.getReference("groups/" + groupId + "/GroupTasks/" + title + "/");
        taskReference.setValue(this);
    }

    public MasterTask toMasterTask() {
        Calendar sd = Calendar.getInstance();
        sd.set(Calendar.DAY_OF_MONTH, startDate.day);
        sd.set(Calendar.MONTH, startDate.month);
        sd.set(Calendar.YEAR, startDate.year);

        Calendar ed = Calendar.getInstance();
        ed.set(Calendar.DAY_OF_MONTH, endDate.day);
        ed.set(Calendar.MONTH, endDate.month);
        ed.set(Calendar.YEAR, endDate.year);

        return new MasterTask(title, description, type, sd,
                ed, groupId, activeUsers, costDue, paymentAmount, cycle);
    }
}
