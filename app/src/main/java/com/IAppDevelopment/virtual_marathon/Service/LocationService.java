package com.IAppDevelopment.virtual_marathon.Service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Race.Home;

import java.text.DecimalFormat;

import static com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Notification.CHANNEL_2_ID;
import static com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Notification.CHANNEL_ID;

/**
 * This class produces a server that works in the background,
 *  To get data from the user's location even if the user is not in the app,
 * She processes them and stops her work in the background,
 *  While the user is reaching the specified target distance or,
 * If you choose to discontinue an activity proactively,
 * The server code center works in a parallel process,
 * Because of the load created.
 */
public class LocationService extends Service {

   // FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference();
    double startLatitude = 0;
    double startLongitude = 0;
    float sumdistance, sumdistance2,distance_flag;
    String msg2="00.00";
    private LocationListener listener;
    private LocationManager locationManager;
    private HandlerThread handlerThread = new HandlerThread("HandlerThread");
    private Handler threadHandler;
    SensorManager sensorManager;
    Sensor sensor;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
//    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        handlerThread.start();
        threadHandler=new Handler(handlerThread.getLooper());
        threadHandler.post(new run1());

        Thread thread = new Thread(new stepCount());
        thread.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RunService")
                .setContentText(input)
                .setContentText("The race has begun")
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
        handlerThread.quit();
    }


    /**
     * The role of this department is to work in parallel in the background
     */
    public class run1 implements Runnable {

        SharedPreferences  sharedPreferences2;
        double Altitude_start,Altitude_stop;
        int Altitude_sum;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview",getApplicationContext().MODE_PRIVATE);

        @Override
        public void run() {

                                try {
                        for (int i=10;i>0;i--) {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            distance_flag = sharedPreferences.getFloat("distance",0);

           // sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("stop_service", false);
            editor.commit();


            //פעולות בזמן ההזנה לשינוי תנועה
            listener = new LocationListener() {

            @Override
                public void onLocationChanged(Location location) {

              //חישוב גובה התחלתי
                if(sumdistance2==0){
                     Altitude_start = location.getAltitude();
                }


             //   sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //עידכון הזיכרון במיקום המשתמש
                if (location!=null){
                    editor.putFloat("Latitude", (float) location.getLatitude());
                    editor.putFloat("Longitude",(float) location.getLongitude());
                editor.commit();
                }

                    Intent i = new Intent("location_update");
                    i.putExtra("coordinates", msg2 );
                    sendBroadcast(i);

                    double endLatitude = location.getLatitude();
                    double endLongitude = location.getLongitude();

                    //פעולות לביצוע מתחילת תנועת המשתמש
                    if(startLatitude!=0 && startLongitude!=0) {

                        Location crntLocation=new Location("crntlocation");
                        crntLocation.setLatitude(startLatitude);
                        crntLocation.setLongitude(startLongitude);

                        Location newLocation=new Location("newlocation");
                        newLocation.setLatitude(endLatitude);
                        newLocation.setLongitude(endLongitude);

                        float distance = crntLocation.distanceTo(newLocation);
                        float distance2 =crntLocation.distanceTo(newLocation) / 1000;
                        sumdistance+=distance;
                        sumdistance2+=distance2;

                        msg2 = String.format(new DecimalFormat("0.00").format(sumdistance2));


                        //פעולות לביצוע בזמן סיום המרחק
                        if(sumdistance2>=distance_flag){

                            Altitude_stop=location.getAltitude();

                            Checking_height_differences(Altitude_start,Altitude_stop );
                            boolean stop_service=true;

                         //   SharedPreferences  sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.putBoolean("stop_service", stop_service);
                            editor1.commit();


                        }

                        //פעולות לביצוע במידה וקמות הצעדים אינה תקינה
                        if(sumdistance2>00.100){
                            if(stepCount/sumdistance2>500){
                                NotificationManagerCompat notificationManagerCompat;
                                notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_2_ID)
                                        .setSmallIcon(R.drawable.ic_run)
                                        .setContentTitle(getString(R.string.system_notice))
//                                        .setContentText("המערכת מצאה כי יתכן שיש בעיה בנתונים המוזנים, \nאליה ולכן נאלצה לעצור את הפעילות")
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getString(R.string.Message_Non_running_activity)))
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setCategory(NotificationCompat.CATEGORY_ERROR)
                                        .build();
                                notificationManagerCompat.notify(2,notification);
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                                stopService(intent);
                            }
                        }

                        //בדיקת טווח דיוק GPS
//                        if( location.getAccuracy()>=7){
//                            Toast.makeText(getApplicationContext(),String.format(new DecimalFormat("0.00").format(location.getAccuracy())),Toast.LENGTH_LONG).show();
//                        }

                    }
                    startLatitude=endLatitude;
                    startLongitude=endLongitude;

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            };

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            //noinspection MissingPermission
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

        }

        /**
         * This function is responsible for calculating the control height
         * differences in case time needs to be added or lowered
         * @param Altitude_start
         * @param Altitude_stop
         */
        //חישוב הפרשי גובה
        private void Checking_height_differences(double Altitude_start,double Altitude_stop ){

            //חישוב מרחק הירידה
            if(Altitude_start>Altitude_stop) {
                if (Altitude_start <= 0) //
                    Altitude_sum = (int) ((Altitude_stop  * (-1))+Altitude_start);
                else if (Altitude_stop >=0)
                    Altitude_sum = (int) (Altitude_start + Altitude_stop*(-1));
                else if (Altitude_start>=0&&Altitude_stop<=0)
                    Altitude_sum = (int) (Altitude_start + Altitude_stop);

            }
            //חישוב מרחק עליה
            else if (Altitude_stop>Altitude_start){
                if (Altitude_stop<=0||Altitude_stop>=0&&Altitude_start<=0)
                    Altitude_sum = (int) (Altitude_start - Altitude_stop );
//               else if (Altitude_stop>=0&&Altitude_start<=0)
//                    Altitude_sum = (int) (Altitude_start - Altitude_stop);
               else if (Altitude_stop>=0&&Altitude_start>=0)
                    Altitude_sum = (int) ((Altitude_stop-Altitude_start)*(-1));

            }


          //  SharedPreferences  sharedPreferences3 = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor3 = sharedPreferences.edit();
            editor3.putInt("Altitude_sum", Altitude_sum);
            editor3.commit();
        }
    }


    /**
     * The role of this class is to work in parallel in the background.
     * It uses the oscillation sensor in the device to calculate steps.
     */
    public class stepCount extends Thread {
        public void run() {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            SensorEventListener stepDetector = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {

                    if (sensorEvent != null) {
                        float x_acceleration = sensorEvent.values[0];
                        float y_acceleration = sensorEvent.values[1];
                        float z_acceleration = sensorEvent.values[2];

                        double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                        double MagnitudeDelta = Magnitude - MagnitudePrevious;
                        MagnitudePrevious = Magnitude;

                        if (MagnitudeDelta > 4) {
                            stepCount++;
                        }
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
//            String st = String.format("%d", stepCount);
            sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }
}
