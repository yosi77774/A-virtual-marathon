package com.IAppDevelopment.virtual_marathon.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IAppDevelopment.virtual_marathon.Object_classes.User;
import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Race.Home;
import com.IAppDevelopment.virtual_marathon.Service.Service_notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * This department absorbs new users, by checking the data obtained from them,
 * And uploads them to the server if found normal,
 * In addition, it connects existing users to the application.
 */
public class Login extends AppCompatActivity {

    View view;
    Button BSave,BLogin;
    EditText Email,Password,Name;
    TextView tvResetPassword;
    ImageView Image,Image_touch;
    String edEmail,edPassword,edName;
    byte[] data3=null;
    SharedPreferences sharedPreferences;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    String pic_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Boot_variables();

        BSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checklist_BSave();
            }
        });
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checklist_Image();
            }

        });
        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checklist_BLogin();
            }
        });
        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getApplicationContext(), Reset_Password.class)); }
        });
    }

    /**
     * This function checks if the user is connected to
     * the system and will then move to the home screen
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null){
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
    }

    /**
     * This function manages the user's connection to the system
     */
    public void loin(){

        mAuth.signInWithEmailAndPassword(edEmail, edPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("Name", edName.toString());
                            editor.putString("Email", edEmail.toString());
                            editor.commit();
                            Intent Intent_Service =new Intent(getApplicationContext(), Service_notification.class);
                            startService(Intent_Service);
                            startActivity(new Intent(getApplicationContext(), Home.class));

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.Login_error,Toast.LENGTH_LONG).show();

                        }


                    }
                });

    }

    /**
     * This function is responsible for saving the data required for system activity
     */
    public void save(){

        if(data3==null){
            Toast.makeText(getApplicationContext(), R.string.No_image_error,Toast.LENGTH_LONG).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(edEmail, edPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Add_pic_url();

                                myRef.child("User").child(edName).child("Uid").setValue(FirebaseAuth.getInstance().getUid());
                                myRef.child("User").child(edName).child("pic_url").setValue(pic_url);

                                sharedPreferences = getApplicationContext().getSharedPreferences("bottomnavigationview", getApplicationContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putString("Name", edName.toString());
                                editor.putString("Email", edEmail.toString());
                                editor.commit();

                            Intent Intent_Service =new Intent(getApplicationContext(),Service_notification.class);
                            startService(Intent_Service);

                            startActivity(new Intent(getApplicationContext(), Home.class));
                            }

                        else {

                            Toast.makeText(getApplicationContext(), R.string.Registration_error,Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    /**
     * This function is responsible for accessing the camera and creating an image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //Picture intoe ImageView
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Image.setImageBitmap(imageBitmap);

            Image_touch.setVisibility(View.INVISIBLE);
            // Get the data from an ImageView as bytes

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();
            data3 = baos.toByteArray();
        }
    }

    /**
     * This function is responsible for saving the image in Firebase
     */
    public void Add_pic_url(){

        UploadTask uploadTask = storageRef.child("profiles").child(edEmail.toString()).putBytes(data3);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final StorageReference imgRef = storageRef.child("profiles").child(edEmail.toString());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pic_url=uri.toString();
                    }
                });

            }
        });

    }

    /**
     * This function is responsible for everything related to data
     * initialization for the activities of the department
     */
    private void Boot_variables(){
        view = this.getWindow().getDecorView();
        view.setBackgroundResource(R.color.colorWhite);

        tvResetPassword=findViewById(R.id.Reset_Password);

        BSave=findViewById(R.id.Registration);
        BLogin=findViewById(R.id.Login);

        Email=findViewById(R.id.editTextTextEmailAddress);
        Password=findViewById(R.id.editTextTextPassword);
        Name=findViewById(R.id.editTextTextPersonName);
        Image=findViewById(R.id.imageView);
        Image_touch=findViewById(R.id.imageView3);

        mAuth  = FirebaseAuth.getInstance();
    }

    /**
     * This function is responsible for checking the user
     * data before saving and alerting the user if the data is incorrect
     */
    private void Checklist_BSave(){
        edEmail = Email.getText().toString();
        edPassword = Password.getText().toString();
        edName = Name.getText().toString();

         User user = new User(edName,edPassword,edEmail);

        user.pic_url = pic_url;

        if (TextUtils.isEmpty(edName)){
            Name.setError(getString(R.string.Name_is_Required_));
            return;
        }

        if (TextUtils.isEmpty(edEmail)){
            Email.setError(getString(R.string.Email_is_Required_));
            return;
        }

        if(TextUtils.isEmpty(edPassword)){
            Password.setError(getString(R.string.Password_is_Requird_));
            return;
        }

        if(edPassword.length()<6){
            Password.setError(getString(R.string.Password_Must_be_6_Characters));
            return;
        }

        save();
    }

    /**
     * This function is responsible for performing tests of the user
     * data before connecting to the system and alerting the user if the data is incorrect
     */
    private void Checklist_BLogin(){
        edEmail = Email.getText().toString();
        edPassword = Password.getText().toString();
        edName = Name.getText().toString();

        User user = new User(edName,edPassword,edEmail);

        user.pic_url = pic_url;

        if (TextUtils.isEmpty(edName)){
            Name.setError(getString(R.string.Name_is_Required_));
            return;
        }

        if (TextUtils.isEmpty(edEmail)){
            Email.setError(getString(R.string.Email_is_Required_));
            return;
        }

        if(TextUtils.isEmpty(edPassword)){
            Password.setError(getString(R.string.Password_is_Requird_));
            return;
        }

        if(edPassword.length()<6){
            Password.setError(getString(R.string.Password_Must_be_6_Characters));
            return;
        }

        loin();
    }

    private void Checklist_Image(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePictureIntent, 123);}
    }
}