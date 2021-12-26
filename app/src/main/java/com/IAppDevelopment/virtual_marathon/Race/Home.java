package com.IAppDevelopment.virtual_marathon.Race;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Dialog_system_message;
import com.IAppDevelopment.virtual_marathon.Login.Login;
import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Rating.Rating;
import com.IAppDevelopment.virtual_marathon.Service.Service_notification;
import com.IAppDevelopment.virtual_marathon.chat.Messages;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This class is designed to process the time data downloaded from the server,
 * And move them to the display,
 * In addition, we will get from the user the desired distance for the target end he has chosen,
 * And we will pass this figure by saving it locally.
 */

public class Home extends AppCompatActivity implements Dialog_system_message.DialogListener{


    BottomNavigationView  bottomNavigationView ;
    TextView Km_data;
    ImageButton ButtonPls, ButtonMnos;
    int cont=0;
    float distance=2;
    TextView tv_Seconds,tv_Minute,tv_Hours,tv_Date,tv_button,tv_title,tv_Time;
    Calendar calendar = Calendar.getInstance();
    int DAY_OF_MONTH,Race_day=0;
    int minutes ,seconds ,Hours;
    boolean flag = false;
    private static final long START_TIME_IN_MILLIS = 86400000 ;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis= START_TIME_IN_MILLIS;
    ImageButton button_run;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // startActivity(new Intent(getApplicationContext(), MapsActivity.class));

        checkService();
        Boot_variables();
        updateCountDownText();
        ButtonMnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_control_Mnos();
            }
        });
        ButtonPls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_control_Pls();
            }
        });
       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Check_control_bottomNavigationView(item);
               return false;
           }
       });
       button_run.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Checklist_button_run();
           }
       });
    }

    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis=millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;

            }
        }.start();
        mTimerRunning = true;

    }

    private void updateCountDownText(){

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)%60;
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis)%60 ;
        int Hours = (int) TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)%24 ;
      //  int Days = (int)  TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis)%365;

//       tv_Date.setText(String.format(Locale.getDefault(),"%2d",Race_day-DAY_OF_MONTH));

