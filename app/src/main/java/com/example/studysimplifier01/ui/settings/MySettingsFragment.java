package com.example.studysimplifier01.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.studysimplifier01.dateConvertion.WeekDayConverter;
import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.R;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class MySettingsFragment extends PreferenceFragmentCompat {
    DaysViewModel viewModel;
    MyToast t;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        t = new MyToast(getContext());
        viewModel = new ViewModelProvider(this).get(DaysViewModel.class);
    }


    SharedPreferences.OnSharedPreferenceChangeListener prefListener =
            (prefs, key) -> {
                if (key.equals(Values.ALARM)) {
                    //"8:30-10:00"
                    boolean on = prefs.getBoolean(key,false);
                        viewModel.getLessons().observe(this, lessons -> {
                            for( Lesson l : lessons )
                                if(on)setClockAlarm(getContext(),l);
                                else cancelClockAlarm(getContext(),l.getLessonID());
                        });

                }
                else if (key.equals(Values.LANG)){
                    String lang = prefs.getString(Values.LANG, "");
                    Locale myLocale = new Locale(lang);
                    Resources res = getActivity().getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Locale.setDefault(myLocale);
                    getActivity().recreate();
                }
            };
    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    public static final int GAP = 5;
    public static void setClockAlarm(Context context,Lesson lesson){

        String lessonName = lesson.getLesson();
        String interval = lesson.getTime();
        int ID = (int)lesson.getLessonID();
        int dayOfWeek = lesson.getDayOfWeek() % 7 + 1;

        int hours = Integer.parseInt(interval.substring(0,interval.indexOf(':')));
        int minutes = Integer.parseInt(interval.substring(interval.indexOf(':')+1,interval.indexOf('-')));



        if(minutes - GAP <0){
            minutes = 60 + minutes - GAP;
            hours--;
        }
        else minutes-= GAP;

        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(ALARM_SERVICE);


        Intent myIntent = new Intent(context.getApplicationContext(), MyReceiver.class);
        myIntent.setAction(Values.ALARM);

        myIntent.putExtra(Values.RECEIVER_LESSON_NAME,lessonName);
        myIntent.putExtra(Values.RECEIVER_INTERVAL,interval);
        myIntent.putExtra(Values.RECEIVER_ID,ID);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ID, myIntent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,minutes);

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int days = dayOfWeek - weekday;
        if(days<0) days +=7;
        calendar.add(Calendar.DAY_OF_YEAR, days);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }
    public static void cancelClockAlarm(Context context,long lID){

        int ID = (int)lID;
        Intent myIntent = new Intent(context.getApplicationContext(), MyReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ID, myIntent,0);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }
}