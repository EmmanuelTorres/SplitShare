package com.splitshare.splitshare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by emmanuel on 10/9/17.
 * Initial implementation by dklug on 10/16/17.
 */

public class Cycle {
    enum cycleType
    {
        ONE_TIME, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    enum Day
    {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    // A Cycle's cycleType reflects it's behaviour
    public cycleType type;

    // For use with WEEKLY cycles, relevant days of week.
    public ArrayList<Boolean> daysOfWeek = new ArrayList<Boolean>();

    /**
     * spacing represents number of days between each event for daily events (every n days)
     * or number of weeks between weekly events (monday and tuesday every n weeks). Finally,
     * represents spacing in months for monthly events.
     *
     * Spacing 1 means every, spacing 0 is undefined! spacing 2 means every other.
     */
    public int spacing;

    /**
     * If true, is nth day of month for MONTHLY events, marked by dayOfMonth.
     * If false, is nthOccurrence of first true daysOfWeek day (e.g., 2nd wednesday)
     */
    public boolean isNthDay;

    public int dayOfMonth;
    public int nthOccurrence;

    // Default constructor makes a ONE_TIME cycle
    public Cycle()
    {
        type = cycleType.ONE_TIME;
    }

    // Constructor for a DAILY cycle
    public Cycle(final int s)
    {
        type = cycleType.DAILY;
        setSpacing(s);
    }

    // Constructor for a MONTHLY cycle every nth day
    public Cycle(final int d, final int s)
    {
        type = cycleType.MONTHLY;
        isNthDay = true;
        dayOfMonth = d;
        setSpacing(s);
    }

    // Constructor for a WEEKLY cycle
    public Cycle(final boolean[] customWeek, final int o, final int s)
    {
        type = cycleType.WEEKLY;
        isNthDay = false;
        for (int i = 0; i < 7; i++)
        {
            daysOfWeek.add(i, customWeek[i]);
        }
        nthOccurrence = o;
        setSpacing(s);
    }

    // TODO: constructor for yearly events

    // TODO: test this!!
    public boolean isOnDayWithStart(Calendar start, Calendar thisDay) {
        Calendar temp = start;
        thisDay.set(Calendar.HOUR_OF_DAY, 0);
        thisDay.set(Calendar.MINUTE, 0);
        thisDay.set(Calendar.SECOND, 0);
        thisDay.set(Calendar.MILLISECOND, 0);
        // always false if this event is before the day we're checking
        if (thisDay.before(start))
            return false;
        // else check based on type
        if (type == cycleType.ONE_TIME) {
            if (temp.equals(thisDay))
                return true;
        } else if (type == cycleType.DAILY) {
            // add spacing until we equal or overshoot
            while (temp.before(thisDay))
                temp.add(Calendar.DATE, spacing);

            // if equal, it's on thisDay.
            if (temp.equals(thisDay))
                return true;
        } else if (type == cycleType.WEEKLY) {
            if(daysOfWeek.get(thisDay.get(Calendar.DAY_OF_WEEK)-1)) {
                // Normalize both days to Sunday
                Calendar temp2 = thisDay;
                temp2.add(Calendar.DATE, 1 - temp2.get(Calendar.DAY_OF_WEEK));
                temp.add(Calendar.DATE, 1 - temp.get(Calendar.DAY_OF_WEEK));

                // count number of weeks between the normalized weeks
                long numWeeks = (temp2.getTimeInMillis() - temp.getTimeInMillis())/(7*(1000*60*60*24));

                // if same week, or "spacing" weeks later, return true
                if (numWeeks % spacing == 0)
                    return true;
            }
        } else if (type == cycleType.MONTHLY) {
            if (isNthDay) {
                if (thisDay.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                    int numMonths1 = start.get(Calendar.MONTH)+12*start.get(Calendar.YEAR);
                    int numMonths2 = thisDay.get(Calendar.MONTH)+12*thisDay.get(Calendar.YEAR);
                    int diff = numMonths2-numMonths1;
                    if (diff % spacing == 0) return true;
                }
            } else {
                if (start.get(Calendar.WEEK_OF_MONTH) == thisDay.get(Calendar.WEEK_OF_MONTH) &&
                        start.get(Calendar.DAY_OF_WEEK) == thisDay.get(Calendar.DAY_OF_WEEK)) {

                    int numMonths1 = start.get(Calendar.MONTH)+12*start.get(Calendar.YEAR);
                    int numMonths2 = thisDay.get(Calendar.MONTH)+12*thisDay.get(Calendar.YEAR);
                    int diff = numMonths2-numMonths1;
                    if (diff % spacing == 0) return true;
                }
            }
        } else if (type == cycleType.YEARLY) {
            if (start.get(Calendar.DAY_OF_YEAR) == thisDay.get(Calendar.DAY_OF_YEAR))
                return true;
        }

        return false;
    }

    private void setSpacing(int n) {
        if (n > 0)
            spacing = n;
        else
            spacing = 1;
    }
}
