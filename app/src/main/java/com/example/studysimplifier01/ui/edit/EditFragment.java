package com.example.studysimplifier01.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;

public class EditFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.edit_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DaysViewModel viewModel = new ViewModelProvider(this).get(DaysViewModel.class);
        EditScheduleAdapter adapter = new EditScheduleAdapter(getContext(),viewModel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return root;
    }
}