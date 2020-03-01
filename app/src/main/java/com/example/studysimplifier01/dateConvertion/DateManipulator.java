package com.example.studysimplifier01.dateConvertion;

import java.util.Calendar;
import java.util.Date;

public class DateManipulator {

    // Add days to a date in Java
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    // Add days to a date in Java
    public static Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static int getDayOfWeek() {
        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DAY_OF_WEEK);
        return d-1==0 ? 7: d-1;
    }
}