package com.example.sociallibrary;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.StringSearch;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private EditText mSearchText;
    private GoogleMap mMap;
    private ImageButton btnSearch;
    private LatLng latLngAddress;
    ImageButton btnV;

    String userId;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mSearchText = findViewById(R.id.input_search);
        btnSearch = findViewById(R.id.btn_search_home);
        btnV = findViewById(R.id.btn_v);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    geoLocate();
                    closeKeyboard();
            }
        });

        acct = GoogleSignIn.getLastSignedInAccount(this); //this is how to save account of google in USERS table
        userId = acct.getId();

        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseBooks = FirebaseDatabase.getInstance().getReference();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                double lat = latLngAddress.latitude; //latitude
                double lng = latLngAddress.longitude; // longtitude
                databaseBooks.child("users").child(userId).child("userName").setValue(acct.getDisplayName());
                databaseBooks.child("users").child(userId).child("lat").setValue(lat);
                databaseBooks.child("users").child(userId).child("lng").setValue(lng);
                databaseBooks.child("users").child(userId).child("wishlist").child("NotABook").setValue("1");
                databaseBooks.child("users").child(userId).child("books").child("NotABook").setValue("1");
                databaseBooks.child("users").child(userId).child("borrowed").child("NotABook").setValue("1");
                databaseBooks.child("users").child(userId).child("id").setValue(userId);
                storageReference.child("users");
                
                Intent intent = new Intent(HomeLocationActivity.this, com.example.sociallibrary.Index.class);
                startActivity(intent);

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

    private void init()
    {
        Log.d("init", "initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }
                    return false;
            }
        });

    }

    private void geoLocate(){
        Log.d("geoLocate", "geoLocate method");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(HomeLocationActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e){
            Log.e("error", "geolocate error: " + e.getMessage());
        }

        if(list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            latLngAddress = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngAddress));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAddress, 18));
            MarkerOptions markerOptions = new MarkerOptions().position(latLngAddress)
                    .title("My Home");//this is how to put icon for book
            mMap.addMarker(markerOptions);
            btnV.setVisibility(View.VISIBLE);
            closeKeyboard();
        }

    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

            LatLng israelLatLng = new LatLng(31.2175427,34.6095679);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(israelLatLng));
        init();
    }



}
