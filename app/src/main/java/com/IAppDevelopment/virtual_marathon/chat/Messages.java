package com.IAppDevelopment.virtual_marathon.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.IAppDevelopment.virtual_marathon.Login.Login;
import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Race.Home;
import com.IAppDevelopment.virtual_marathon.Rating.Rating;
import com.IAppDevelopment.virtual_marathon.Service.Service_notification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class receives the user data of the messages from the online server,
 * and processes them to make them suitable for display.
 */
public class Messages extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    SharedPreferences sharedPreferences;

    RecyclerView recyclerView;
    EditText ed_message;
    ArrayList<com.IAppDevelopment.virtual_marathon.chat.message> message = new ArrayList<>();
    ArrayList<com.IAppDevelopment.virtual_marathon.chat.message> message1 = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    String   pic_url;

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Boot_variables();

        Download_pic_url();

        Query q = myRef.child("Message").orderByValue();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                message1.clear();
                Query q2 = myRef.child("Message").orderByValue();

                for (final DataSnapshot dst : dataSnapshot.getChildren()) {

                    com.IAppDevelopment.virtual_marathon.chat.message m = dst.getValue(com.IAppDevelopment.virtual_marathon.chat.message.class);

                        message1.add(new message(m.getData(),m.getMy_name(),m.getMessage(),m.getPic_url()));

                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Messages.this);
                recyclerView.setLayoutManager(layoutManager);


                messageAdapter messageAdapter = new messageAdapter(message1);
                recyclerView.setAdapter(messageAdapter);

                if(!isVisible()){
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Check_control_bottomNavigationView(item);

                return false;
            }
        });
    }

    /**
     * This function is responsible for receiving the message and inserting it into Fairbase
     * @param view
     */
    public void ib_onclick(View view) {

        if (!TextUtils.isEmpty(ed_message.getText())) {
            Query q1 = myRef.child("Message").orderByValue();

            sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
            String data_name = sharedPreferences.getString("Name", null);

            message.add(new message(
                            " " + getFormatedDateTime(calendar.getTime()),
                            data_name,
                            ed_message.getText().toString() + "",
                            pic_url

                    )

            );
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    myRef.child("Message").push().setValue(message.get(message.size() - 1));

                    message.clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        ed_message.setText("");
        }
    }


    /**
     * This function is responsible for creating a date format for a field in the data structure of the message
     * @param d
     * @return Returns date format
     */
    public static String getFormatedDateTime(Date d)
    {

        SimpleDateFormat dateFormatter  = new SimpleDateFormat("HH:mm"+" "+"dd-MM-yy");

        return dateFormatter.format(d);

    }

    /**
     * This last function is to download the url of the user image
     */
    public void Download_pic_url(){
        Query q1 = myRef.child("Message").orderByValue();

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



    public boolean isVisible(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfLastVisibletItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        return (positionOfLastVisibletItem>=itemCount);
    }

    /**
     * This function is responsible for everything related to data
     * initialization for the activities of the department
     */
    private void Boot_variables(){
        ed_message = findViewById(R.id.ed_messsage);

        recyclerView = findViewById(R.id.rv_message);

        bottomNavigationView = findViewById(R.id.BottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.miComment);
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
                return false;
            case R.id.miHome:
                startActivity(new Intent(getApplicationContext(), Home.class));
                return true;
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
}