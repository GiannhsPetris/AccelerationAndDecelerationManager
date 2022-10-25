package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText mailT, passT;
    Button logButton;
    TextView signText;
    FirebaseAuth fireAuth;
    boolean email = false; // for checking if email edittext is emtpy or not
    boolean pass = false; // for checking if pass edittext is emtpy or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mailT = findViewById(R.id.mailFieldLog);
        passT = findViewById(R.id.passFieldLog);
        logButton = findViewById(R.id.logButton);
        signText = findViewById(R.id.goSign);

        fireAuth = FirebaseAuth.getInstance();
        logButton.setEnabled(false); // disable so it cannot be pressed before entering info on the text fields

        // takes you to sign in page
        signText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        // listeners to check if both text fields (mail and password) are empty or not. if at least one pf them is empty the log in button does not work
        mailT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    logButton.setEnabled(false);
                    email = false;
                }else{
                    email = true;
                    if (pass){
                        logButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    logButton.setEnabled(false);
                    pass = false;
                }else{
                    pass = true;
                    if (email){
                        logButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    //sign in method. takes the password and email the user wrote and checks if the user exists. if it does it opens the main page of the app. if it does not it dispalys the appropriate message
    public void signin(View view){
        fireAuth.signInWithEmailAndPassword(mailT.getText().toString(),passT.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            showMessage("Error",task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }


}