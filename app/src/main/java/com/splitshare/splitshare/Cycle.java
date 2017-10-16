package com.splitshare.splitshare;

/**
 * Created by emmanuel on 10/9/17.
 * Initial implementation by dklug on 10/16/17.
 */

public class Cycle {
    enum cycleType
    {
        WEEKLY, DAILY, CUSTOM, DAYRANGE
    }

    enum Day
    {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // A Cycle's cycleType reflects it's behaviour
    cycleType type;

    // Week represents Monday -> Sunday
    int[] week = new int[7];

    // This int represents the days in between for a DAYRANGE cycle
    private int range;
    // This int represents the last activated day for use in finding the next week
    private int prevDay;

    // Default constructor makes a DAILY cycle
    public Cycle()
    {
        type = cycleType.DAILY;
        for (int i : week)
        {
            week[i] = 1;
        }
    }

    // Constructor for a DAYRANGE cycle
    public Cycle(final int i)
    {
        type=cycleType.DAYRANGE;
        range = i;
        for (int count = 0; count<7; count+=range)
        {
            week[count] = 1;
            prevDay = count;
        }
    }

    // Constructor for a CUSTOM cycle
    public Cycle(final int[] customWeek)
    {
        type = cycleType.CUSTOM;
        week = customWeek;
    }

    // Constructor for a WEEKLY cycle
    public Cycle(Day d)
    {
        type = cycleType.WEEKLY;
        week[d.ordinal()] = 1;
    }

    public int[] getWeek()
    {
        return week;
    }

    // For use in calculating the next week if the type is DAYRANGE
    public void nextWeek()
    {
        if (type==cycleType.DAYRANGE)
        {
            // Reset the week
            for (int i : week)
            {
                week[i] = 0;
            }

            // Calculate the next week
            for (int count = prevDay+range-7; count<7; count+=range)
            {
                week[count] = 1;
            }
        }
    }
}
