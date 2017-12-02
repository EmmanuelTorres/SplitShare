package com.splitshare.splitshare;

import android.util.Log;
import java.util.Calendar;

public class TaskPopulation
{
    public static Calendar today;
    public static Group specificGroup;

    //This is when we toggle down to add more task to our scroll list
    public static void populateNext()
    {
        // Clears the populatedTask list to avoid duplicate entries
        SplitShareApp.populatedTask.clear();

        // Iterate through all the Master Tasks that we know we belong to
        for(MasterTask masterTask : SplitShareApp.usersMasterTasks)
        {
            // If the Group we want to filter by is NOT null
            if (specificGroup != null)
            {
                // If the Master Task does NOT belong to the group we're filtering for
                if (!masterTask.getGroup().getGroupTimestamp().equals(specificGroup.getGroupTimestamp()))
                {
                    // We continue onto the next Master Task
                    Log.d("TaskPop-Pop(Group)", "The Master Task " + masterTask.getTitle() +
                            " does not belong to the group " + specificGroup.getGroupName());
                    continue;
                }
            }

            // If the Group we want to filter by IS null
            // We'll just accept every group

            // Creates a Calendar instance and sets the time to 0 for comparison purposes
            today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // If the Master Task ends before today AND it isn't forever, we just continue onto
            // the next Master Task
            if (masterTask.getEndDate().before(today) && !masterTask.isForever())
            {
                continue;
            }
            else
            {
                // Creates a copy of our today Calendar instance
                Calendar currentDay = (Calendar) today.clone();

                // Loads the first seven days
                for (int i = 0; i < 7; i++)
                {
                    if (masterTask.getCycle().isOnDayWithStart(masterTask.getStartDate(), currentDay))
                    {
                        int whosTurn = masterTask.getCycle().numOcurrencesSinceStart(masterTask.getStartDate(), currentDay) % masterTask.getActiveUsers().size();

                        String user1 = masterTask.getActiveUsers().get(whosTurn);

                        // only add to agenda if this task is mine
                        if (user1.compareTo(SplitShareApp.splitShareUser.getUserId()) == 0)
                        {
                            if (masterTask.getEndDate().before(currentDay) && !masterTask.isForever())
                                break;
                            //Create dumb task
                            SplitShareApp.populatedTask.add(masterTask.createDumbTask((Calendar) currentDay.clone()));
                        }
                    }
                }
            }

        }
        // next refresh(pull down) is 7 days later
        today.add(Calendar.DATE, 7);
        MainActivity.addAllToTaskList(SplitShareApp.populatedTask);
        MainActivity.swipeToRefresh.setRefreshing(false);
    }

    /*
     * Populates the Main Activity with Master Tasks only from a specific group
     */
    public static void populate()
    {
        // Clears the populatedTask list to avoid duplicate entries
        SplitShareApp.populatedTask.clear();

        // Iterate through all the Master Tasks that we know we belong to
        for(MasterTask masterTask : SplitShareApp.usersMasterTasks)
        {
            // If the Group we want to filter by is NOT null
            if (specificGroup != null)
            {
                // If the Master Task does NOT belong to the group we're filtering for
                if (!masterTask.getGroup().getGroupTimestamp().equals(specificGroup.getGroupTimestamp()))
                {
                    // We continue onto the next Master Task
                    Log.d("TaskPop-Pop(Group)", "The Master Task " + masterTask.getTitle() +
                            " does not belong to the group " + specificGroup.getGroupName());
                    continue;
                }
            }

            // If the Group we want to filter by IS null
            // We'll just accept every group

            // Creates a Calendar instance and sets the time to 0 for comparison purposes
            today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // If the Master Task ends before today AND it isn't forever, we just continue onto
            // the next Master Task
            if (masterTask.getEndDate().before(today) && !masterTask.isForever())
            {
                continue;
            }
            else
            {
                // Creates a copy of our today Calendar instance
                Calendar currentDay = (Calendar) today.clone();

                // Loads the first seven days
                for (int i = 0; i < 7; i++)
                {
                    if (masterTask.getCycle().isOnDayWithStart(masterTask.getStartDate(), currentDay))
                    {
                        int whosTurn = masterTask.getCycle().numOcurrencesSinceStart(masterTask.getStartDate(), currentDay) % masterTask.getActiveUsers().size();

                        String user1 = masterTask.getActiveUsers().get(whosTurn);

                        // only add to agenda if this task is mine
                        if (user1.compareTo(SplitShareApp.splitShareUser.getUserId()) == 0)
                        {
                            if (masterTask.getEndDate().before(currentDay) && !masterTask.isForever())
                                break;
                            //Create dumb task
                            SplitShareApp.populatedTask.add(masterTask.createDumbTask((Calendar) currentDay.clone()));
                        }
                    }
                }
            }

        }
        // next refresh(pull down) is 7 days later
        today.add(Calendar.DATE, 7);
        MainActivity.addAllToTaskList(SplitShareApp.populatedTask);
//        MainActivity.swipeToRefresh.setRefreshing(false);
    }
}
