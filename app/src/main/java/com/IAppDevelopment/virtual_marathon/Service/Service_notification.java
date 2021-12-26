package com.IAppDevelopment.virtual_marathon.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.IAppDevelopment.virtual_marathon.Login.Login;
import com.IAppDevelopment.virtual_marathon.Object_classes.data_run;
import com.IAppDevelopment.virtual_marathon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Notification.CHANNEL_3_ID;
import static com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Notification.CHANNEL_4_ID;

/**
 * The role of this department is to act as a local server that always
 * operates in the background in order to listen to scheduled messages from the server,
 * and to be a recovery mechanism for a situation where there is no reception to transfer data to the remote server
 */
public class Service_notification extends Service {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
     int hour ;
    int minute;
    String message;
    boolean flag=false;

    public Service_notification() {
    }

    @Override
    public void onCreate() {

        Thread thread1 = new Thread(new notification_Thread());
        thread1.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setContentTitle("Service")
                .setContentText(input)
                .setContentText("Connected")
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(3, notification);

        return START_STICKY;
    }

    /**
     *This function will generate a system message and display it to the user
     * @param message
     */
    private void showNotification(String message){

        flag=false;

        NotificationManagerCompat notificationManagerCompat;
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_4_ID)
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle(getString(R.string.system_notice))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .build();
        notificationManagerCompat.notify(4,notification);

    }

    /**
     * This function receives an hour and a minute and checks whether the time and
     * minute received are present, then return true if not return false
     * @param hour
     * @param minute
     * @return If the time and minute received are present then return true if not return false
     */
    private boolean isTimeArrived(int hour,int minute){
        Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);
        return hh == hour && mm == minute && ss == 00;
    }

    /**
     * This function pauses the system for a second
     */
    private void sleepOneSecond (){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function removes the content of the system message and
     * the time it is scheduled from the Firewall
     * @param q
     */
    public void  fb(Query q){
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                flag=true;
                hour=dataSnapshot.child("hour").getValue(int.class);
               //gi minute=dataSnapshot.child("minute").getValue(int.class);
                message=dataSnapshot.child("message").getValue(String.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * This function checks for an internet connection if there is
     * a connection to the firebox the running results that did not
     * go up due to the fact that there was no internet connection
     */
    public void Checking_Internet_connection(){

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview",getApplicationContext().MODE_PRIVATE);

          String pic_url = sharedPreferences.getString("pic_url","");
           Long RUN_TIME = sharedPreferences.getLong("RUN_TIME",0) ;
            float distance_flag = sharedPreferences.getFloat("distance",0);
           String data_name = sharedPreferences.getString("Name",null);

            data_run dataRun = new data_run(data_name,pic_url,"0",RUN_TIME);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Internet_connection", false);
            editor.putString("pic_url", pic_url);
            editor.commit();

            if(distance_flag==2) {

                    myRef.child("Rating_2km").push().setValue(dataRun);

            }
            else if (distance_flag == 5){

                    myRef.child("Rating_5km").push().setValue(dataRun);

            }
            else if (distance_flag == 10){

                    myRef.child("Rating_10km").push().setValue(dataRun);

            }
            else if (distance_flag == 21){

                    myRef.child("Rating_21km").push().setValue(dataRun);

            }
            else if (distance_flag == 42){

                    myRef.child("Rating_42km").push().setValue(dataRun);

            }


        }


    }

    /**
     *The role of this class is to work in parallel and to enter
     * system changes for class operations. Service_notification
     */
    public class notification_Thread extends Thread {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview",getApplicationContext().MODE_PRIVATE);
        public void run() {

        while (true){
            sleepOneSecond();
            Query q = myRef.child("notification").orderByValue();
            fb(q);

            if (isTimeArrived(hour,minute)&&flag){
                showNotification(message);
            }

            if ( sharedPreferences.getBoolean("Internet_connection",false)==true){
                Checking_Internet_connection();

            }

        }
        }
    }
}
