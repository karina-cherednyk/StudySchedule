package com.example.studysimplifier01.roomDBModel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.studysimplifier01.roomDBModel.entities.*;

import java.util.List;


@Dao
public interface DaysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLesson(Lesson lesson);

    @Insert
    void insertParticularLesson(ParticularLesson lesson);

    @Query("SELECT * FROM ParticularLesson LIMIT 1")
    ParticularLesson[] getAnyPLesson();

    @Query("SELECT * FROM ParticularLesson")
    LiveData<List<ParticularLesson>> getPLessons();

    @Query("SELECT * FROM ParticularLesson WHERE ParticularLesson.date BETWEEN :first AND :last")
    LiveData<List<ParticularLesson>> getPLessons(int first, int last);

    @Query("SELECT * FROM ParticularLesson WHERE ParticularLesson.lessonID =:lesson_id AND ParticularLesson.date >= :first ")
    LiveData<List<ParticularLesson>> getPLessons(long lesson_id, int first);

    @Query("UPDATE ParticularLesson SET taskForToday = :task, completed =:completed WHERE particularLessonID =:id")
    void updateParticularLesson(long id, String task, boolean completed);

    @Query("DELETE FROM ParticularLesson WHERE particularLessonID =:id")
    void deleteParticularLesson(long id);

    @Query("DELETE FROM Lesson")
    void deleteLessons();

    @Query("DELETE FROM ParticularLesson")
    void deleteParticularLessons();


    @Query("SELECT * FROM Lesson WHERE dayOfWeek =:dayOfWeek")
    LiveData<List<Lesson>> getLessons(int dayOfWeek);


    @Query("SELECT * FROM Lesson LIMIT 1")
    Lesson[] getAnyLesson();


    @Query("SELECT * FROM Lesson WHERE Lesson.dayOfWeek=:dayOfWeek")
    LiveData<List<Lesson>> getAllLessons(int dayOfWeek);
    @Query("SELECT * FROM Lesson ")
    LiveData<List<Lesson>> getAllLessons();

    @Query("UPDATE Lesson SET lesson = :lesson,professor = :professor, classRoom = :classroom , time = :time, dayOfWeek =:day  WHERE lessonID =:id")
    void updateLesson(long id, String lesson, String professor, String classroom, String time, int day);

    @Query("DELETE FROM Lesson WHERE lessonID =:id")
    void deleteLesson(Long id);
}
