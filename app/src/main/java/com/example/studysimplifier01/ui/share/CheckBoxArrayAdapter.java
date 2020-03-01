package com.example.studysimplifier01.ui.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.mongoDBModel.collections.User;
import com.example.studysimplifier01.roomDBModel.entities.Lesson;

import java.util.ArrayList;
import java.util.List;


public abstract class CheckBoxArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater inflater;
    private  List<T> checked = new ArrayList<>();

    public CheckBoxArrayAdapter(Context context, List<T> elemsList ) {
        super( context, R.layout.lesson_row, R.id.lesson_row_text, elemsList );
        checked.addAll(elemsList);
        inflater = LayoutInflater.from(context) ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        T elem = (T) this.getItem( position );
        CheckBox checkBox ;
        TextView textView ;

        // Create a new row view
        if ( convertView == null ) {
            convertView = inflater.inflate(R.layout.lesson_row, null);

            // Find the child views.
            textView = convertView.findViewById( R.id.lesson_row_text);
            checkBox = convertView.findViewById( R.id.lesson_check_box );


            convertView.setTag( new CheckBoxViewHolder(textView,checkBox) );

            // If CheckBox is toggled, update the elem it is tagged with.
            checkBox.setOnClickListener(v -> {
                CheckBox cb = (CheckBox) v ;
                T elem1 = (T) cb.getTag();
                if(cb.isChecked()) checked.add(elem1);
                else checked.remove(elem1);
            });
        }
        else {
            CheckBoxViewHolder viewHolder = (CheckBoxViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox() ;
            textView = viewHolder.getTextView() ;
        }
        checkBox.setTag( elem );

        setText(textView,elem);

        return convertView;
    }
    protected abstract void setText(TextView view, T elem);

    private class CheckBoxViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;

        public CheckBoxViewHolder(TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public TextView getTextView() {
            return textView;
        }

    }
    public List<T> getChecked(){
        return checked;
    }

}

class LessonsAdapter extends CheckBoxArrayAdapter<Lesson>{

    public LessonsAdapter(Context context, List<Lesson> elemsList) {
        super(context, elemsList);
    }

    @Override
    protected void setText(TextView view, Lesson elem) {
        view.setText(elem.getLesson());
    }
}
class UsersAdapter extends CheckBoxArrayAdapter<User>{


    public UsersAdapter(Context context, List<User> elemsList) {
        super(context, elemsList);
    }

    @Override
    protected void setText(TextView view, User elem) {
        view.setText(elem.getUsername());
    }
}