package com.splitshare.splitshare;

import java.util.Calendar;

/**
 * Created by lyphc on 11/16/2017.
 */



public class TaskPopulation
{
    public static void populate()
    {
        for(MasterTask a : SplitShareApp.usersMasterTasks){
            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            //if last date of task < today, then skip this task
            if(a.getEndDate().before(today)){
                continue;
            } else {

                int limit = 0;
                Calendar currentDay = (Calendar)today.clone();
                //breaks after either one month or 5 tasks spawn from this master task
                for(int i = 0; i< 30; i++){
                    if(limit == 5){break;}
                   //if there is an event for this day
                   if(a.getCycle().isOnDayWithStart(a.getStartDate(), currentDay)){
                       limit++;
                       //Create dumb task
                        SplitShareApp.populatedTask.add( a.createDumbTask((Calendar) currentDay.clone()));

                   }
                    currentDay.add(Calendar.DATE, 1);

                }
            }

        }

        MainActivity.addAllToTaskList(SplitShareApp.populatedTask);
    }

}
