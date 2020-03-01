package com.example.studysimplifier01.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.studysimplifier01.main.MainActivity;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.R;
import com.example.studysimplifier01.mongoDBModel.collections.Schedule;

import com.example.studysimplifier01.mongoDBModel.collections.User;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;
import com.mongodb.stitch.android.core.StitchAppClient;


import java.util.LinkedList;
import java.util.List;



public class ShareFragment extends Fragment {

        private Button uploadScheduleButton;
        private EditText scheduleNameEdit;
        private DaysViewModel viewModel;
        private TextView scheduleNameText;

        private Button setScheduleButton;

        private ListView scheduleList;
        private ListView shareList;
        private Spinner schedulesSpinner;

        private StitchAppClient client;
        private MyToast t;
        private String username;
        private List<Lesson> lessons = new LinkedList<>();
        private List<User> friends = new LinkedList<>();
        private List<Schedule> friendsSchedules;
        private boolean lessonsFetched;
        private boolean friendsFetched;
        private String CUR_SCHEDULE;

    private String getFriendNameById(String id){
        for (User friend : friends)
            if(friend.getOwner_id().equals(id)) return friend.getUsername();
         return null;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_share, container, false);

        username = MongoAccess.getUsername();
        CUR_SCHEDULE =  getContext().getString(R.string.my_current_schedule);


        uploadScheduleButton = root.findViewById(R.id.upload_my_schedule_button);
        setScheduleButton = root.findViewById(R.id.set_schedule_button);

        scheduleNameEdit = root.findViewById(R.id.schedule_name_edit);
        scheduleNameText = root.findViewById(R.id.schedule_name_text);

        scheduleList = root.findViewById(R.id.schedule_list);
        shareList = root.findViewById(R.id.share_with_list);
        schedulesSpinner = root.findViewById(R.id.schedules_spinner);

        viewModel =  new ViewModelProvider(this).get(DaysViewModel.class);
        viewModel.getLessons().observe(getViewLifecycleOwner(), fetchedLessons -> {
            lessons = fetchedLessons;
            lessonsFetched = true;
            scheduleList.setAdapter(new LessonsAdapter(getContext(),lessons));
        });
        LinkedList<String> labels = new LinkedList<>();
        labels.add(CUR_SCHEDULE);
        t = new MyToast(getContext());

        if( username == null){
            schedulesSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,labels));
            schedulesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    scheduleList.setAdapter(new LessonsAdapter(getContext(),lessons));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            scheduleNameText.setVisibility(View.GONE);
            scheduleNameEdit.setVisibility(View.GONE);
            root.findViewById(R.id.share_friends_layout).setVisibility(View.GONE);
            setSetAction();
            return root;
        }

        MongoAccess.getFriends().onSuccessTask(list-> {
            friends = list;
            friendsFetched = true;
            shareList.setAdapter(new UsersAdapter(getContext(),friends));
            return MongoAccess.getFriendsSchedules();
        }).addOnSuccessListener(schedules -> {
            friendsSchedules = schedules;
            for(Schedule schedule: schedules) {
                if(schedule.getOwner_id().equals(MongoAccess.getId())) labels.add(getString(R.string.my_schedule)+schedule.getName());
                else labels.add(getFriendNameById(schedule.getOwner_id())+": "+schedule.getName());
            }


            schedulesSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, labels));

            schedulesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position ==0 && lessonsFetched) scheduleList.setAdapter(new LessonsAdapter(getContext(),lessons));
                    else scheduleList.setAdapter(new LessonsAdapter(getContext(), schedules.get(position-1).getLessons()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            uploadScheduleButton.setEnabled(true);

        }).addOnFailureListener(e -> t.toast(e));

        setUploadAction();
        setSetAction();
        return root;
    }

    private void setSetAction() {
        setScheduleButton.setOnClickListener(v -> {
            int res = schedulesSpinner.getSelectedItemPosition();
            List<Lesson> newLessons =( (CheckBoxArrayAdapter<Lesson>)scheduleList.getAdapter()).getChecked();
            if(res == 0 && newLessons.size() == lessons.size()) t.toast(getString(R.string.schedule_already_used));
            else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.set_schedule_title))
                        .setMessage(getString(R.string.set_schedule_mesage))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton(getString(R.string.cancel),null)
                        .setPositiveButton(getString(R.string.insert_n_delete), (dialog, which) -> {
                            viewModel.deleteAll();
                            viewModel.insertAll(newLessons);
                            t.toast(getString(R.string.lessons_inserted));
                        })
                        .setNeutralButton(getString(R.string.only_insert), (dialog, which) -> {
                            viewModel.insertAll(newLessons);
                            t.toast(getString(R.string.schedule_changed));
                        })
                        .create()
                        .show();

            }
        });

    }

    private void setUploadAction() {
        uploadScheduleButton.setOnClickListener(v -> {
            if(!lessonsFetched || !friendsFetched) return;
            String scheduleName = scheduleNameEdit.getText().toString();
            if(scheduleName.isEmpty()){
                t.toast(getString(R.string.do_name_schedule));
                return;
            }
            List<Lesson> checked =     ((CheckBoxArrayAdapter)scheduleList.getAdapter()).getChecked();
            List<User> friendsChecked =  ((CheckBoxArrayAdapter)shareList.getAdapter()).getChecked();
            List<String> friendsIds = new LinkedList<>();
            for(User friend: friendsChecked) friendsIds.add(friend.getOwner_id());

            MongoAccess.insertSchedule(new Schedule(MongoAccess.getId(),scheduleName,checked,friendsIds)).addOnCompleteListener(task -> {
                if(task.isSuccessful()) t.toast(getString(R.string.schedule_added));
                else t.toast(task.getException());
            });
        });
    }






}