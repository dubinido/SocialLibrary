package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android. os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Index extends AppCompatActivity implements BookAdapter.OnBookListener ,GenreAdapter.OnGenreListener {

    private GoogleSignInClient mGoogleSignInClient;
    public static final String BOOK_ID="id";

    String genre ="All";
    GenreAdapter genreAdapter;

    Button btnPersonal, btnSignOut, btnScan;
    ImageButton btnMap, btnSearch;

    static final Integer BOOK_LIMIT=20;
    DatabaseReference databaseBooks;
    ListView listViewBooks;
    List<Book> bookList;

    RecyclerView rvBooks;
    RecyclerView rvGenres;

    DatabaseReference databaseGenres;
    ListView listViewGenres;
    List<Genre> genreList;
    EditText editTextBook;

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
        btnSearch = findViewById(R.id.btn_search);
        editTextBook = findViewById(R.id.editTextBook);

        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, com.example.sociallibrary.Personal.class);
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                Log.d("ido", editTextBook.getText().toString() );
                searchBook(editTextBook.getText().toString());

            }
        });

        databaseBooks = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage storage = FirebaseStorage.getInstance(); //create an instance of firebase storage
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        bookList=new ArrayList<>();
        rvBooks = (RecyclerView) findViewById(R.id.rvBookList);
        databaseGenres = FirebaseDatabase.getInstance().getReference("genres");
        genreList=new ArrayList<>();

        rvGenres = (RecyclerView) findViewById(R.id.rvGenres);

        databaseGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                genreList.clear();

                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren())
                {
                    Genre genre = genreSnapshot.getValue(Genre.class);

                    genreList.add(genre);
                }
                genreAdapter = new GenreAdapter(genreList,Index.this);
                rvGenres.setAdapter(genreAdapter);

                rvGenres.setLayoutManager(new LinearLayoutManager(Index.this,LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateBooks();

    }

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
// GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity()); //this is how to save account of google in USERS table

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateBooks()
    {
        Query qBooks = databaseBooks.child("books");
        qBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bookList.clear();

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren())
                {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (genre.equals("All"))
                        bookList.add(book);
                    else if(book.getGenre().equals(genre))
                        bookList.add(book);
                }
                BookAdapter adapter = new BookAdapter(bookList, Index.this );
                rvBooks.setAdapter(adapter);

                rvBooks.setLayoutManager(new LinearLayoutManager(Index.this));
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
    }

    @Override
    public void onGenreClick(int position) {
        genre=genreList.get(position).getGenreName();
        updateBooks();
    }
    public void searchBook(final String bookName)
    {
        bookList.clear();
        Query qSearch = databaseBooks.child("books");
        qSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren())
                {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (book.getName().toLowerCase().equals(bookName.toLowerCase())) {
                        bookList.add(book);
                    }
                }
                BookAdapter adapter = new BookAdapter(bookList, Index.this );
                rvBooks.setAdapter(adapter);
                rvBooks.setLayoutManager(new LinearLayoutManager(Index.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

