package com.example.studysimplifier01.ui.show;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;

public class PageFragment extends Fragment {

    private int page;

    private int  minDate, orDate;
    private String lessonName;
    private long lessonId;
    private static String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private MyPagerAdapter myPagerAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public PageFragment(){

    }
    public PageFragment(MyPagerAdapter myPagerAdapter, int page,String lessonName, long lessonId, int minDate, int orDate){
        this.myPagerAdapter = myPagerAdapter;
        this.page = page;
        this.lessonName = lessonName;
        this.lessonId = lessonId;
        this.minDate = minDate;
        this.orDate = orDate;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.particular_lessons_tab, container, false);
        recyclerView = view.findViewById(R.id.hw_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        if(page == 0) {
            DaysViewModel viewModel = new ViewModelProvider(this).get(DaysViewModel.class);
            adapter = new HWAdapter(getContext(), this, viewModel, myPagerAdapter, lessonId, lessonName, minDate, orDate);
            requestPermissions(permissions,REQUEST_RECORD_AUDIO_PERMISSION);
        }
        else
            adapter = new CommonHWAdapter(getContext(), lessonName, minDate, orDate);

        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(page!=0) return;
        System.out.println("HERE WITH IMAGE");
        if (resultCode == Activity.RESULT_OK)
            if (data != null) {
                Uri fileUri = data.getData();
                ((HWAdapter)adapter).addImage(fileUri);
            }


    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(page!=0) return;
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                ((HWAdapter)adapter).setAcceptAudio(grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }

    }
}