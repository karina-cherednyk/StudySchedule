package com.example.studysimplifier01.dateConvertion;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {

    private static String delimiter = "/";
    @TypeConverter
    public static String toDateStr(int date){
        String day, month, year;
        String sDate = Integer.toString(date);
        if(sDate.length() == 5) {
            month = "0"+sDate.substring(0,1);
            day = sDate.substring(1,3);
            year = sDate.substring(3,5);
        }
        else {
            month = sDate.substring(0,2);
            day = sDate.substring(2,4);
            year = sDate.substring(4,6);
        }
        return day + delimiter + month + delimiter +year;
    }
    public static String toDateStr(int dayi, int monthi, int yeari){
        String day = dayi+"";
        String month = monthi +"";
        String year = yeari +"";
        if(day.length()!=2) day = "0"+day;
        if(month.length()!=2) month = "0"+month;
        year = year.substring(2);

        return day + delimiter + month + delimiter +year;
    }

    public static void setDelimiter(String d){
        delimiter = d;
    }
    @TypeConverter
    public static int fromDate(String date){
        date = date.replaceAll(delimiter,"");
        int res = Integer.parseInt(date.substring(2,4)) * 10000;
        res += Integer.parseInt(date.substring(0,2)) * 100;
        res+= Integer.parseInt(date.substring(4,6));
        return res;
    }
    public static String toDateStr(Date date){
        return toDateStr(toDate(date));
    }
    public static int toDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR)%100;
        return month*10000+day*100+year;
    }
    public static String getDelimiter(){
        return delimiter;
    }

    public static int toDate(int dayi, int monthi, int yeari) {
        int res = monthi*10000;
        res +=dayi*100;
        res+=yeari;
        return  res;
    }
    public static int getDay(int date){
        return (date/100) % 100;
    }
    public static int getMonth(int date){
        return date/10000;
    }
    public static int getYear(int date){
        return 2000+date % 100;
    }

    public static int getWeekDay(int day, int month, int year){
        Calendar cldr = Calendar.getInstance();
        cldr.set(year,month-1,day);
        return cldr.get(Calendar.DAY_OF_WEEK);
    }
    public static int getWeekDay(int date){
        Calendar cldr = Calendar.getInstance();
        cldr.set(getYear(date),getMonth(date)-1,getDay(date));
        return cldr.get(Calendar.DAY_OF_WEEK);
    }

    public static String toDateStrFull(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR)%100;
        int hours = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);

        return norm2(hours)+":"+norm2(minutes)+" "+norm2(day) + delimiter + norm2(month) + delimiter +norm2(year);
    }
    public static String norm2(int i){
        return i/10==0 ? "0"+i : ""+i;
    }
}
