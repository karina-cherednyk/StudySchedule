package com.example.studysimplifier01.dateConvertion;

import android.content.Context;


import com.example.studysimplifier01.R;

public class WeekDayConverter {
        public WeekDayConverter(Context context){
            String[] days = {
                   context.getString(R.string.mon),
                    context.getString(R.string.tue),
                    context.getString(R.string.wen),
                    context.getString(R.string.thu),
                    context.getString(R.string.fri),
                    context.getString(R.string.sat),
                    context.getString(R.string.sun)
            }  ;
            this.days = days;
        }

        public String[] days ;

        public String toDayOfWeek(int day){
            return days[day-1];
        }

        public int fromDayOfWeek(String day){
            for(int i=0; i<days.length; i++)
                if(days[i].equals(day)) return i+1;
            return 0;
        }

}
