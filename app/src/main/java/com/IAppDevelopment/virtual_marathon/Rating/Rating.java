package com.IAppDevelopment.virtual_marathon.Rating;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IAppDevelopment.virtual_marathon.Login.Login;
import com.IAppDevelopment.virtual_marathon.Object_classes.data_run;
import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Race.Home;
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

import java.util.ArrayList;

/**
 * This class receives the run data,
 * processes it to fit the display.
 */
public class Rating extends AppCompatActivity {

    BottomNavigationView bottomNavigationView,bottomNavigationView2;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    ArrayList<data_run> myArray = new ArrayList<>();
    ArrayList<data_run> myArray2 = new ArrayList<>();

    ListView lv;
    TextView tv_b_n;

    int cont=0;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Boot_variables();

        bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Check_control_bottomNavigationView2(item);
                   return false;
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
     * A function checks the selected race and generates a query for the fb function
     * @param cont
     */
     public void cont(int cont){
    if(cont==0) {
        tv_b_n.setText(getString(R.string.race_Results)+"  \n  2Km");
        Query q1 = myRef.child("Rating_2km").orderByValue();
        fb(q1);
    }
    else if (cont == 1){
        tv_b_n.setText(getString(R.string.race_Results)+"  \n  5Km");
        Query q2 = myRef.child("Rating_5km").orderByValue();
        fb(q2);
    }
    else if (cont == 2){
        tv_b_n.setText(getString(R.string.race_Results)+"  \n  10Km");
        Query q3 = myRef.child("Rating_10km").orderByValue();
        fb(q3);
    }
    else if (cont == 3){
        tv_b_n.setText(getString(R.string.race_Results)+"  \n  21Km");
        Query q4 = myRef.child("Rating_21km").orderByValue();
        fb(q4);
    }
    else if (cont == 4){
        tv_b_n.setText(getString(R.string.race_Results)+"  \n  42Km");
        Query q5 = myRef.child("Rating_42km").orderByValue();
        fb(q5);
    }
}

    /**
     * This function downloads the entire data history of the runners for the selected race
     * @param q
     */
     public void  fb(Query q){
         q.addListenerForSingleValueEvent(new ValueEventListener() {

             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 myArray.clear();
                 for (final DataSnapshot dst : dataSnapshot.getChildren()) {

                     data_run d_r = dst.getValue(data_run.class);

                     myArray.add(new data_run(d_r.getData_name(),d_r.getPic_url(),d_r.getPlace(),d_r.getTime()));

                 }

                 myArray2.clear();
                 myArray2.addAll(Bubble(myArray)) ;

                 refresh_lv();

             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
             }
         });
     }

    /**
     * This function sorts the data so that the fastest time to a particular assignment is ranked higher
     * @param myArray1
     * @return
     */
    public static ArrayList<data_run> Bubble(ArrayList<data_run> myArray1){

        data_run d_r_tmp = new data_run();
        boolean degel = true;
        int j = 1;

        while (degel){

            degel = false;

            for (int i = 0; i < myArray1.size()-j; i++){

                if(myArray1.get(i).time > myArray1.get(i+1).time){

                    d_r_tmp = myArray1.get(i+1);
                    myArray1.set(i+1, myArray1.get(i));
                    myArray1.set(i, d_r_tmp);
                    degel = true;
                }

            }
            j++;
        }

        return  Add_rating(myArray1);
    }

    /**
     * This function puts the user rating per user
     * @param myArray1
     * @return
     */
    public static ArrayList<data_run> Add_rating(ArrayList<data_run> myArray1){

        for (int i=0 ; i<myArray1.size();i++)
        {

            myArray1.get(i).place = i+1+"";
        }
        return myArray1;
    }

    /**
     * This function transfers the data to the display
     */
    private void refresh_lv () {

        RatingAdapter adp = new RatingAdapter(this, R.layout.row,myArray2);

        lv.setAdapter(adp);
    }

    /**
     * This function is responsible for everything related to data
     * initialization for the activities of the department
     */
    private void Boot_variables(){
        tv_b_n=findViewById(R.id.tv_b_nv);

        bottomNavigationView2 = findViewById(R.id.BottomNavigationView2);
        bottomNavigationView = findViewById(R.id.BottomNavigationView);


        bottomNavigationView.setSelectedItemId(R.id.miEvents);
        bottomNavigationView2.setSelectedItemId(R.id.rating);

        lv = findViewById(R.id.lv_rating);

        tv_b_n.setText(getString(R.string.race_Results)+"  \n  2Km");

        Query q = myRef.child("Rating_2km").orderByValue();
        fb(q);
    }


    /**
     * This function creates constraints for the cont variable
     * @param item
     * @return
     */
    private boolean Check_control_bottomNavigationView2(MenuItem item){

        switch (item.getItemId()) {
            case R.id.up:

                cont++;
                if(cont>=5)
                {
                    cont=4;
                }
                cont(cont);
                return false;
            case R.id.down:

                cont--;
                if(cont<=-1)
                {
                    cont=0;
                }
                cont(cont);
                return false;

        }
        return false;
    }

    /**
     *This function will generate a response by pressing each of the bottomNavigationView buttons
     * @param item
     * @return
     */
    private boolean Check_control_bottomNavigationView(MenuItem item){
        switch (item.getItemId()){
            case R.id.miEvents:
                return false;
            case R.id.miComment:
                startActivity(new Intent(getApplicationContext(), Messages.class));
                return true;
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