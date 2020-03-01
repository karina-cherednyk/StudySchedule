package com.example.studysimplifier01.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import com.example.studysimplifier01.main.MainActivity;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.R;

import com.example.studysimplifier01.mongoDBModel.collections.User;


import org.bson.Document;
import org.bson.types.ObjectId;

public class ProfileFragment extends Fragment {



    private Button logoutButton;
    private Button updateButton;
    private Button findButton;
    private EditText usernameEdit;
    private EditText friendEdit;
    private MyToast t;
    private String username;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        username = ((MainActivity)getActivity()).getUsername();


        logoutButton = root.findViewById(R.id.log_out_button);
        updateButton = root.findViewById(R.id.update_user);
        usernameEdit = root.findViewById(R.id.new_username_edit);
        findButton = root.findViewById(R.id.find_friend_button);
        friendEdit = root.findViewById(R.id.friend_username_edit);

        setFindAction();
        setLogoutAction();
        setUpdateAction();


        t = new MyToast(getContext());

        return root;
    }

    private void setFindAction() {
        findButton.setOnClickListener(v -> {
            String username = friendEdit.getText().toString();
            if(username.isEmpty()) {
                t.toast(getString(R.string.give_username));
                return;
            }


            MongoAccess.getFriend(username).addOnCompleteListener( task -> {
                if(task.isCanceled()) t.toast(task.getException());
                else if(task.getResult() != null)t.toast(getString(R.string.already_friends));
                else {
                    Document ufind = new Document(User.Fields.USERNAME, username);
                    Document uupdate = new Document("$push", new Document(User.Fields.FRIENDS,MongoAccess.getId()));
                    Document mfind = new Document(User.Fields.OWNER_ID,MongoAccess.getId());


                    MongoAccess.findUpdateUser(ufind,uupdate).onSuccessTask(user ->
                            MongoAccess.findUpdateUser(mfind,new Document("$push",new Document(User.Fields.FRIENDS,user.getOwner_id())))
                    )
                            .addOnSuccessListener(user -> t.toast(getString(R.string.succesfully_befriended))
                            )
                            .addOnFailureListener(e -> t.toast(e));
                }
                    }
            );
        });
    }


    private void setUpdateAction() {
        updateButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString();
            if(username.isEmpty()) {
                t.toast(getString(R.string.give_username));
                return;
            }
            String email = MongoAccess.getStitchUser().getProfile().getEmail();
            String id = MongoAccess.getId();

            MongoAccess.getUsername(id).addOnSuccessListener(user->{
                if(user != null)
                    MongoAccess.updateOneUser(id,new Document("$set", new Document(User.Fields.USERNAME,username))).addOnCompleteListener(task -> {
                        if(task.isSuccessful() ) t.toast(getString(R.string.username_updated)) ;
                        else                     t.toast(task.getException()) ;
                    });
                else
                    MongoAccess.insertOneUser(new User(new ObjectId(), id, username, email)).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) t.toast(getString(R.string.username_added));
                        else                     t.toast(task.getException());
                    });
            }).addOnFailureListener(e -> t.toast(e));
        });
    }


    private void setLogoutAction() {
        logoutButton.setOnClickListener(v ->
                MongoAccess.auth.logout().addOnSuccessListener(r -> {
            ((MainActivity) getActivity()).doLogin();
        }));
    }

}