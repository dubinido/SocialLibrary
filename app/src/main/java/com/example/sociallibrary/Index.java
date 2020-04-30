package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;


public class Index extends AppCompatActivity implements BookAdapter.OnBookListener, GenreAdapter.OnGenreListener,MyBookAdapter.OnBookListener {

    private GoogleSignInClient mGoogleSignInClient;
    public static final String BOOK_ID = "id";
    public static final String BOOK_ISBN = "isbn";
    public static final String USER_ID = "userId";
    List<Book> dataBooks;
    double minRate = 0;
    HashMap<String, String> listNames;//in check

    String genre = "All";
    GenreAdapter genreAdapter;

    LatLng userLoc;
    Button btnLoc;

    Button btnPersonal, btnSignOut, btnScan;
    ImageButton btnMap, btnSearch;

    DatabaseReference databaseBooks;
    List<Book> bookList;

    RecyclerView rvBooks;
    RecyclerView rvGenres;

    DatabaseReference databaseGenres;
    ListView listViewGenres;
    List<Genre> genreList;
    EditText editTextBook;

    Spinner spinnerRating;

    List<String> isbn;
    List<User> userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        dataBooks = new ArrayList<>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // userLoc =

        btnPersonal = findViewById(R.id.personalbtn);
        btnSignOut = findViewById(R.id.btnSignout);
        btnMap = findViewById(R.id.btnMap);
        btnScan = findViewById(R.id.btnScan);
        btnSearch = findViewById(R.id.btn_search);
        editTextBook = findViewById(R.id.editTextBook);
        spinnerRating = findViewById(R.id.spinnerRate);

        spinnerRating.setSelection(0, true);
        View v = spinnerRating.getSelectedView();
        ((TextView) v).setTextColor(Color.BLACK);

        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, Personal.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, MapsActivity.class);
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
                //Intent intent = new Intent(Index.this, com.example.sociallibrary.AddBook.class);
                //startActivity(intent);
                // to add books
                Intent intent = new Intent(Index.this, ScanActivity.class);
                startActivity(intent);

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                Log.d("ido", editTextBook.getText().toString());
                searchBook(editTextBook.getText().toString());

            }
        });

        editTextBook.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("TAG", "onEditorAction: ");
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    closeKeyboard();
                    searchBook(editTextBook.getText().toString());
                }
                return false;
            }
        });

        databaseBooks = FirebaseDatabase.getInstance().getReference();

        bookList = new ArrayList<>();
        rvBooks = (RecyclerView) findViewById(R.id.rvBookList);
        databaseGenres = FirebaseDatabase.getInstance().getReference("genres");
        genreList = new ArrayList<>();

        rvGenres = (RecyclerView) findViewById(R.id.rvGenres);

        databaseGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                genreList.clear();

                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren()) {
                    Genre genre = genreSnapshot.getValue(Genre.class);

                    genreList.add(genre);
                }
                genreAdapter = new GenreAdapter(genreList, Index.this);
                rvGenres.setAdapter(genreAdapter);

                rvGenres.setLayoutManager(new LinearLayoutManager(Index.this, LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        isbn = new ArrayList<>();
        userId = new ArrayList<>();
        listNames = new HashMap<>();
        Log.d("barak check1:", "oncreate");


        /** userLoc intial**/
        btnLoc = (Button) findViewById(R.id.btnLoc);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLocation();
            }
        });
        ActivityCompat.requestPermissions(Index.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        Log.d("barak check2:", "oncreate");

        spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String txt = String.valueOf(parent.getItemAtPosition(position));
                ((TextView) view).setTextColor(Color.BLACK);
                minRate = Double.parseDouble(txt);
                updateBooks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        btnLoc.performClick();
        Log.d("userLoc","second "+userLoc.toString());
        createBooks();
    }

    private void createBooks()
    {
        Query qBook = databaseBooks.child("books");
        Log.d("barak check1:", "create 1");
        qBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    dataBooks.add(book);
                }
                Log.d("barak check1:", "finally");
                getListOfBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateBooks()
    {
        Log.d("isbn size: ",String.valueOf(isbn.size()));
        bookList.clear();
        for (int i=0; i<isbn.size();i++)
        {
            for (int j=0; j<dataBooks.size();j++)
            {
                Book book=dataBooks.get(j);
                if (book.getId().equals(isbn.get(i))) {
                    if (book.getRating() >= minRate) {
                        book.setUser(userId.get(i));
                        if (genre.equals("All"))
                            bookList.add(book);
                        else if (book.getGenre().equals(genre))
                            bookList.add(book);
                    }
                }

            }
            bookList=bubbleSort(bookList);
            for (int k=0; i<bookList.size();i++)
                Log.d("grade: ",String.valueOf(bookList.get(i).getGrade(userLoc)));
            Log.d("booklist 2: ",String.valueOf(bookList.size()));
            MyBookAdapter adapter = new MyBookAdapter(bookList, Index.this,userLoc);
            rvBooks.setAdapter(adapter);
            rvBooks.setLayoutManager(new LinearLayoutManager(Index.this));
        }
    }
    @Override
    public void onBookClick(int position) {
        Intent intent = new Intent(getApplicationContext(),Product.class);
        intent.putExtra(BOOK_ID,bookList.get(position).getId());
        intent.putExtra(USER_ID,bookList.get(position).getUser().getId());
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
                    if (book.getName().toLowerCase().contains(bookName.toLowerCase()) || bookName.toLowerCase().contains(book.getName().toLowerCase()) ) {
                        bookList.add(book);
                    }
                }
                MyBookAdapter adapter = new MyBookAdapter(bookList, Index.this,userLoc );
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

    private void getListOfBooks(){
        isbn.clear();
        userId.clear();
        Query qUsers = databaseBooks.child("users");
        qUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnap : dataSnapshot.getChildren())
                {
                    User user = userSnap.getValue(User.class);
                    List<String> keys = new ArrayList<>();
                    keys.addAll(user.getBooks().keySet());
                    for (int counter = 0; counter < keys.size(); counter++) {
                        isbn.add(keys.get(counter));
                        userId.add(user);
                    }
                }
                Log.d("barak check2:", "finally");
                updateBooks();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private List<Book> bubbleSort(List<Book> arr) {
        int i, j;
        if(userLoc!=null) {

            for (i = 0; i < arr.size() - 1; i++) {
                for (j = 0; j < arr.size() - i - 1; j++) {
                    if (arr.get(j).compare(arr.get(j + 1), userLoc) < 0) {
                        Book temp = arr.get(j);
                        arr.set(j, arr.get(j + 1));
                        arr.set(j + 1, temp);
                    }
                }
            }
        }
        else
        {
            btnLoc.performClick();
            bubbleSort(arr);
        }
        return arr;
    }

    private void makeLocation()
    {
        GpsTracker gt = new GpsTracker(getApplicationContext());
        Location l = gt.getLocation();
        if( l == null){
            Log.d("location :","unable");
            Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
        }else {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            Log.d("location :","fixed");
            userLoc=new LatLng(lat,lon);
            Log.d("userLoc",userLoc.toString());
        }
    }
}

