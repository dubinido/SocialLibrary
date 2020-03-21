package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class index extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Button btnPersonal, btnSignOut, btnMap, btnScan;

        btnPersonal = findViewById(R.id.personalbtn);
        btnSignOut = findViewById(R.id.btnSignout);
        btnMap = findViewById(R.id.btnMap);
        btnScan = findViewById(R.id.btnScan);

        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(index.this, com.example.sociallibrary.personal.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(index.this, com.example.sociallibrary.MapsActivity.class);
                    startActivity(intent);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(index.this, com.example.sociallibrary.ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signOut() //sign out method
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(index.this, "Singed Out successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(index.this, com.example.sociallibrary.MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
// TODO: GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity()); //this is how to save account of google in USERS table