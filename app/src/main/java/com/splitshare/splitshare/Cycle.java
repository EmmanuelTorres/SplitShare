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
     *  occurrence
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

    // Constructor for a MONTHLY cycle every dayOfWeek day of the weekOfMonth week
    public Cycle(final int dayOfWeek, final int weekOfMonth, final int s)
    {
        type = cycleType.MONTHLY;
        isNthDay = false;
        dayOfMonth = dayOfWeek;
        nthOccurrence = weekOfMonth;
        setSpacing(s);
    }

    // Constructor for a WEEKLY cycle
    public Cycle(final boolean[] customWeek, final int s)
    {
        type = cycleType.WEEKLY;
        isNthDay = false;
        for (int i = 0; i < 7; i++)
        {
            daysOfWeek.add(i, customWeek[i]);
        }
        setSpacing(s);
    }

    // Constructor for a YEARLY cycle
    // IS_YEARLY is a dummy variable for overloaded constructor to be unique
    public Cycle(final int month, final int day, final boolean IS_YEARLY) {
        type = cycleType.YEARLY;
        dayOfMonth = day;
        // nthOccurrence = month (0 index)
        nthOccurrence = month;
    }
    
    public boolean isOnDayWithStart(Calendar start, Calendar thisDay) {
        Calendar temp = (Calendar)start.clone();
        thisDay.set(Calendar.HOUR_OF_DAY, 0);
        thisDay.set(Calendar.MINUTE, 0);
        thisDay.set(Calendar.SECOND, 0);
        thisDay.set(Calendar.MILLISECOND, 0);
        // always false if this event begins after the day we're checking
        if (thisDay.before(start))
            return false;
        // else check based on type
        if (type == cycleType.ONE_TIME) {
            if (temp.equals(thisDay))
                return true;
        } else if (type == cycleType.DAILY) {
            /*
            // OLD METHOD
            // add spacing until we equal or overshoot
            while (temp.before(thisDay))
                temp.add(Calendar.DATE, spacing);
            // if equal, it's on thisDay.
            if (temp.equals(thisDay))
                return true;
            */
            long days = (thisDay.getTimeInMillis() - start.getTimeInMillis())/86400000; //(1000*60*60*24)
            if (days % spacing == 0)
                return true;
        } else if (type == cycleType.WEEKLY) {
            if(daysOfWeek.get(thisDay.get(Calendar.DAY_OF_WEEK)-1)) {
                // Normalize both days to Sunday
                Calendar temp2 = (Calendar)thisDay.clone();
                temp2.add(Calendar.DATE, 1 - temp2.get(Calendar.DAY_OF_WEEK));
                temp.add(Calendar.DATE, 1 - temp.get(Calendar.DAY_OF_WEEK));

                // count number of weeks between the normalized weeks
                long numWeeks = (temp2.getTimeInMillis() - temp.getTimeInMillis())/(7*86400000);

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
                if (nthOccurrence == thisDay.get(Calendar.WEEK_OF_MONTH) &&
                        dayOfMonth == thisDay.get(Calendar.DAY_OF_WEEK)) {

                    int numMonths1 = start.get(Calendar.MONTH)+12*start.get(Calendar.YEAR);
                    int numMonths2 = thisDay.get(Calendar.MONTH)+12*thisDay.get(Calendar.YEAR);
                    int diff = numMonths2-numMonths1;
                    if (diff % spacing == 0) return true;
                    //TODO: first monday, but week starts on thurs? FIX!
                }
            }
        } else if (type == cycleType.YEARLY) {
            if (nthOccurrence == thisDay.get(Calendar.MONTH) &&
                    dayOfMonth == thisDay.get(Calendar.DAY_OF_MONTH))
                return true;
        }

        return false;
    }

    public int numOcurrencesSinceStart(Calendar start, Calendar thisDay) {
        Calendar normalizedStart = (Calendar)start.clone();
        normalizedStart.set(Calendar.HOUR_OF_DAY, 0);
        normalizedStart.set(Calendar.MINUTE, 0);
        normalizedStart.set(Calendar.SECOND, 0);
        normalizedStart.set(Calendar.MILLISECOND, 0);

        Calendar normalizedThisDay = (Calendar)thisDay.clone();
        normalizedThisDay.set(Calendar.HOUR_OF_DAY, 0);
        normalizedThisDay.set(Calendar.MINUTE, 0);
        normalizedThisDay.set(Calendar.SECOND, 0);
        normalizedThisDay.set(Calendar.MILLISECOND, 0);


        // always 0 if this begins after the day we're checking
        if (thisDay.before(start)) {
            return 0;
        } else if (type == cycleType.ONE_TIME) { // DONE
            if (thisDay.after(start))
                return 1;
            else
                return 0;
        } else if (type == cycleType.DAILY) { // DONE
            long days = (normalizedThisDay.getTimeInMillis() - normalizedStart.getTimeInMillis())/86400000; //(1000*60*60*24)
            return (int)days / spacing;
        } else if (type == cycleType.WEEKLY) { // TODO
            // case thisDay is on the same week as start date
            if (normalizedStart.get(Calendar.WEEK_OF_YEAR) == normalizedThisDay.get(Calendar.WEEK_OF_YEAR) &&
                    (normalizedStart.get(Calendar.YEAR) == normalizedThisDay.get(Calendar.YEAR))) {
                int daysInFirstWeek = 0;
                // count number of days in first week
                for (int i = normalizedStart.get(Calendar.DAY_OF_WEEK) - 1; i < normalizedThisDay.get(Calendar.DAY_OF_WEEK) - 1; i++) {
                    if (daysOfWeek.get(i)) {
                        daysInFirstWeek++;
                    }
                }
                return daysInFirstWeek;
            }

            // CASE: not same week

            int daysInFirstWeek = 0;
            // count number of days in first week
            for (int i = normalizedStart.get(Calendar.DAY_OF_WEEK) - 1; i < 7; i++) {
                if (daysOfWeek.get(i)) {
                    daysInFirstWeek++;
                }
            }

            int daysInFinalWeek = 0;
            // count number of days in end week
            for (int i = 0; i < thisDay.get(Calendar.DAY_OF_WEEK) - 1; i++) {
                if (daysOfWeek.get(i)) {
                    daysInFinalWeek++;
                }
            }
            int daysPerWeek = 0;
            // count days per week
            for (int i = 0; i < 7; i++) {
                if (daysOfWeek.get(i)) {
                    daysPerWeek++;
                }
            }

            // Normalize both days to Sunday
            normalizedThisDay.add(Calendar.DATE, 1 - normalizedThisDay.get(Calendar.DAY_OF_WEEK));
            normalizedStart.add(Calendar.DATE, 1 - normalizedStart.get(Calendar.DAY_OF_WEEK));

            // count number of weeks between the normalized weeks
            long numWeeks = (normalizedThisDay.getTimeInMillis() - normalizedStart.getTimeInMillis())/(7*86400000);
            long weeksBetween = (numWeeks-1)/spacing; // don't count first week
            long midCount = weeksBetween*daysPerWeek;
            long totalCount = daysInFirstWeek + midCount;
            if (numWeeks % spacing == 0) {
                return (int) (totalCount + daysInFinalWeek);
            } else {
                return (int) totalCount;
            }
        } else if (type == cycleType.MONTHLY) {
            int monthDiff = normalizedThisDay.get(Calendar.MONTH) - normalizedStart.get(Calendar.MONTH);
            int yearDiff = normalizedThisDay.get(Calendar.YEAR) - normalizedStart.get(Calendar.YEAR);
            int monthsBetween = yearDiff*12 + monthDiff;

            if (isNthDay) {
                // if happened this month, +1
                // normalize to first day, count months
                if (normalizedThisDay.get(Calendar.DAY_OF_MONTH) > dayOfMonth) {
                    monthsBetween++;
                }
            } else {
                if (nthOccurrence >= normalizedThisDay.get(Calendar.WEEK_OF_MONTH) &&
                        dayOfMonth > normalizedThisDay.get(Calendar.DAY_OF_WEEK)) {
                    monthsBetween++;
                    //TODO: first monday, but week starts on thurs? FIX!
                }
            }
            return monthsBetween;
        } else if (type == cycleType.YEARLY) {
            int yearsBetween = normalizedThisDay.get(Calendar.YEAR) - normalizedStart.get(Calendar.YEAR);
            if (nthOccurrence >= thisDay.get(Calendar.MONTH) &&
                    dayOfMonth > thisDay.get(Calendar.DAY_OF_MONTH))
                yearsBetween++;

            return yearsBetween;
        }

        return 0;
    }

    private void setSpacing(int n) {
        if (n > 0)
            spacing = n;
        else
            spacing = 1;
    }
}
