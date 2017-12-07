package com.splitshare.splitshare;

import java.util.Calendar;

/**
 * Created by lyphc on 11/16/2017.
 */



public class TaskPopulation
{
    public static Calendar today;
    public static Group filterGroup;
    public static void populate()
    {
        // for correctness, if there's no MasterTasks
        today = Calendar.getInstance();

        SplitShareApp.populatedTask.clear();
        for(MasterTask a : SplitShareApp.usersMasterTasks){
            // allows us to filter by group.
            // if a group is set, we ignore all MasterTasks with non-matching timestamps
            if (filterGroup != null && !filterGroup.getGroupTimestamp().equals(a.getGroup().getGroupTimestamp()))
                continue;

            today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            //if last date of task < today, then skip this task
            if(a.getEndDate().before(today) && !a.isForever()){
                continue;
            } else {
                Calendar currentDay = (Calendar)today.clone();
                //breaks after either one month or 5 tasks spawn from this master task
                for (int i = 0; i < 7; i++) {
                    //if there is an event for this day
                    if (a.getCycle().isOnDayWithStart(a.getStartDate(), currentDay)) {
                        int whosTurn = a.getCycle().numOcurrencesSinceStart(a.getStartDate(), currentDay) % a.getActiveUsers().size();
                        String user1 = a.getActiveUsers().get(whosTurn);
                        // only add to agenda if this task is mine
                        if (user1.compareTo(SplitShareApp.splitShareUser.getUserId()) == 0) {

                            if (a.getEndDate().before(currentDay) && !a.isForever())
                                break;
                            //Create dumb task
                            SplitShareApp.populatedTask.add(a.createDumbTask((Calendar) currentDay.clone()));
                        }

                    }
                    currentDay.add(Calendar.DATE, 1);

                }
            }

        }
        // next refresh(pull up) is 7 days later
        today.add(Calendar.DATE, 7);
        MainActivity.addAllToTaskList(SplitShareApp.populatedTask);
        MainActivity.swipeToRefresh.setRefreshing(false);
    }

    //This is when we refresh to add more task to our scroll list
    public static void populateNext()
    {
        SplitShareApp.populatedTask.clear();
        for(MasterTask a : SplitShareApp.usersMasterTasks){
            // allows us to filter by group.
            // if a group is set, we ignore all MasterTasks with non-matching timestamps
            if (filterGroup != null && !filterGroup.getGroupTimestamp().equals(a.getGroup().getGroupTimestamp()))
                continue;

            //if last date of task < today, then skip this task
            if(a.getEndDate().before(today) && !a.isForever()){
                continue;
            } else {
                Calendar currentDay = (Calendar)today.clone();
                //breaks after either one month or 5 tasks spawn from this master task
                for (int i = 0; i < 7; i++) {
                    //if there is an event for this day
                    if (a.getCycle().isOnDayWithStart(a.getStartDate(), currentDay)) {
                        int whosTurn = a.getCycle().numOcurrencesSinceStart(a.getStartDate(), currentDay) % a.getActiveUsers().size();
                        String user1 = a.getActiveUsers().get(whosTurn);
                        // only add to agenda if this task is mine
                        if (user1.compareTo(SplitShareApp.splitShareUser.getUserId()) == 0) {

                            if (a.getEndDate().before(currentDay) && !a.isForever())
                                break;
                            //Create dumb task
                            SplitShareApp.populatedTask.add(a.createDumbTask((Calendar) currentDay.clone()));
                        }

                    }
                    currentDay.add(Calendar.DATE, 1);

                }
            }

        }
        // next refresh is 7 days later
        today.add(Calendar.DATE, 7);
        MainActivity.addMoreToTaskList(SplitShareApp.populatedTask);
    }

}
