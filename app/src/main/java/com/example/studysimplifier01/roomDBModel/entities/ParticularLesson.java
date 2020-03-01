package com.example.studysimplifier01.roomDBModel.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.studysimplifier01.dateConvertion.DateConverter;

@Entity(
)
public class ParticularLesson implements Comparable<ParticularLesson>{
    public String taskForToday;
    @ColumnInfo(index = true)
    public long lessonID;

    @PrimaryKey(autoGenerate = true)
    public long particularLessonID;

    public int date;
    public boolean completed;

    public ParticularLesson(long lessonID, int date, String taskForToday){
        this.lessonID = lessonID;
        this.date = date;
        this.taskForToday = taskForToday;
    }
    public String toString(){
        return DateConverter.toDateStr(date)+" - \n"+taskForToday;
    }

    @Override
    public int compareTo(ParticularLesson that) {
        long d = (this.lessonID - that.lessonID);
        if(d<0) return -1;
        if(d>0) return 1;
        return 0;
    }
}