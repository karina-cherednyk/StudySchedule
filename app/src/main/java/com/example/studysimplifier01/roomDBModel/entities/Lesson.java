package com.example.studysimplifier01.roomDBModel.entities;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.dateConvertion.WeekDayConverter;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

import java.io.Serializable;


@Entity
public class Lesson implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long lessonID;

    private String lesson;

    private String professor;

    private String classRoom;

    private String time;

    private int dayOfWeek;



    public Lesson(String lesson, String professor, String classRoom, String time, int dayOfWeek){
        this.lesson = lesson;
        this.professor = professor;
        this.classRoom = classRoom;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }

    public static String str(Context context, Lesson lesson){

        return  context.getString(R.string.lesson)+lesson.lesson+
                context.getString(R.string.professor)+lesson.professor+
                context.getString(R.string.classroom)+lesson.classRoom+
                context.getString(R.string.time)+lesson.time;
    }

    public void setLessonID(long lessonID) {
        this.lessonID = lessonID;
    }

    public long getLessonID() {
        return lessonID;
    }

    public String getLesson() {
        return lesson;
    }

    public String getProfessor() {
        return professor;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public String getTime() {
        return time;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }


    public static final class Fields {
        public static final String ID = "lessonID";
        public static final String lESSON = "lesson";
        public static final String PROFESSOR = "professor";
        public static final String CLASSROOM = "classroom";
        public static final String TIME = "time";
        public static final String DAY_OF_WEEK = "dayOfWeek";
    }

    public static BsonDocument convert(Lesson lesson){
        BsonDocument doc = new BsonDocument();
        doc.put(Fields.lESSON, new BsonString(lesson.lesson));
        doc.put(Fields.PROFESSOR, new BsonString(lesson.professor));
        doc.put(Fields.CLASSROOM, new BsonString(lesson.classRoom));
        doc.put(Fields.TIME, new BsonString(lesson.time));
        doc.put(Fields.DAY_OF_WEEK, new BsonInt32(lesson.dayOfWeek));
        return doc;
    }
    public static Lesson convert(BsonDocument doc){
        return new Lesson(
                doc.getString(Fields.lESSON).getValue(),
                doc.getString(Fields.PROFESSOR).getValue(),
                doc.getString(Fields.CLASSROOM).getValue(),
                doc.getString(Fields.TIME).getValue(),
                doc.getInt32(Fields.DAY_OF_WEEK).getValue()
        );
    }
}