package com.example.studysimplifier01.ui.edit;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studysimplifier01.R;
import com.example.studysimplifier01.dateConvertion.WeekDayConverter;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class EditScheduleAdapter extends RecyclerView.Adapter<EditScheduleAdapter.EditViewHolder> {



    private LayoutInflater inflater;
    private Context context;
    private DaysViewModel viewModel;
    private List<Lesson> list;
    private Set<String> times = new TreeSet<>();
    private Set<String> professors = new TreeSet<>();
    private MyToast t;


    public EditScheduleAdapter(Context context, DaysViewModel viewModel) {
        this.inflater = LayoutInflater.from(context);;
        this.context = context;
        this.viewModel = viewModel;
        this.t = new MyToast(context);

        viewModel.getLessons().observe((LifecycleOwner) context, lessons -> {
            list = lessons;
            setUniqueTimes();
            notifyDataSetChanged();
        });

    }
    private void setUniqueTimes(){
        for(Lesson lesson : list) {
            times.add(lesson.getTime());
            professors.add(lesson.getProfessor());
        }
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditViewHolder(inflater.inflate(R.layout.edit_lesson,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EditViewHolder holder, int position) {
            holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size()+1;
    }





    public class EditViewHolder extends RecyclerView.ViewHolder {
        int position;
        EditText lessonEdit;
        AutoCompleteTextView professorEdit;
        EditText classRoomEdit;
        AutoCompleteTextView timeEdit;
        Spinner daySpinner;
        Button toggle;
        LinearLayout layout;
        Lesson lesson;
        ImageButton disposeButton;
        ImageButton commitButton;
        WeekDayConverter wdk;




        public EditViewHolder(@NonNull View itemView)  {
            super(itemView);
            lessonEdit = itemView.findViewById(R.id.lessonEdit);
            professorEdit = itemView.findViewById(R.id.professor_edit);
            classRoomEdit = itemView.findViewById(R.id.classroomEdit);
            timeEdit = itemView.findViewById(R.id.timeEdit);
            daySpinner = itemView.findViewById(R.id.daySpinner);
            toggle = itemView.findViewById(R.id.toggleLessonButton);
            layout = itemView.findViewById(R.id.edit_table_layout);
            disposeButton = itemView.findViewById(R.id.editDisposeButton);
            commitButton = itemView.findViewById(R.id.editCommitButton);
            wdk = new WeekDayConverter(context);
            toggle.setOnClickListener(listListener);
        }
        public void bindView(int position){
            this.position = position-1;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, wdk.days);
            daySpinner.setAdapter(adapter);
            professorEdit.setAdapter(new ArrayAdapter<>(context,  android.R.layout.simple_dropdown_item_1line, new ArrayList<>(professors)));
            timeEdit.setAdapter(new ArrayAdapter<>(context,  android.R.layout.simple_dropdown_item_1line, new ArrayList<>(times)));

            if(this.position > -1)bindListView();
            else bindAddView();

            layout.setVisibility(View.GONE);
        }
        private void bindListView(){
            lesson = list.get(position);
            toggle.setText(lesson.getLesson());
            lessonEdit.setText(lesson.getLesson());
            professorEdit.setText(lesson.getProfessor());
            classRoomEdit.setText(lesson.getClassRoom());
            daySpinner.setSelection(wdk.fromDayOfWeek(wdk.toDayOfWeek(lesson.getDayOfWeek()))-1);
            timeEdit.setText(lesson.getTime());

            commitButton.setOnClickListener(listListener);
            disposeButton.setOnClickListener(listListener);
        }
        private void bindAddView(){
            toggle.setText(context.getString(R.string.add_new_lesson));
            lessonEdit.setText("");
            professorEdit.setText("");
            classRoomEdit.setText("");
            timeEdit.setText("00:00-00:00");

            commitButton.setOnClickListener(addListener);
            disposeButton.setOnClickListener(addListener);
        }
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == commitButton){
                   String lesson = lessonEdit.getText().toString();
                   String professor = professorEdit.getText().toString();
                   String room = classRoomEdit.getText().toString();
                   String time = timeEdit.getText().toString();
                   if(time.length()<6 || time.charAt(2) != ':' || time.charAt(5) != '-') {
                       t.toast(context.getString(R.string.wrong_format));
                       timeEdit.setText("00:00-00:00");
                       return;
                   }

                   String dayOfWeek = daySpinner.getSelectedItem().toString();
                   viewModel.insert(new Lesson(lesson,professor,room,time,wdk.fromDayOfWeek(dayOfWeek)));
                }
                else {
                    lessonEdit.setText("");
                    professorEdit.setText("");
                    classRoomEdit.setText("");
                    timeEdit.setText("");
                }

            }
        };
        View.OnClickListener listListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == commitButton){
                    String lessonName = lessonEdit.getText().toString();
                    String professor = professorEdit.getText().toString();
                    String room = classRoomEdit.getText().toString();
                    String time = timeEdit.getText().toString();
                    String dayOfWeek = daySpinner.getSelectedItem().toString();
                    if(time.length()<6 || time.charAt(2) != ':' || time.charAt(5) != '-') {
                        t.toast(context.getString(R.string.wrong_format));
                        timeEdit.setText("00:00-00:00");
                        return;
                    }
                    viewModel.update(lesson.getLessonID(),new Lesson(lessonName,professor,room,time,wdk.fromDayOfWeek(dayOfWeek)));
                }
                else if(v == disposeButton){
                    viewModel.deleteLesson(lesson.getLessonID());
                }
                else {
                   if(layout.getVisibility() == View.VISIBLE) layout.setVisibility(View.GONE);
                   else layout.setVisibility(View.VISIBLE);
                }

            }
        };

    }

}
