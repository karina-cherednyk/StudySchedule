package com.example.studysimplifier01.roomDBModel;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.example.studysimplifier01.roomDBModel.entities.Lesson;
import com.example.studysimplifier01.roomDBModel.entities.ParticularLesson;

@Database(entities={
        Lesson.class, ParticularLesson.class
}, version = 8, exportSchema = false
)
public abstract class DaysDatabase extends RoomDatabase {
    public abstract  DaysDao daysDao();

    //create singleton
    private static DaysDatabase INSTANCE;
    public static DaysDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (DaysDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DaysDatabase.class, "days_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            .fallbackToDestructiveMigration()
                            .addCallback(  new RoomDatabase.Callback(){
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    new populateDB(INSTANCE,context).execute();
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }




    private static class populateDB extends AsyncTask<Void,Void,Void>{

    private static String[] lessonTime = {
         "8:30-10:00","10:00-11:20","11:40-13:00","13:30-14:50","15:00-16:20"
    };
    private static String[] lesson = {
            "OC Android", "OC Android (Practice)", //1 2
            "English", //3
            "Algorithm theory","Algorithm theory (Practice)", //4 5
            "Information search","Information search (Practice)", //6 7
            "Object Oriented Programming","OOP (Practice)", //8 9
            "Maths thinking", "Maths thinking (Practice)" // 10 11
    };

    //long lessonID, int date, String taskForToday
    private static ParticularLesson[] particularLessons = {
            new ParticularLesson(2,20520, "Create many Activities"),
            new ParticularLesson(7,12820,"Boolean search"),
            new ParticularLesson(9,12920,"Segment and Point, Copy constructor"),
            new ParticularLesson(9,20520, "Private and public properties, lab 2")
    };
    private static String[] professor = {
            "A.I. Cheredarchuk", "A.I. Cheredarchuk",
            "L.M. Zaleska-Zarichna",
            "O.S. Pylavska", "P.G. Prokofyev",
            "A.M. Glybovets","Y.V. Vozniuk",
            "V.V. Bublik", "V.V. Gorborukov",
            "M.A. Dudenko", "M.A. Oliynyk"
    };
    private  static String[] classroom = {
            "1-318","1-310",
            "2-404",
            "1-225","3-302",
            "1-223","1-108",
            "1-223","1-307",
            "1-225","1-208"
    } ;
    private  static int[] dayOfWeek = {
            1,3,
            1,
            2,5,
            3,2,
            3,3,
            5,5
    } ;
    private static int[] timeID = {
            1,5,
            2,
            2,1,
            1,3,
            2,4,
            3,4
    };

    private final DaysDao dao;

        public populateDB(DaysDatabase instance, Context context) {
            dao = instance.daysDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(dao.getAnyLesson().length == 0 ){
                for(int i=0; i<lesson.length; i++){
                    //Lesson(String lesson, String professor, String classRoom, long timeID, int dayOfWeek)
                    Lesson l = new Lesson(lesson[i],professor[i],classroom[i],lessonTime[timeID[i]-1],dayOfWeek[i]);
                    dao.insertLesson(l);
                }
            }
            if(dao.getAnyPLesson().length == 0){
                for(int i=0; i< particularLessons.length; i++)
                    dao.insertParticularLesson(particularLessons[i]);
            }
            return null;
        }
    }

}