package com.IAppDevelopment.virtual_marathon.Race;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Dialog;
import com.IAppDevelopment.virtual_marathon.Dialog_and_Notification.Dialog_Accuracy;
import com.IAppDevelopment.virtual_marathon.Object_classes.data_run;
import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Service.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * This class is the class from which the race begins,
 * the class processes data of time, distance,
 * in addition to data that is updated on the map
 */

public class Race extends FragmentActivity implements Dialog.DialogListener, Dialog_Accuracy.DialogListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener,
        DialogInterface.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_CODE_REGISTER =1 ;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private TextView textView,tv_start,tv_stop,btn_start,btn_stop;
    private BroadcastReceiver broadcastReceiver;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    MediaPlayer mp_strart;
    float distance_flag;
    boolean flag_strart=false,flag_strart2=false,flag_strart3=false;
    SharedPreferences sharedPreferences;
    String data_name,pic_url;
    boolean PositiveButton=false;
    ProgressBar progressBar2;


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    textView.setText("" + intent.getExtras().get("coordinates"));
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.f_map);
         mapFragment.getMapAsync(this);

        Download_pic_url();

        Boot_variables();


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) { if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 1000 ){

                    long j=chronometer.getBase();
                    long i=SystemClock.elapsedRealtime();
                    long w=SystemClock.elapsedRealtime()-chronometer.getBase();
                    long w2=SystemClock.elapsedRealtime()-chronometer.getBase();
                } }});

        if(!runtime_permissions()) { enable_buttons();}
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //   requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_REGISTER);
            return;
        }

        registerLocationUpdates();

    }

    @SuppressWarnings("ResourceType")
    private void registerLocationUpdates() {

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);


    }
//    Location location_finish;
//    LatLng lating_finish;
    float n;
    boolean flag_Accuracy=true;
    public void onMyLocationChange(Location location) {

//    if(location.getAccuracy()>7&&flag_Accuracy){
//         n=location.getAccuracy();
//        openDialog_Accuracy();
//        flag_Accuracy=false;
//    }
    //Toast.makeText(getApplicationContext(),location.getProvider(),Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(),String.format(new DecimalFormat("0.00").format(n)),Toast.LENGTH_LONG).show();

//        location_finish=location;

        if (flag_strart2 == false&&flag_strart==false){
            LatLng lating = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lating, 18));
            progressBar2.setVisibility(View.INVISIBLE);

    }
        else if (flag_strart2 == false&&flag_strart==true){
            if(flag_strart3==false) {
                LatLng lating = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lating)
                        .title("start"))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            flag_strart3=true;
            }
        }
        else if(flag_strart2==true && flag_strart3==true) {
//             lating_finish = new LatLng(location.getLatitude(), location.getLongitude());
//           // mMap.moveCamera(CameraUpdateFactory.newLatLng(lating));
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//            mMap.addMarker(new MarkerOptions().position(lating_finish)
//                    .title("finish"))
//                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            flag_strart3=false;
        }

    }

    /**
     * This function ends on responses in case and presses one of the start or pause buttons
     */
    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    mp_strart = MediaPlayer.create(getApplicationContext(), R.raw.start2_run);
                    mp_strart.start();
                    flag_strart=true;
                    tv_start.setVisibility(View.INVISIBLE);
                    btn_start.setVisibility(View.INVISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    tv_stop.setVisibility(View.VISIBLE);


                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset+11000);
                    chronometer.start();
                    running = true;

