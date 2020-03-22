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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class personal extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    Button btnMain, btnSignOut;
    TextView tvHelloUser;
    ImageView ivPersonImg;
    String userName;
    String userImg;

    static final Integer BOOK_LIMIT = 20;
    DatabaseReference databaseBooks;
    ListView listViewBooks;
    List<Book> bookList;

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

        btnMain = findViewById(R.id.btnMain);
        btnSignOut = findViewById(R.id.btnSignout);
        tvHelloUser = findViewById(R.id.tvHelloUser);
        ivPersonImg = findViewById(R.id.ivPersonalUserImg);
        userName = getFirstName();
        userImg = getProfileImg();


        tvHelloUser.setText("Hello " + userName);
        if (userImg=="")
            ivPersonImg.setImageResource(R.drawable.profile_pic);
        else
            Picasso.get().load(userImg).into(ivPersonImg);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personal.this, com.example.sociallibrary.Index.class);
                startActivity(intent);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        databaseBooks = FirebaseDatabase.getInstance().getReference("books");
        listViewBooks = (ListView) findViewById(R.id.listBooksPersonal);
        bookList = new ArrayList<>();


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
    private String getProfileImg() // return the user profile
    {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(personal.this);
        String personProfileImg;
        if (acct != null) {
            if (acct.getPhotoUrl()!=null) {
                personProfileImg = acct.getPhotoUrl().toString();
                if (personProfileImg!="")
                {
                    return personProfileImg;
                }
            }
        }
        return "";
    }


    //book list display
    @Override
    protected void onStart() {
        super.onStart();

        databaseBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bookList.clear();
                int counter = 0;

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    if (counter < BOOK_LIMIT) {
                        Book book = bookSnapshot.getValue(Book.class);

                        bookList.add(book);
                        counter++;
                    }
                }
                BookList adapter = new BookList(personal.this, bookList);
                listViewBooks.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
