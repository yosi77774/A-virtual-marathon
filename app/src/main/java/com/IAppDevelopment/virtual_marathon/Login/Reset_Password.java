package com.IAppDevelopment.virtual_marathon.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IAppDevelopment.virtual_marathon.R;
import com.IAppDevelopment.virtual_marathon.Race.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class is for the legend that the user forgot the password,
 * The user's e-mail data will be processed,and if necessary,
 * the server's system will be updated and allow the user to change the password.
 */

public class Reset_Password extends AppCompatActivity {

    View view;
    EditText etEmail;
    Button b_Reset_Password;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset__password);

        Boot_variables();

        b_Reset_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reset_Password();
            }
        });
    }

    /**
     * This function is to manage the creation of a user password
     */
    private void Reset_Password(){
        String email = etEmail.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setText(getString(R.string.Email_is_Required_));
            etEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.Email_error));
            etEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.Reset_message,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));

                }else {
                    Toast.makeText(getApplicationContext(), R.string.Try_again,Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
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
        etEmail = findViewById(R.id.etEmail);
        b_Reset_Password = findViewById(R.id.b_Reset_Password);
        progressBar = findViewById(R.id.progressBar);
        mAuth  = FirebaseAuth.getInstance();
    }
}