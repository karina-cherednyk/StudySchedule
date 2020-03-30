package com.example.studysimplifier01.ui.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studysimplifier01.dateConvertion.DateConverter;
import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.main.MyToast;
import com.example.studysimplifier01.mongoDBModel.MongoAccess;
import com.example.studysimplifier01.mongoDBModel.collections.MongoLesson;
import com.example.studysimplifier01.R;
import com.example.studysimplifier01.roomDBModel.DaysViewModel;
import com.example.studysimplifier01.roomDBModel.entities.ParticularLesson;
import com.github.dhaval2404.imagepicker.ImagePicker;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HWAdapter extends RecyclerView.Adapter<HWAdapter.HWViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private LinearLayout mainLayout;
    private List<ParticularLesson> pLessons = new LinkedList<>();
    private DaysViewModel viewModel;
    private int dayOfWeek;
    private int orDate;
    private long lessonID;
    private String lessonName;
    private int minDate;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private MyPagerAdapter myPagerAdapter;
    private MyToast t;
    private NotificationManagerCompat notificationManager;
    private static SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
    private String dir;
    private PageFragment fragment;

    public HWAdapter(Context context, PageFragment fragment, DaysViewModel viewModel, MyPagerAdapter myPagerAdapter, long lessonID, String lessonName, int minDate, int orDate){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.lessonID = lessonID;
        this.lessonName = lessonName;
        this.minDate = minDate;
        this.orDate = orDate;
        this.dayOfWeek = DateConverter.getWeekDay(orDate);
        this.mainLayout = ((Activity)context).findViewById(R.id.hw_layout);
        this.myPagerAdapter = myPagerAdapter;
        this.t = new MyToast(context);
        this.notificationManager = NotificationManagerCompat.from(context);
        this.dir = context.getExternalCacheDir().getAbsolutePath();
        this.fragment = fragment;
        createNotificationChannel();


      viewModel.getPLessons(lessonID,minDate).observe((LifecycleOwner) context, particularLessons -> {
          pLessons = particularLessons;
          notifyDataSetChanged();
      });
    }
    ParticularLesson getByDate(int date){
        for(ParticularLesson p : pLessons)
            if(p.date == date) return p;
        return null;
    }

    @NonNull
    @Override
    public HWViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new HWViewHolder(inflater.inflate(R.layout.hw_onelesson,parent, false ));
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull HWViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return pLessons.size()+1;
    }

    public void addImage(Uri fileUri) {
        imageRequestHolder.addImageUri(fileUri);
    }

    private HWViewHolder imageRequestHolder;
    public void setAcceptAudio(boolean b) {
        audioPermission = b;
    }
    private boolean audioPermission;


    public class HWViewHolder extends RecyclerView.ViewHolder{
        int position;
        EditText newTaskText;
        Button dateView;
        ParticularLesson pLesson;
        ImageButton addButton;
        ImageButton deleteButton;
        ImageButton shareButton;
        CheckBox checkBox;
        int date = orDate;
        boolean completed;
        LinearLayout layout;
        LinearLayout taskLayout;
        ImageButton pickImgButton;
        ImageButton recordButton;
        private int initialColor;
        private String textTask="";

        LinkedList<Uri> uris = new LinkedList<>();


        public HWViewHolder(@NonNull View itemView) {
            super(itemView);
            newTaskText = itemView.findViewById(R.id.hw_one_task);
            dateView = itemView.findViewById(R.id.hw_date);
            addButton = itemView.findViewById(R.id.hw_add_button);
            deleteButton = itemView.findViewById(R.id.hw_delete_button);
            checkBox = itemView.findViewById(R.id.hw_completed_checkBox);
            layout = itemView.findViewById(R.id.hw_one_layout);
            taskLayout = itemView.findViewById(R.id.hw_one_task_layout);
            pickImgButton = itemView.findViewById(R.id.pick_img_button);
            initialColor = layout.getSolidColor();
            recordButton = itemView.findViewById(R.id.record_button);
            recordButton.setOnClickListener(new RecordListener(recordButton));

            shareButton = itemView.findViewById(R.id.share_button);
            if(MongoAccess.getUsername() == null) shareButton.setVisibility(View.GONE);

            pickImgButton.setOnClickListener(v ->{
                imageRequestHolder = HWViewHolder.this;
                ImagePicker.Companion.with(fragment)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
                    });


        }



        private void setChecked(boolean checked){
            if(checked){
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_success));
                deleteButton.setColorFilter(ContextCompat.getColor(context,R.color.color_error));
                completed = true;
            }
            else {

                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_error));
                deleteButton.setColorFilter(ContextCompat.getColor(context,R.color.color_on_error));
                completed = false;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void getPreviousTasks(String taskForToday) {
            Pattern imagePattern = Pattern.compile(Values.IMG_TAG + "([^&]+)" + Values.IMG_TAG + "&");
            Pattern audioPattern = Pattern.compile(Values.AUDIO_TAG + "([^&]+)" + Values.AUDIO_TAG + "&");
            //split for tasks added in different bindingNewLesson view
            String[] tasks = taskForToday.split("\n\n");

            for(String task : tasks){
                //get all images uri
                List<String> imageUris = new ArrayList<String>();
                List<String> audioUris = new ArrayList<>();
                Matcher m = imagePattern.matcher(task);
                while (m.find()) imageUris.add(m.group(1));
                m = audioPattern.matcher(task);
                while (m.find()) audioUris.add(m.group(1));

                //get text task
                String textTask = task.replaceAll(imagePattern.pattern(),"").replaceAll(audioPattern.pattern(),"").trim();


                if(!textTask.isEmpty()) {
                    TextView et = new TextView(context);
                    et.setText(textTask);
            
                    et.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP,new int[]{0,8,0,8}));
                    et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    taskLayout.addView(et);
                    this.textTask = textTask;
                }
                for(String uri: imageUris){

                    Uri myUri = Uri.fromFile(new File(uri));
                    ImageView im = new ImageView(context);
                    Glide.with(context).load(myUri).into(im);
                    im.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP));
                    im.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    taskLayout.addView(im);
                }
                for(final String fileName: audioUris){
                    int buttonStyle = R.style.MyButtonStyle;
                    final Button b = new Button(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);
                    b.setText(context.getString(R.string.play_record));
                    b.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP));
                    b.setOnClickListener(new ListenListener(fileName,b));
                    taskLayout.addView(b);
                }



                if(taskLayout.getHeight()>mainLayout.getHeight()) taskLayout.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_MATCH));
            }
        }


       @RequiresApi(api = Build.VERSION_CODES.M)
       private void bindPLesson(){

           //so position is < pLessons.size() -> view is not last
           pLesson = pLessons.get(position);


           //are particular lesson tasks completed?
           setChecked(pLesson.completed);
           checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> setChecked(isChecked));
           checkBox.setChecked(pLesson.completed);


           //add new views to taskLayout, view = task(tex || image)
           getPreviousTasks(pLesson.taskForToday);

           dateView.setText(DateConverter.toDateStr(pLesson.date));
           dateView.setEnabled(false);
           
           addButton.setOnClickListener(v -> {
               String text = newTaskText.getText().toString() + saveAudio() +saveUris();
               if(text.isEmpty() && pLesson.completed == completed)return;
               else if(text.isEmpty()){
                   viewModel.update(pLesson.particularLessonID,pLesson.taskForToday,completed);
                  t.toast(context.getString(R.string.lesson_status_updated));
               }
               else {
                   audios = new LinkedList<>();
                   uris = new LinkedList<>();
                   viewModel.update(pLesson.particularLessonID,pLesson.taskForToday+"\n\n"+text,completed);
                  t.toast(context.getString(R.string.lesson_task_updated));
               }
           });
           deleteButton.setOnClickListener(v -> new AlertDialog.Builder(context)
                   .setTitle(context.getString(R.string.confirmation))
                   .setMessage(context.getString(R.string.delete_confirmation_message))
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                       viewModel.deleteParticularLesson(pLesson.particularLessonID);
                       notifyItemRemoved(position);
                       Toast.makeText(context,"Task deleted",Toast.LENGTH_SHORT).show();
                   })
                   .setNegativeButton(android.R.string.no, null).show());
            shareButton.setVisibility(View.GONE);
       }

       private void bindingNewLesson(){

           //no need for checkbox view
           checkBox.setVisibility(View.GONE);

           dateView.setText(DateConverter.toDateStr(orDate));
           
           dateView.setOnClickListener(v -> {
               int day = DateConverter.getDay(minDate);
               int month = DateConverter.getMonth(minDate);
               int year = DateConverter.getYear(minDate);
               new DatePickerDialog(context,
                       (view, DPyear, DPmonth, DPday) -> {
                           DPmonth++;
                           if(DateConverter.getWeekDay(DPday,DPmonth,DPyear) != dayOfWeek) {
                               t.toast(context.getString(R.string.incorrect_day_of_week));
                           }
                           else {
                               dateView.setText(DateConverter.toDateStr(DPday,DPmonth,DPyear));
                               date = DateConverter.toDate(DPday,DPmonth,DPyear);
                           }

                       }, year, month-1, day).show(); ;
           });
           
    
           addButton.setOnClickListener(v -> {

               String text = newTaskText.getText().toString();
               text+= saveUris() + saveAudio();

               if(text.isEmpty()) {
                 t.toast(context.getString(R.string.add_task_before_commit));
                   return;
               }

               ParticularLesson pl = getByDate(date);
               //add new lesson, this holder will be place for new lesson
               if(pl == null){
                   clearHolder();
                   viewModel.insert(new ParticularLesson(lessonID, date, text));
                   t.toast(context.getString(R.string.task_inserted));
               }
               //update existing lesson
               else {
                   viewModel.update(pl.particularLessonID,pl.taskForToday+"\n\n"+text, completed);
                   clearHolder();
                   t.toast(context.getString(R.string.task_updated));
               }
               uris.clear();
               audios.clear();
           });
           shareButton.setVisibility(View.VISIBLE);
           shareButton.setOnClickListener(v -> {

               String text = newTaskText.getText().toString();

               ((CommonHWAdapter)myPagerAdapter.getAdapter(1)).addTask(new MongoLesson(lessonName,MongoAccess.getId(),text,date));

               text+= saveUris() + saveAudio();

               if(text.isEmpty()) {
                   t.toast(context.getString(R.string.add_task_before_commit));
                   return;
               }

               ParticularLesson pl = getByDate(date);
               //add new lesson, this holder will be place for new lesson
               if(pl == null){
                   clearHolder();
                   viewModel.insert(new ParticularLesson(lessonID, date, text));
                   t.toast(context.getString(R.string.task_inserted));
               }
               //update existing lesson
               else {
                   viewModel.update(pl.particularLessonID,pl.taskForToday+"\n\n"+text, completed);
                   clearHolder();
                   t.toast(context.getString(R.string.task_updated));
               }
               uris.clear();
               audios.clear();


           });

           deleteButton.setOnClickListener(v -> {clearHolder(); uris.clear();audios.clear();});
           for(Uri uri: uris){
               ImageView im = new ImageView(context);
               Glide.with(context).load(uri).into(im);
               im.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP));
               im.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
               taskLayout.addView(im);
           }
           if(taskLayout.getHeight()>mainLayout.getHeight()) taskLayout.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_MATCH));
       }

        private void clearHolder(){
            newTaskText.setText("");
            taskLayout.removeAllViews();
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bindView(final int pos){
            position = pos;
            clearHolder();
            checkBox.setVisibility(View.VISIBLE);
            layout.setBackgroundColor(initialColor);
            if(pos<pLessons.size())  bindPLesson();
            else bindingNewLesson();


        }


    private String saveUris(){
        if(uris.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for(Uri uri : uris)
            sb.append(Values.IMG_TAG+ uri.getPath() + Values.IMG_TAG+ "&");


        return sb.toString();
    }
        private String saveAudio(){
            if(audios.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for(String audio : audios)
                sb.append(Values.AUDIO_TAG + audio + Values.AUDIO_TAG+ "&");
            return sb.toString();
        }
    private LinkedList<String> audios = new LinkedList<>();

        public void addImageUri(Uri fileUri) {

            uris.add(fileUri);

            Uri myUri = Uri.fromFile(new File(fileUri.getPath()));
            ImageView im = new ImageView(context);
            Glide.with(context).load(myUri).into(im);
            im.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP));
            im.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            taskLayout.addView(im);
        }


        private class RecordListener implements View.OnClickListener {


            private String fileName;
            private ImageButton button;


            RecordListener(ImageButton button){
                this.button = button;
            }

            @Override
            public void onClick(View v) {
                if(!audioPermission) {
                    t.toast(context.getString(R.string.no_permission_for_audio));
                    button.setEnabled(false);
                }
                if(recording != button){
                    //previous record wasnt stopped
                    if(recording != null) {
                        recorder.stop();
                        recorder.release();
                        recording.setImageResource(R.drawable.record);
                        recording = null;
                        notificationManager.cancel(Values.RECORDING_ID);
                    }
                    recorder = new MediaRecorder();
                    String datetime = ft.format(new Date());
                    fileName = dir+"/"+datetime+".3gp";
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(fileName);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        t.toast(e);
                        return;
                    }
                    recording = recordButton;
                    recordButton.setImageResource(R.drawable.stop);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Values.RECORDING_CHANNEL)
                                .setContentTitle(context.getString(R.string.recording))
                                .setContentText(context.getString(R.string.recording))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setOngoing(true)
                                .setSmallIcon(R.mipmap.my_launcher);

                        try {
                            notificationManager.notify(Values.RECORDING_ID, builder.build());
                        } catch (Exception e){
                            t.toast(e);
                        }
                    }
                }
                else {

                    recorder.stop();
                    recorder.release();
                    recording.setImageResource(R.drawable.record);
                    recording = null;

                    audios.add(fileName);

                    int buttonStyle = R.style.MyButtonStyle;
                    final Button b = new Button(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);
                    b.setText(context.getString(R.string.play_record));
                    b.setLayoutParams(getParamsInstance(Values.LAYOUT_PARAMS_MODE_WRAP));
                    b.setOnClickListener(new ListenListener(fileName,b));
                    taskLayout.addView(b);

                    notificationManager.cancel(Values.RECORDING_ID);
                }
            }

        }
    }
    private class ListenListener implements View.OnClickListener{
        private String fileName;
        private Button button;
        ListenListener(String fileName, Button button){
            this.fileName = fileName;
            this.button = button;
        }
        @Override
        public void onClick(View v) {
            if(playing != button){
                if(playing!=null){
                    player.stop();
                    player.release();
                    playing.setText(context.getString(R.string.play_record));
                    playing = null;
                    notificationManager.cancel(Values.PLAYING_ID);
                }
                player = new MediaPlayer();
                try {

                    player.setDataSource(fileName);
                    player.prepare();
                    player.start();
                    player.setOnCompletionListener(mp -> {
                        player.stop();
                        player.release();
                        playing.setText(context.getString(R.string.play_record));
                        playing = null;

                        notificationManager.cancel(Values.PLAYING_ID);
                    });

                } catch (IOException e) {t.toast(e); return;}
                playing = button;
                button.setText(context.getString(R.string.stop_record));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Values.PLAYING_CHANNEL)
                            .setContentTitle(context.getString(R.string.playing))
                            .setContentText(context.getString(R.string.playing))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(true)
                            .setSmallIcon(R.mipmap.my_launcher);

                    try {
                        notificationManager.notify(Values.PLAYING_ID, builder.build());
                    } catch (Exception e){
                        t.toast(e);
                    }
                }
            }
            else{
                player.stop();
                player.release();
                playing.setText(context.getString(R.string.play_record));
                playing = null;

                notificationManager.cancel(Values.PLAYING_ID);
            }
        }
    }
    private static  LinearLayout.LayoutParams getParamsInstance(String mode, int[] margins){
        LinearLayout.LayoutParams res;
        if(mode.equals(Values.LAYOUT_PARAMS_MODE_WRAP)) res =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        else res = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        res.setMargins(margins[0],margins[1],margins[2],margins[3]);
        return res;
    }
    private static  LinearLayout.LayoutParams getParamsInstance(String mode){
        LinearLayout.LayoutParams res;
        if(mode.equals(Values.LAYOUT_PARAMS_MODE_WRAP)) res =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        else res = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return  res;
    }

    private ImageButton recording;
    private Button playing;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(Values.RECORDING_CHANNEL, Values.RECORDING_CHANNEL, importance);
            channel.setDescription("For audio recording");
            notificationManager.createNotificationChannel(channel);

            NotificationChannel  channel2 = new NotificationChannel(Values.PLAYING_CHANNEL, Values.PLAYING_CHANNEL, importance);
            channel2.setDescription("For audio playing");
            notificationManager.createNotificationChannel(channel2);

        }
    }

}
