package com.example.studysimplifier01.ui.show;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.main.MainActivity;
import com.example.studysimplifier01.main.SetPreferences;
import com.example.studysimplifier01.main.Values;
import com.google.android.material.tabs.TabLayout;

public class HWActivity extends AppCompatActivity {
    private long lessonId;
    private String lessonName;
    private int minDate;
    private int orDate;


    private Button close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetPreferences.set(this);

        setContentView(R.layout.activity_hw);
        Intent intent = getIntent();
        lessonId = intent.getLongExtra(Values.CARD_LESSON_ID,-1);
        lessonName = intent.getStringExtra(Values.CARD_LESSON_NAME);
        minDate = intent.getIntExtra(Values.CARD_LESSON_MIN_DATE,-1);
        orDate = intent.getIntExtra(Values.CARD_LESSON_DATE,-1);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new MyPagerAdapter(getSupportFragmentManager(), this, lessonName,lessonId,minDate,orDate));

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        close = findViewById(R.id.commitButton);
        close.setOnClickListener(v -> finish());
        
    }

}
