package com.example.studysimplifier01.ui.show;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.dateConvertion.DateConverter;
import com.example.studysimplifier01.dateConvertion.DateManipulator;
import com.example.studysimplifier01.R;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.google.android.material.button.MaterialButton;


import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class ShowFragment extends Fragment {


    private  RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CardAdapter adapter;
    private DaysViewModel viewModel;
    private MaterialButton dateButton;

    DateManipulator dm = new DateManipulator();
    int curDayOfWeek = dm.getDayOfWeek();
    Date curDate = new Date();
    Date firstDateOfWeek = dm.subtractDays(curDate, curDayOfWeek -1);
    Date lastDateOfWeek = dm.addDays(firstDateOfWeek,6);
    DatePickerDialog picker;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_show, container, false);


        dateButton = root.findViewById(R.id.date_text);
        dateButton.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            final int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        cldr.set(year1,monthOfYear,dayOfMonth);
                        curDate = cldr.getTime();
                        curDayOfWeek = cldr.get(Calendar.DAY_OF_WEEK)-1;
                        firstDateOfWeek = dm.subtractDays(curDate, curDayOfWeek -1);
                        lastDateOfWeek = dm.addDays(firstDateOfWeek,6);

                        dateButton.setText(DateConverter.toDateStr(curDate));
                    }, year, month, day);
            picker.show();
        });
        viewModel =  new ViewModelProvider(this).get(DaysViewModel.class);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


       dateButton.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) { }

           @Override
           public void afterTextChanged(Editable s) {
               adapter = new CardAdapter(getContext(),viewModel,curDayOfWeek,firstDateOfWeek,lastDateOfWeek);
               recyclerView.setAdapter(adapter);
           }
       });
        dateButton.setText(DateConverter.toDateStr(new Date()));


        return root;
    }


}