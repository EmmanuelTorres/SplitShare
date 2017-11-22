package com.splitshare.splitshare;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by armando on 11/17/17.
 */

public class StoredMasterTask {
    public String title;
    public String description;
    public String category;
    public SimpleDate startDate;
    public SimpleDate endDate;
    public Group group;
    public List<String> activeUsers;
    // TODO: List<double> paymentDistribution;
    // and paymentAmount is the sum total
    public double paymentAmount;
    public boolean costDue;
    public Cycle cycle;

    public  StoredMasterTask() {

    }

    public StoredMasterTask(String title, String description, String category, SimpleDate startDate,
                            SimpleDate endDate, Group group, List<String> activeUsers, boolean costDue, double paymentAmount,
                            Cycle cycle)
    {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group;
        this.activeUsers = new ArrayList<String>(activeUsers);
        this.costDue = costDue;
        this.paymentAmount = paymentAmount;
        this.cycle = cycle;
    }

    public void addToDatabase()
    {
        DatabaseReference taskReference = SplitShareApp.firebaseDatabase.getReference("groups/" + group.getGroupTimestamp() + "/GroupTasks/" + title + "/");
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

        return new MasterTask(title, description, category, sd,
                ed, group, activeUsers, costDue, paymentAmount, cycle);
    }
}
