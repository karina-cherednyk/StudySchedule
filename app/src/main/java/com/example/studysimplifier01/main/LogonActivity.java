package com.example.studysimplifier01.main;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.mongoDBModel.collections.User;
import com.example.studysimplifier01.ui.show.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Locale;


public class LogonActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signinButton;
    private Button anonButton;
    private Button confirmButton;
    private Button resetPassButton;
//    private TextView passwordText;
//    private TextView emailText;
//    private TextView usernameText;
    private EditText passwordEdit;
    private EditText emailEdit;
    private EditText usernameEdit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetPreferences.set(this);
        setContentView(R.layout.logon);



        loginButton =  findViewById(R.id.log_in_button);
        signinButton = findViewById(R.id.sign_in_button);
        anonButton = findViewById(R.id.log_in_anon_button);
        confirmButton = findViewById(R.id.confirm_logon_button);
        resetPassButton = findViewById(R.id.reset_pass_button);
//
//        passwordText = findViewById(R.id.password_text);
//        emailText = findViewById(R.id.email_text);
//        usernameText = findViewById(R.id.username_text);

        passwordEdit = findViewById(R.id.password_edit);
        emailEdit = findViewById(R.id.email_edit);
        usernameEdit = findViewById(R.id.username_edit);

        confirmButton.setVisibility(View.GONE);
        disableUserName();
        disableEmail();
        disablePassword();

        enableAnonAuth();
        loginButton.setOnClickListener(x -> enableAuth());
        signinButton.setOnClickListener(x -> enableSignIn());
        resetPassButton.setOnClickListener(x->enableResetPass());

        RadioButton uaButton = findViewById(R.id.ua_button);
        RadioButton enButton = findViewById(R.id.en_button);

        if(PreferenceManager.getDefaultSharedPreferences(this).getString(Values.LANG,"").equals("uk")) uaButton.setChecked(true);

        uaButton.setOnClickListener(x-> changeLocal("uk"));
        enButton.findViewById(R.id.en_button).setOnClickListener(x-> changeLocal("en"));
    }

    private void enableResetPass() {
        enableEmail();
        disableUserName();
        disableUserName();
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setOnClickListener(resetPassListener);
    }

    private void enableAuth() {
        enableEmail();
        enableUserName();
        enablePassword();
        confirmButton.setVisibility(View.VISIBLE);

        confirmButton.setOnClickListener(logInListener);
    }
    private void enableSignIn(){
        enableEmail();
        disableUserName();
        enablePassword();
        confirmButton.setVisibility(View.VISIBLE);

        confirmButton.setOnClickListener(signInListener);
    }


    private void enableAnonAuth() {
        anonButton.setOnClickListener(v ->
                MongoAccess.client.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
        }));
    }
    
    
    View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String password = passwordEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();


            if( password == null || password.equals("") ||  email == null || email.equals("")){
                t.toast(getString(R.string.fill_forms));
                return;
            }
            
            MongoAccess.getEmailPasswordClient().registerWithEmail(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) t.toast(getString(R.string.succesfully_sent_email));
                else                     t.toast(getString(R.string.error_registering),task.getException());

            });



        }
    };


    final View.OnClickListener resetPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailEdit.getText().toString();
            if ( email == null || email.equals("") ) {
                t.toast(getString(R.string.fill_forms));
                return;
            }
            
            MongoAccess.getEmailPasswordClient().sendResetPasswordEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) t.toast(getString(R.string.succesfully_sent_reset_email));
                else                     t.toast(getString(R.string.error_sending_reset_email), task.getException());
                
            });

        }
    };

    final View.OnClickListener logInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = usernameEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();

            if ((username.isEmpty() && email.isEmpty()) || password.isEmpty()) {
                t.toast(getString(R.string.fill_e_or_u_and_pass));
                return;
            }
            if (!username.isEmpty())
                MongoAccess.accessAnon().onSuccessTask(
                        stitchUser -> MongoAccess.getUser(new Document(User.Fields.USERNAME, username))
                )
                        .onSuccessTask(
                                user -> MongoAccess.logIn(user.getEmail(), password)
                        ).addOnSuccessListener(stitchUser -> {
                    setResult(Activity.RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
                    t.toast(getString(R.string.exception_w_authorization), e);
                    MongoAccess.logout();
                });


            else
                MongoAccess.logIn(email, password).onSuccessTask(
                        task -> MongoAccess.getUsername(MongoAccess.getId())).addOnSuccessListener(
                        user -> {
                            if (user == null)
                                MongoAccess.insertOneUser(new User(new ObjectId(), MongoAccess.getId(), email, email));
                        }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setResult(Activity.RESULT_OK);
                        finish();
                        return;

                    } else t.toast(getString(R.string.unable_login), task.getException());
                });
        }

    };



    private void disableUserName(){
//        usernameText.setVisibility(View.GONE);
        usernameEdit.setVisibility(View.GONE);
        usernameEdit.setText("");
    }
    private void enableUserName(){
//        usernameText.setVisibility(View.VISIBLE);
        usernameEdit.setVisibility(View.VISIBLE);
    }
    private void disableEmail(){
//        emailText.setVisibility(View.GONE);
        emailEdit.setVisibility(View.GONE);
        emailEdit.setText("");
    }
    private void enableEmail(){
//        emailText.setVisibility(View.VISIBLE);
        emailEdit.setVisibility(View.VISIBLE);
    }
    private void disablePassword(){
//        passwordText.setVisibility(View.GONE);
        passwordEdit.setVisibility(View.GONE);
        passwordEdit.setText("");
    }
    private void enablePassword(){
//        passwordText.setVisibility(View.VISIBLE);
        passwordEdit.setVisibility(View.VISIBLE);
    }
    private static String TAG = LogonActivity.class.getCanonicalName();
    private MyToast t = new MyToast(this);


    private void changeLocal(String lang){
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Locale.setDefault(myLocale);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(Values.LANG,lang);
        editor.commit();
        recreate();
    }
}