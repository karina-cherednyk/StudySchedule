package com.example.studysimplifier01.ui.show;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.dateConvertion.DateConverter;
import com.example.studysimplifier01.dateConvertion.DateManipulator;
import com.example.studysimplifier01.R;
import com.example.studysimplifier01.dateConvertion.WeekDayConverter;
import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;
import com.example.studysimplifier01.roomDBModel.entities.ParticularLesson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>{

    LayoutInflater inflater;
    Context context;
    DaysViewModel viewModel;
    List<List<Lesson>>lessons = new ArrayList< List<Lesson>>();
    List<ParticularLesson> particularLessons = new ArrayList<>();
    Date firstDateOfWeek,lastDateOfWeek;
    int curDayOfWeek,first,last;
    DateManipulator dm = new DateManipulator();
    WeekDayConverter wdk;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CardAdapter(Context context, DaysViewModel viewModel, int curDayOfWeek, Date firstDateOfWeek, Date lastDateOfWeek) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.viewModel = viewModel;
        this.curDayOfWeek = curDayOfWeek;
        first = DateConverter.toDate(firstDateOfWeek);
        last = DateConverter.toDate(lastDateOfWeek);
        this.firstDateOfWeek = firstDateOfWeek;
        this.lastDateOfWeek = lastDateOfWeek;
        wdk = new WeekDayConverter(context);


        for(int i=0; i<7; i++){
            final int dayOfWeek = i+1;
            viewModel.getLessons(dayOfWeek).observe((LifecycleOwner) context, lessonList -> {
                if(lessonList.isEmpty()) return;
                lessons.add(lessonList);
                lessons.sort(c);
               if(dayOfWeek ==6) notifyDataSetChanged();
            });
            viewModel.getPLessons(first,last).observe((LifecycleOwner) context, pLessons -> {
                particularLessons = pLessons;
                Collections.sort(particularLessons);
                notifyDataSetChanged();
            });
        }

    }
    Comparator<List<Lesson>> c = (o1, o2) -> (o1.get(0).getDayOfWeek() - o2.get(0).getDayOfWeek());

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = inflater.inflate(R.layout.card,parent, false );
        return new CardViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
       holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public MyListView list;
        int position;
        TextView dayOfWeekView;
        String date;
        List<Lesson> thisDayLessons;



        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardview);
            list = itemView.findViewById(R.id.card_list);
            dayOfWeekView = itemView.findViewById(R.id.card_dayOfWeek);

        }
        private class MyArrayAdapter extends ArrayAdapter{

            int resource;
            public MyArrayAdapter(@NonNull Context context, int resource, @NonNull List< Lesson> objects) {
                super(context, resource, objects);
                this.resource = resource;
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Lesson lesson = ( Lesson) getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
                    for (ParticularLesson pl : particularLessons) {
                        if (pl.lessonID == lesson.getLessonID() && !pl.completed) {
                          //  convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_error));
                           convertView.setBackground(ContextCompat.getDrawable(context,R.drawable.error_list));

                            break;
                        }
                    }
                }

                TextView lessonName = convertView.findViewById(R.id.card_show_lesson);
                TextView professorName = convertView.findViewById(R.id.card_show_professor);
                TextView classroom = convertView.findViewById(R.id.card_show_classroom);
                TextView time = convertView.findViewById(R.id.card_show_time);

                lessonName.setText(lesson.getLesson());
                professorName.setText(lesson.getProfessor());
                classroom.setText(lesson.getClassRoom());
                time.setText(lesson.getTime());

                return convertView;
            }
        }
        public void setText() {
            thisDayLessons =  lessons.get(position);
            List<String> l = new LinkedList<>();
            MyArrayAdapter adapter = new MyArrayAdapter(context,R.layout.one_lesson_show,thisDayLessons);
            list.setAdapter(adapter);

            if(lessons.get(position).get(0).getDayOfWeek() == curDayOfWeek) {
                //TODO
//                Resources.Theme theme = context.getTheme();
//                TypedValue val = new TypedValue();
//                theme.resolveAttribute(R.attr.colorPrimaryVariant, val, true);
//                card.setBackgroundColor(ContextCompat.getColor(context, val.data));
            }
            date = DateConverter.toDateStr(dm.addDays(firstDateOfWeek, lessons.get(position).get(0).getDayOfWeek()-1));


            list.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(context, HWActivity.class);
                Lesson lesson = thisDayLessons.get(position);
                intent.putExtra(Values.CARD_LESSON_ID, lesson.getLessonID());
                intent.putExtra(Values.CARD_LESSON_MIN_DATE,first);
                intent.putExtra(Values.CARD_LESSON_DATE,DateConverter.fromDate(date));
                intent.putExtra(Values.CARD_LESSON_NAME,lesson.getLesson());
                context.startActivity(intent);
            });



            dayOfWeekView.setText(wdk.toDayOfWeek(lessons.get(position).get(0).getDayOfWeek())+ ", "+date );
        }

        public void bindView(int pos){
            position = pos;
            setText();
        }

    }
}