//                    try {
//                        for (int i=10;i>0;i--) {
//                            Thread.sleep(1000);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    Intent i =new Intent(getApplicationContext(), LocationService.class);
                    startService(i);
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    /**
     * This function is responsible for system responses during downtime
     */
    private void stopRun(){

        if (running ) {
            flag_strart2=true;

            chronometer.stop();
            running = false;
            Intent i =new Intent(getApplicationContext(),LocationService.class);
            stopService(i);

            sharedPreferences = this.getSharedPreferences("bottomnavigationview",this.MODE_PRIVATE);
            int Altitude_sum = sharedPreferences.getInt("Altitude_sum", 0);
            if(Altitude_sum>25||Altitude_sum<-25) {
                Altitude_sum = Altitude_sum * 1500;
            }
            else {
                Altitude_sum = 0;
            }
            long RUN_TIME = (SystemClock.elapsedRealtime() - chronometer.getBase()) + (Altitude_sum);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset-RUN_TIME);
            sharedPreferences = this.getSharedPreferences("bottomnavigationview",this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("RUN_TIME", RUN_TIME);
            editor.commit();
            flag_strart=false;




            data_run dataRun = new data_run(data_name,pic_url,"0",RUN_TIME);
            mp_strart = MediaPlayer.create(getApplicationContext(), R.raw.mp3);

            if(distance_flag==2) {
                if(Checking_Internet_connection()) {
                    myRef.child("Rating_2km").push().setValue(dataRun);
                }
                mp_strart.start();
            }
            else if (distance_flag == 5){
                if(Checking_Internet_connection()) {
                    myRef.child("Rating_5km").push().setValue(dataRun);
                }
                mp_strart.start();
            }
            else if (distance_flag == 10){
                if(Checking_Internet_connection()) {
                    myRef.child("Rating_10km").push().setValue(dataRun);
                }
                mp_strart.start();
            }
            else if (distance_flag == 21){
                if(Checking_Internet_connection()) {
                    myRef.child("Rating_21km").push().setValue(dataRun);
                }
                mp_strart.start();
            }
            else if (distance_flag == 42){
                if(Checking_Internet_connection()) {
                    myRef.child("Rating_42km").push().setValue(dataRun);
                }
                mp_strart.start();
            }
        }
    }


    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }else {
                runtime_permissions();
            }
        }
        registerLocationUpdates();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    /**
     * This function is responsible for listening to changes that occur in memory to update the display
     * @param sharedPreferences
     * @param s
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        sharedPreferences = this.getSharedPreferences("bottomnavigationview",this.MODE_PRIVATE);
        if( sharedPreferences.getBoolean("stop_service", false) == true){
            stopRun();
            flag_strart=false;

            LatLng  lating_finish = new LatLng(sharedPreferences.getFloat("Latitude", 0), sharedPreferences.getFloat("Longitude", 0));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(lating));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.addMarker(new MarkerOptions().position(lating_finish)
                    .title("finish"))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            flag_strart3=false;
            btn_stop.setVisibility(View.INVISIBLE);
            tv_stop.setVisibility(View.INVISIBLE);
        }


        LatLng lating = new LatLng(sharedPreferences.getFloat("Latitude", 0), sharedPreferences.getFloat("Longitude", 0));
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lating,18));

        mMap.addMarker(new MarkerOptions().position(lating)
                .title("run")
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_run2)));

    }

    /**
     * This end function locks the backward option during activity
     */
    @Override
    public void onBackPressed() {


        if( flag_strart == false){
            super.onBackPressed();
        }

        else if(flag_strart == true) {
            Toast.makeText(getApplicationContext(), R.string.b_Return, Toast.LENGTH_LONG).show();
            return;
        }
    }


    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorReasId){
        Drawable vectoDrawable = ContextCompat.getDrawable(context,vectorReasId);
        vectoDrawable.setBounds(0,0,vectoDrawable.getIntrinsicWidth(),
                vectoDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectoDrawable.getIntrinsicWidth(),
                vectoDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectoDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * This last function is to download the url of the user image
     */
    public void Download_pic_url(){
     //   Query q1 = myRef.child("Message").orderByValue();

        sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview",getApplicationContext().MODE_PRIVATE);
        String data_email = sharedPreferences.getString("Email",null);

        final StorageReference imgRef = storageRef.child("profiles").child(data_email);

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                pic_url=uri.toString();

            }
        });
    }

    /**
     * This function is responsible for everything related to data
     * initialization for the activities of the department
     */
    private void Boot_variables(){
        sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview",getApplicationContext().MODE_PRIVATE);
        distance_flag = sharedPreferences.getFloat("distance",0);
        data_name = sharedPreferences.getString("Name",null);
        //  pic_url = sharedPreferences.getString("pic_url",null);

        sharedPreferences = this.getSharedPreferences("bottomnavigationview",this.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop1);
        textView = (TextView) findViewById(R.id.R_data);
        tv_start = findViewById(R.id.tv_start);
        tv_stop = findViewById(R.id.tv_stop);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        progressBar2 = findViewById(R.id.progressBar2);
    }

    /**
     * This function is responsible for displaying a system message
     */
    public void openDialog(){
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(),"dialog");
    }

    public void openDialog_Accuracy(){
        Dialog_Accuracy dialog_accuracy = new Dialog_Accuracy();
        dialog_accuracy.show(getSupportFragmentManager(),"dialog");
    }

    /**
     * This function is responsible for confirming the cessation of activity by clicking OK
     */
    @Override
    public void onOkClicked() {
        Toast.makeText(this, R.string.race_Activity_stopped, Toast.LENGTH_SHORT).show();
        flag_strart=false;
        chronometer.stop();
        running = false;
        Intent i =new Intent(getApplicationContext(),LocationService.class);
        stopService(i);
        btn_stop.setVisibility(View.INVISIBLE);
        tv_stop.setVisibility(View.INVISIBLE);
    }

    /**
     * This function is responsible for continuing the activity by clicking Cancel
     */
    @Override
    public void onCancelClicked() {
        Toast.makeText(this, R.string.race_The_activity_continues, Toast.LENGTH_SHORT).show();
        PositiveButton = false;
    }

    @Override
    public void onOkClicked_Dialog_Accuracy() {
        PositiveButton = false;
    }

    /**
     * This function checks if there is an internet connection if you do not save the race results in favor of their recovery mechanism
     * @return If there is a connection you will return true if not false
     */
    public Boolean Checking_Internet_connection(){

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
           // Toast.makeText(getApplicationContext(), "isConnected", Toast.LENGTH_LONG).show();
            return true;
        }
        else {
          //  Toast.makeText(getApplicationContext(), "not connected", Toast.LENGTH_LONG).show();

            sharedPreferences = this.getSharedPreferences("bottomnavigationview",this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Internet_connection", true);
            editor.putString("pic_url", pic_url);
            editor.commit();

            return false;
        }


    }

}