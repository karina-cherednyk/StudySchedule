package com.example.studysimplifier01.ui.show;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.R;


public class MyPagerAdapter extends FragmentPagerAdapter {
    private static String tabTitles[];



    private Context context;
    private int  minDate, orDate;
    private String lessonName;
    private long lessonId;
    private boolean online;

    public MyPagerAdapter(FragmentManager fm, Context context,String lessonName, long lessonId, int minDate, int orDate, boolean online) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        tabTitles  = new String[] {context.getString(R.string.my_hw), context.getString(R.string.shared_hw) };
        this.lessonName = lessonName;
        this.lessonId = lessonId;
        this.minDate = minDate;
        this.orDate = orDate;
        this.online = online;
    }

    @Override public int getCount() {
        if(online) return 2;
        return 1;
    }

    PageFragment[] fragments = new PageFragment[2];

    @Override public Fragment getItem(int position) {
        fragments[position] =  new PageFragment(this,position,lessonName,lessonId,minDate,orDate);
        return fragments[position];
    }
    public RecyclerView.Adapter getAdapter(int position){
        if(fragments[position] == null) return null;
        return fragments[position].getAdapter();
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}