package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    boolean ans = false;

    private static final String TAG = "MyActivity";
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    private void updateUI(GoogleSignInAccount account) {
        if(account != null) {
            updateUser(account.getId());
            /*Log.e("barak", account.getId());
            Log.e("barak", String.valueOf(isSigned(account.getId())));

            if(isSigned(account.getId()))
            {
                Intent intent = new Intent(MainActivity.this, com.example.sociallibrary.Index.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, com.example.sociallibrary.HomeLocationActivity.class);
                startActivity(intent);
            } */
        }
    }

    private void signIn() //the sign in method - opens the google form to choose user
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        int RC_SIGN_IN = 1;
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
     private void isSigned (String id)
     {
         Log.d("TAG", "ans final "+ans);
         //return updateUser(id);

     }

     private void updateUser(final String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
         ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 Log.d("TAG", "onDataChange:before if ");
                 ans = false;
                 for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                     Log.d("TAG", userSnapshot.getKey());
                     Log.d("TAG", "ans "+ans);
                     if (userSnapshot.getKey().equals(id))
                     {
                         Log.d("TAG", "onDataChange:true ");
                         ans = true;
                         Log.d("TAG", "ans "+ans);
                         Intent intent = new Intent(MainActivity.this, com.example.sociallibrary.Index.class);
                         startActivity(intent);


                     }
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {
             }
         });

         Intent intent = new Intent(MainActivity.this, com.example.sociallibrary.HomeLocationActivity.class);
         startActivity(intent);
     }

}