//        tv_Hours.setText(String.format(Locale.getDefault(),"%2d",Hours));
//        tv_Minute.setText(String.format(Locale.getDefault(),"%02d",minutes));
//        tv_Seconds.setText(String.format(Locale.getDefault(),"%2d",seconds));

        tv_Time.setText(String.format(Locale.getDefault(),"%2d"+":"+"%02d"+":"+"%02d"+":"+"%02d",Race_day-DAY_OF_MONTH,Hours,minutes,seconds));

        if(Race_day-DAY_OF_MONTH==0){
          //  button_run.setVisibility(View.VISIBLE);
            tv_button.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.race_day);

        }
        else{

        }

        if(flag==false) {
            flag=true;

            startTimer();
        }
    }

    /**
     * This function removes the race day from the firebase and thereby updates what is relevant
     * @param q
     */

    public void  fb(Query q){
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Race_day=dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * This function is responsible for everything related to data
     * initialization for the activities of the department
     */

    private void Boot_variables() {
        Query q1 = myRef.child("Race_day").orderByValue();
        fb(q1);

        tv_title=findViewById(R.id.Race_title);
//        tv_Date=findViewById(R.id.Date_data);
//        tv_Hours=findViewById(R.id.Hours_data);
//        tv_Minute=findViewById(R.id.Minute_data);
//        tv_Seconds=findViewById(R.id.Seconds_data);
        tv_button=findViewById(R.id.tv_button);
        tv_Time=findViewById(R.id.tv_Time);

        button_run=findViewById(R.id.Button_run);

        Km_data =findViewById(R.id.data);

        ButtonMnos =findViewById(R.id.ButtonMnos);
        ButtonPls =findViewById(R.id.ButtonPls);

        DAY_OF_MONTH = calendar.get(Calendar.DAY_OF_MONTH);

        Hours=calendar.get(Calendar.HOUR_OF_DAY);
        minutes=calendar.get(Calendar.MINUTE);
        seconds=calendar.get(Calendar.SECOND);

        seconds=(int) TimeUnit.SECONDS.toMillis(seconds);
        minutes=(int) TimeUnit.MINUTES.toMillis(minutes);
        Hours=(int) TimeUnit.HOURS.toMillis(Hours);
        //   DAY_OF_WEEK=(int) TimeUnit.DAYS.toMillis(DAY_OF_WEEK);

        mTimeLeftInMillis=START_TIME_IN_MILLIS-(seconds+minutes+Hours);

        bottomNavigationView = findViewById(R.id.BottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.miHome);

    }

    /**
     * This function produces control by pressing the next race button
      */
    private void Check_control_Pls(){
        cont++;

        if(cont<0){
            cont=0;
        }
        else if(cont==0){
            Km_data.setText("2");
            distance=2;
        }
        else if(cont==1){
            Km_data.setText("5");
            distance=5;
        }
        else if(cont==2){
            Km_data.setText("10");
            distance=10;
        }
        else if(cont==3){
            Km_data.setText("21");
            distance=21;
        }
        else if(cont==4){
            Km_data.setText("42");
            distance=42;
        }
        else if(cont>4){
            cont=4;
        }
    }

    /**
     * This function generates control by pressing the previous race button
     */
    private void Check_control_Mnos(){
        cont--;

        if(cont<0){
            cont=0;
        }
        else if(cont==0){
            Km_data.setText("2");
            distance=2;
        }
        else if(cont==1){
            Km_data.setText("5");
            distance=5;
        }
        else if(cont==2){
            Km_data.setText("10");
            distance=10;
        }
        else if(cont==3){
            Km_data.setText("21");
            distance=21;
        }
        else if(cont==4){
            Km_data.setText("42");
            distance=42;
        }
        else if(cont>4){
            cont=4;
        }
    }

    /**
     *This function will generate a response by pressing each of the bottomNavigationView buttons
     * @param item
     * @return
     */
    private boolean Check_control_bottomNavigationView(MenuItem item){
        switch (item.getItemId()){
            case R.id.miEvents:
                startActivity(new Intent(getApplicationContext(), Rating.class));
                return true;
            case R.id.miComment:
                startActivity(new Intent(getApplicationContext(), Messages.class));
                return true;
            case R.id.miHome:
                return false;
            case R.id.logout:
                Intent Intent_Service =new Intent(getApplicationContext(), Service_notification.class);
                stopService(Intent_Service);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                return true;
        }
        return false;
    }

    /**
     * This function will operate by pressing the Go to Race button Function checks if
     * this is the race day will allow a pass if you do not notify the user
     */
    private void Checklist_button_run(){
        if(Race_day-DAY_OF_MONTH==0) {
            Dialog_system_message();

            sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // editor.clear();
            editor.putFloat("distance", distance);
            editor.commit();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.Message_button_run, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This function will bring up a system message before going to the race screen
     */
    public void Dialog_system_message(){
        Dialog_system_message dialog_system_message = new Dialog_system_message();
        dialog_system_message.show(getSupportFragmentManager(),"dialog");
    }

    /**
     * This function will move to the race screen
     */
    @Override
    public void onOkClicked_Dialog_system_message() {
        startActivity(new Intent(getApplicationContext(), Race.class));
    }

    /**
     * This function checks if Service_notification is connected if you do not enable it
     */
    public void checkService()
    {
       /* if(false==isServiceRunning(getApplicationContext(),Service_notification.class))
        {
            Intent Intent_Service =new Intent(getApplicationContext(), Service_notification.class);
            startService(Intent_Service);
        }*/
    }

    /**
     * This function checks if Service is connected then returns true if not returns false
     * @param c
     * @param serviceClass
     * @return if Service is connected then returns true if not returns false
     */
    public boolean isServiceRunning (Context c,Class<?> serviceClass)
    {

        ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }
        return false;
    }
}