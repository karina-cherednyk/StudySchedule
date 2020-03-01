package com.example.studysimplifier01.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.example.studysimplifier01.R;


import java.util.Locale;

public class SetPreferences
{
    public static void set(Context ctx){
        updateLanguage(ctx);
        updateTheme(ctx);
    }

    public static void updateLanguage(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString(Values.LANG, "");
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
    }
    public static void updateTheme(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        int theme = prefs.getInt(Values.THEME_KEY,-1);
        if(theme != -1) ctx.setTheme(theme);
        else ctx.setTheme(R.style.BluePink);
    }
}