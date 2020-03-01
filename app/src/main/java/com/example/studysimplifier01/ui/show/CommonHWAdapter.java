package com.example.studysimplifier01.ui.show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysimplifier01.dateConvertion.DateConverter;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.mongoDBModel.collections.MongoLesson;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.R;


import java.util.LinkedList;
import java.util.List;

class CommonHWAdapter extends RecyclerView.Adapter<CommonHWAdapter.HWViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private int orDate;
    private String lessonName;
    private int minDate;
    private List<MongoLesson> tasks = new LinkedList<>();
    MyToast t;

    public CommonHWAdapter(Context context, String lessonName, int minDate, int orDate){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.lessonName = lessonName;
        this.minDate = minDate;
        this.orDate = orDate;
        t = new MyToast(context);


        MongoAccess.fetchFriendsTasks(lessonName).addOnSuccessListener(fetchedTasks-> {
                    tasks = fetchedTasks;
                    int k= tasks.size();
                    final int[] c = {0};
                    for( MongoLesson task : tasks)
                        MongoAccess.getUsername(task.getOwner_id()).addOnSuccessListener(user -> {
                            task.setOwner_id(user.getUsername());
                            ++c[0];
                            if(c[0] == k) {
                                notifyDataSetChanged();
                            }
                        });
                }).addOnFailureListener(e-> t.toast(e));

    }

    @NonNull
    @Override
    public HWViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HWViewHolder(inflater.inflate(R.layout.hw_onelesson_shared,parent, false ));
    }
    public void addTask(MongoLesson ml){
        MongoAccess.insertTask(ml).addOnSuccessListener(insertedTask-> {
                MongoAccess.getUsername(ml.getOwner_id()).addOnSuccessListener(user -> {
                    ml.setOwner_id(user.getUsername());
                    tasks.add(ml);
                    notifyItemInserted(tasks.size()-1);
                });
        }).addOnFailureListener(e-> t.toast(e));

    }
    @Override
    public void onBindViewHolder(@NonNull HWViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class HWViewHolder extends RecyclerView.ViewHolder {
        private int position;
        private TextView userNameText;
        private TextView dateText;
        private TextView dateOfTaskText;
        private TextView taskText;
        public HWViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.hw_share_user_name);
            dateText = itemView.findViewById(R.id.hw_share_date);
            dateOfTaskText = itemView.findViewById(R.id.hw_share_date_of_task);
            taskText = itemView.findViewById(R.id.hw_share_task);
        }
        public void bindView(int position){
            this.position = position;
            MongoLesson l = tasks.get(position);
            userNameText.setText(l.getOwner_id());
            dateText.setText(context.getString(R.string.task_created_when)+DateConverter.toDateStrFull(l.getDate()));
            dateOfTaskText.setText(context.getString(R.string.task_deadline_when)+DateConverter.toDateStr(l.getDate_of_task()));
            taskText.setText(l.getTask());
        }
    }
}
