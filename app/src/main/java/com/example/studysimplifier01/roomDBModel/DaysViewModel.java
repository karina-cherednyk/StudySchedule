package com.example.studysimplifier01.roomDBModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;
import com.example.studysimplifier01.roomDBModel.entities.ParticularLesson;
import com.example.studysimplifier01.ui.settings.MySettingsFragment;

import java.util.List;

public class DaysViewModel extends AndroidViewModel {
    private DaysDao dao;


    public DaysViewModel(Application application){
        super(application);
        DaysDatabase db = DaysDatabase.getDatabase(application);
        dao = db.daysDao();

    }

    public LiveData<List<Lesson>> getLessons(int dayOfWeek) {  return dao.getAllLessons(dayOfWeek); }

    public LiveData<List<Lesson>> getLessons() {  return dao.getAllLessons(); }


    public LiveData<List<ParticularLesson>> getPLessons(int first, int last) {return dao.getPLessons(first,last);}
    public LiveData<List<ParticularLesson>> getPLessons(long lessonID, int first) {
        return dao.getPLessons(lessonID, first);
    }

    public void update(long particularLessonID, String taskForToday, boolean completed) {
        new updatePLesson().execute(new ARGS(particularLessonID,taskForToday,completed));
    }


    public void insert(ParticularLesson particularLesson) {
        new insertPLesson().execute(particularLesson);
    }
    public void deleteParticularLesson(long id){
        new deletePLesson().execute(id);
    }

    public void insert(Lesson lesson) {
        if(alarm()){
            MySettingsFragment.setClockAlarm(getApplication(),lesson);
        }
        new insertLesson().execute(lesson);
    }
    private boolean alarm(){
        return PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean(Values.ALARM,false);
    }
    public void deleteLesson(long lessonID) {
        if( alarm()){
            MySettingsFragment.cancelClockAlarm(getApplication(),lessonID);
        }

        new deleteLesson().execute(lessonID);
    }
    public void update(long lessonID, Lesson newLesson) {
        if(alarm()){
            MySettingsFragment.cancelClockAlarm(getApplication(),lessonID);
            MySettingsFragment.setClockAlarm(getApplication(),newLesson);
        }
        new updateLesson().execute(new ARGS(lessonID,newLesson));
    }
    public void insertAll(List<Lesson> lessons) {
        if(alarm()) for(Lesson lesson : lessons)  MySettingsFragment.setClockAlarm(getApplication(),lesson);

        new insertLesson().execute(lessons.toArray(new Lesson[lessons.size()]));
    }
    public void deleteAll(){
        if( alarm()) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplication()).edit();
            editor.putBoolean(Values.ALARM,false);
            editor.commit();
        }
        new deleteAll().execute();
    }

    private class ARGS{
        long id;
        String task;
        boolean completed;
        Lesson lesson;
        ARGS(long id,String task, boolean completed){
            this.id = id;
            this.task = task;
            this.completed = completed;
        }
        ARGS(long id,Lesson lesson){
            this.id = id;
            this.lesson = lesson;
        }
    }
    private class deletePLesson extends AsyncTask<Long,Void,Void> {
        @Override
        protected Void doInBackground(Long... longs) {
            dao.deleteParticularLesson(longs[0]);
            return null;
        }
    }
    private class deleteLesson extends AsyncTask<Long,Void,Void> {
        @Override
        protected Void doInBackground(Long... longs) {
            dao.deleteLesson(longs[0]);
            return null;
        }
    }
    private class insertPLesson extends AsyncTask<ParticularLesson,Void,Void> {
        @Override
        protected Void doInBackground(ParticularLesson... particularLessons) {
            dao.insertParticularLesson( particularLessons[0]);
            return null;
        }
    }
    private class insertLesson extends AsyncTask<Lesson,Void,Void> {
        @Override
        protected Void doInBackground(Lesson... lessons) {
            for(Lesson l : lessons)
             dao.insertLesson( l);
            return null;
        }
    }
    private class updatePLesson extends AsyncTask<ARGS,Void,Void> {

        @Override
        protected Void doInBackground(ARGS... args) {
            dao.updateParticularLesson(args[0].id,args[0].task,args[0].completed);
            return null;
        }
    }
    private class updateLesson extends AsyncTask<ARGS,Void,Void> {

        @Override
        protected Void doInBackground(ARGS... args) {
            Lesson l = args[0].lesson;
            dao.updateLesson(args[0].id,l.getLesson(),l.getProfessor(),l.getClassRoom(),l.getTime(),l.getDayOfWeek());
            return null;
        }
    }

    private class deleteAll extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteLessons();
            dao.deleteParticularLessons();
            return null;
        }
    }
}
