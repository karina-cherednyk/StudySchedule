package com.example.studysimplifier01.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.studysimplifier01.R;

public class MyToast {
    private Context context;
    private String TAG;
    public MyToast(Context context){
        this.context = context;
        this.TAG = context.getClass().getCanonicalName();
    }
    public void toast(String text){
        Toast.makeText(context, text,
                Toast.LENGTH_LONG).show();
        Log.e(TAG,text);
    }
    public void toast(String text, Exception e){
        Toast.makeText(context, text+", "+e.getMessage(),
                Toast.LENGTH_LONG).show();
        Log.e(TAG, text, e);
    }
    public void toast(Exception e){
        Toast.makeText(context, context.getString(R.string.exc)+e.getMessage(),
                Toast.LENGTH_LONG).show();
        Log.e(TAG, context.getString(R.string.exc), e);
    }
}
