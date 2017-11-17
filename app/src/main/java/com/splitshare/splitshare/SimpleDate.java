package com.splitshare.splitshare;

import java.util.Calendar;

/**
 * Created by armando on 11/17/17.
 */

public class SimpleDate {
    public int day;
    public int month;
    public int year;
    SimpleDate() {

    }
    SimpleDate(int m, int d, int y) {
        day = d;
        month = m;
        year = y;
    }

    public Calendar toCalendar() {
        Calendar ed = Calendar.getInstance();
        ed.set(Calendar.DAY_OF_MONTH, day);
        ed.set(Calendar.MONTH, month);
        ed.set(Calendar.YEAR, year);
        return ed;
    }
}
