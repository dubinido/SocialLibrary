package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android. os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.widget.ListView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Index extends AppCompatActivity implements BookAdapter.OnBookListener  {

    private GoogleSignInClient mGoogleSignInClient;
    public static final String BOOK_ID="id";

    Button btnPersonal, btnSignOut, btnScan;
    ImageButton btnMap;

    static final Integer BOOK_LIMIT=20;
    DatabaseReference databaseBooks;
    ListView listViewBooks;
    List<Book> bookList;

    RecyclerView rvBooks;
    RecyclerView rvGenres;

    DatabaseReference databaseGenres;
    ListView listViewGenres;
    List<Genre> genreList;

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

        btnPersonal = findViewById(R.id.personalbtn);
        btnSignOut = findViewById(R.id.btnSignout);
        btnMap = findViewById(R.id.btnMap);
        btnScan = findViewById(R.id.btnScan);

        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, com.example.sociallibrary.personal.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Index.this, com.example.sociallibrary.MapsActivity.class);
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
                Intent intent = new Intent(Index.this, com.example.sociallibrary.ScanActivity.class);
                startActivity(intent);
            }
        });

        //book list
        databaseBooks = FirebaseDatabase.getInstance().getReference("books");
        //listViewBooks = (ListView) findViewById(R.id.bookList);
        bookList=new ArrayList<>();

        rvBooks = (RecyclerView) findViewById(R.id.rvBookList);

        databaseGenres = FirebaseDatabase.getInstance().getReference("genres");
        //listViewGenres = (ListView) findViewById(R.id.genreList);
        genreList=new ArrayList<>();

        rvGenres = (RecyclerView) findViewById(R.id.rvGenres);

        //this is only for adding example books
        /**
         Button btnAdd = (Button) findViewById(R.id.addTemp);
         btnAdd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        addBook();
        }
        });
         //*/
    }

    /** part od add example
    private void addBook()
    {
        String id = databaseBooks.push().getKey();
        String name = "b1";
        String author = "jk";
        String genre = "scifi";
        String imgUrl = "@drawable/harry";
        double rating = 3;
        String description = "the book is very very very very very very interesting";
        Book book = new Book(id,name,author,rating,description,imgUrl,genre);
        databaseBooks.child(id).setValue(book);

        Toast.makeText(this,"added",Toast.LENGTH_LONG).show();
    }
    // */

    private void signOut() //sign out method
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Index.this, "Singed Out successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Index.this, com.example.sociallibrary.MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
// TODO: GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity()); //this is how to save account of google in USERS table

    //book list display
    @Override
    protected void onStart() {
        super.onStart();

        databaseBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bookList.clear();
                int counter=0;

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                Book book = bookSnapshot.getValue(Book.class);
                bookList.add(book);
                }
                BookAdapter adapter = new BookAdapter(bookList, Index.this );
                //listViewBooks.setAdapter(adapter);
                rvBooks.setAdapter(adapter);

                rvBooks.setLayoutManager(new LinearLayoutManager(Index.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(Index.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                genreList.clear();

                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren())
                {
                    Genre genre = genreSnapshot.getValue(Genre.class);

                    genreList.add(genre);
                }
                //GenreList adapter = new GenreList(Index.this, genreList);
                //listViewGenres.setAdapter(adapter);
                GenreAdapter adapter = new GenreAdapter( genreList);
                //listViewBooks.setAdapter(adapter);
                rvGenres.setAdapter(adapter);

                rvGenres.setLayoutManager(new LinearLayoutManager(Index.this,LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBookClick(int position) {
        Intent intent = new Intent(getApplicationContext(),Product.class);

        intent.putExtra(BOOK_ID,bookList.get(position).getId());

        startActivity(intent);

        Toast.makeText(this,bookList.get(position).getId(),Toast.LENGTH_LONG).show();
    }
}