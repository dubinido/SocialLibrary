package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.sociallibrary.Index.BOOK_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Drawable iconPin;
    public static Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    DatabaseReference databaseBooks;
    List<Book> books;
    List<String> booksIds;
    public static final String USER_ID="userId";
    RecyclerView rvBooks;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseBooks = FirebaseDatabase.getInstance().getReference();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, com.example.sociallibrary.Index.class);
                startActivity(intent);
            }
        });
        books = new ArrayList<>();
        booksIds = new ArrayList<>();
        rvBooks = (RecyclerView) findViewById(R.id.rvUserBookBorrow);
    }

    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                    if(location != null)
                    {
                        currentLocation = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(MapsActivity.this);
                    }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap my_smallMarker = Bitmap.createScaledBitmap(b,120,160,false);
        mMap = googleMap;
        // this is my current location
        LatLng latLng;
        if (currentLocation != null){
            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(my_smallMarker)); //this is how to put icon for book
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13)); // select the best zoom for us between 2 to 21
            // 21 is the closest
            googleMap.addMarker(markerOptions); // my marker - my place
            placeBooks(googleMap, databaseBooks);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //TODO: barak - marker.getTag() return the userKey, do your on click herhe
                    Log.d("ido", "user name is " + marker.getTag());
                    Intent intent = new Intent(MapsActivity.this,UserBookBorrow.class);
                    intent.putExtra(USER_ID,marker.getTag().toString());
                    intent.putExtra("MyLat",String.valueOf(currentLocation.getLatitude()));
                    intent.putExtra("MyLng",String.valueOf(currentLocation.getLongitude()));
                    startActivity(intent);
                    return false;

                }
            });
        }

    }

    private void placeBooks(final GoogleMap googleMap, DatabaseReference databaseBooks) {
        Query qUsers = databaseBooks.child("users");
        qUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    User user = userSnapshot.getValue(User.class);
                    placeMarker(googleMap, user.getLocation(), user.getUserName(), userSnapshot.getKey());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    fetchLastLocation();
                }
                break;
        }
    }

    public void placeMarker(GoogleMap googleMap, LatLng bookLocation, String userName, String userKey) //gets a bookLoc and place a pin
    {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_book_map);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,120,160,false);
        MarkerOptions markerOptions = new MarkerOptions().position(bookLocation)
                .title(userName) // sets user name
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)); //this is how to put icon for book
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(userKey);
    }

    public static Location currentLocation()
    {
        return currentLocation;
    }


}
