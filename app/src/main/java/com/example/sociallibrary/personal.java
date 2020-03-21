package com.example.sociallibrary;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class personal extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Button btnMain, btnSignOut;
        TextView tvHelloUser;
        String userName;

        btnMain = findViewById(R.id.btnMain);
        btnSignOut = findViewById(R.id.btnSignout);
        tvHelloUser = findViewById(R.id.tvHelloUser);
        userName = getFirstName();

        tvHelloUser.setText("Hello " + userName);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personal.this, com.example.sociallibrary.index.class);
                startActivity(intent);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }
    private void signOut() //sign out method
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(personal.this, "Singed Out successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(personal.this, com.example.sociallibrary.MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private String getFirstName() // return the user first name
    {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(personal.this);
        if (acct != null) {
            //String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            return personGivenName;
        }
        return "";
    }




}
