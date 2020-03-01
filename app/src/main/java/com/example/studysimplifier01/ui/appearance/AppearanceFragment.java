package com.example.studysimplifier01.ui.appearance;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.main.Values;

public class AppearanceFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_appearance, container, false);

        root.findViewById(R.id.py_button).setOnClickListener(this);
        root.findViewById(R.id.bb_button).setOnClickListener(this);
        root.findViewById(R.id.bp_button).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int theme;
        if(id == R.id.py_button) theme = R.style.PurpleYellow;
        else if(id == R.id.bb_button) theme = R.style.BlueBrown;
        else if(id == R.id.bp_button) theme = R.style.BluePink;
        else return;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putInt(Values.THEME_KEY, theme);
        editor.commit();
        getActivity().recreate();
    }
}
