package com.example.studysimplifier01.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.mongoDBModel.collections.User;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



import org.bson.Document;



public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String username;

    public String getUsername(){
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetPreferences.set(this);
        
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_show, R.id.nav_edit, R.id.nav_share,
                R.id.nav_appearance, R.id.nav_help, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (savedInstanceState != null) {
            username = savedInstanceState.getString(User.Fields.USERNAME);
         }
        else if(isNetworkAvailable()) doLogin();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void doLogin(){
        if(MongoAccess.auth.isLoggedIn()) {
            String userId = MongoAccess.getId();
            Document whereQuery = new Document(User.Fields.OWNER_ID, userId);
            MongoAccess.getUser(whereQuery).addOnCompleteListener(task ->
                    {
                        if(task.isSuccessful() && task.getResult()!=null) {
                            username = task.getResult().getUsername();
                            t.toast(getString(R.string.welcome_msg)+username);
                        }
                        else username = null;
                    }
                    );
        }
        else {
            Intent intent = new Intent(MainActivity.this, LogonActivity.class);
            startActivityForResult(intent, 111);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        doLogin();
    }


    private MyToast t = new MyToast(this);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(username!=null) outState.putString(User.Fields.USERNAME, username);
        super.onSaveInstanceState(outState);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
