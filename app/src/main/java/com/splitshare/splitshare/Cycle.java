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
        for (int count = 0; count<7; count+=i)
        {
            week[count] = 1;
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
}
