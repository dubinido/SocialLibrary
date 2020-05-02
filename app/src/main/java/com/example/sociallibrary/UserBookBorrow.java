package com.example.sociallibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserBookBorrow extends AppCompatActivity implements BookAdapter.OnBookListener {
    DatabaseReference dbRef;
    List<String> booksIds;
    List<Book> books;
    String id;
    String name;
    LatLng  bookLocation;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    TextView btnUserBack;

    TextView userName,tvUserDistance,tvUserBookBorrowEmpty;
    RecyclerView rvBooks;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_book_borrow);
        dbRef = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        id = intent.getStringExtra(MapsActivity.USER_ID);
        booksIds = new ArrayList<>();
        books = new ArrayList<>();
        tvUserBookBorrowEmpty = (TextView) findViewById(R.id.tvUserBookBorrowEmpty);
        userName = (TextView) findViewById(R.id.tvUserBookBorrow);
        setName();
        rvBooks = (RecyclerView) findViewById(R.id.rvUserBookBorrow);
        makeTheList();
        tvUserDistance = (TextView) findViewById(R.id.tvUserdistance);

        btnUserBack = (TextView) findViewById(R.id.btnUserBack);
        btnUserBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserBookBorrow.this, MapsActivity.class);
                intent.putExtra("mapUser",id);
                startActivity(intent);
            }
        });
        double myLat = Double.parseDouble(intent.getStringExtra("MyLat"));
        double myLng = Double.parseDouble(intent.getStringExtra("MyLng"));
        LatLng myLatLng = new LatLng(myLat, myLng);
        setTvUserDistance(myLatLng);
    }


    @Override
    public void onBookClick(int position) {
        Intent intent = new Intent(getApplicationContext(),Product.class);

        intent.putExtra(Index.BOOK_ID,books.get(position).getId());
        intent.putExtra(Index.USER_ID,books.get(position).getUser().getId());
        startActivity(intent);

        startActivity(intent);
    }

    private void makeTheList()
    {
        Query createList = dbRef.child("users").child(id).child("books");
        createList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booksIds.clear();
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    booksIds.add(bookSnapshot.getKey());
                }
                updateBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateBooks()
    {
        Query getBooks = dbRef.child("books");

        getBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                books.clear();
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (booksIds.contains(book.getId()))
                        books.add(book);
                }
                if (books.isEmpty())
                    tvUserBookBorrowEmpty.setVisibility(View.VISIBLE);
                else
                    tvUserBookBorrowEmpty.setVisibility(View.GONE);
                BookAdapter adapterBooks = new BookAdapter(books, UserBookBorrow.this);
                rvBooks.setAdapter(adapterBooks);
                rvBooks.setLayoutManager(new LinearLayoutManager(UserBookBorrow.this));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setTvUserDistance(final LatLng current){
        Query getName = dbRef.child("users").child(id);
        getName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bookLocation = new LatLng(user.getLat(),user.getLng());
                getDistance(current,bookLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setName()
    {
        Query getName = dbRef.child("users").child(id);
        getName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getUserName();
                userName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        tvUserDistance.setText(new DecimalFormat("##.#").format(Radius*c)+" Km");
    }







}
