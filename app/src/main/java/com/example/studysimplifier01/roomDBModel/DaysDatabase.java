package com.example.studysimplifier01.roomDBModel;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.example.studysimplifier01.dateConvertion.DateManipulator;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;
import com.example.studysimplifier01.roomDBModel.entities.ParticularLesson;

@Database(entities={
        Lesson.class, ParticularLesson.class
}, version = 9, exportSchema = false
)
public abstract class DaysDatabase extends RoomDatabase {
    public abstract  DaysDao daysDao();


    private static DaysDatabase INSTANCE;
    public static DaysDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (DaysDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DaysDatabase.class, "days_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final DaysDao mDao;

        PopulateDbAsync(DaysDatabase db) {
            mDao = db.daysDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

           if(mDao.getAnyLesson().length == 0)
               mDao.insertLesson(new Lesson("Lesson example","Professor example", "1-215","08:30-09:50", DateManipulator.getDayOfWeek()));

            return null;
        }
    }

}