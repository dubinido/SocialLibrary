package com.example.sociallibrary;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

import static com.example.sociallibrary.MapsActivity.currentLocation;

public class Product extends AppCompatActivity {
    Book book;
    DatabaseReference databaseBooks;
    String id,userBookId;
    String userId;
    GoogleSignInAccount acct;

    TextView tvProductId;
    TextView tvProductName,tvProductDescription;
    TextView tvProductAuthor;
    RatingBar rbProductRating;
    TextView tvProductGenre;
    Button btnCloseProduct,btnRate;
    RatingBar rbProduct;
    ImageButton btnWishlist,btnBookMap;
    ImageButton btnAddBorrow;
    AlertDialog dialog;
    ImageView imgProductImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        Intent intent = getIntent();
        id = intent.getStringExtra(Index.BOOK_ID);
        userBookId = intent.getStringExtra(Index.USER_ID);

        databaseBooks = FirebaseDatabase.getInstance().getReference();
        acct = GoogleSignIn.getLastSignedInAccount(this); //this is how to save account of google in USERS table
        userId = acct.getId();
        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductAuthor = (TextView) findViewById(R.id.tvProductAuthor);
        rbProductRating = (RatingBar) findViewById(R.id.rbProductRating);
        tvProductGenre = (TextView) findViewById(R.id.tvProductGenre);
        btnCloseProduct = (Button) findViewById(R.id.btnCloseProduct);
        btnRate = (Button) findViewById(R.id.btnRate);
        tvProductDescription=(TextView) findViewById(R.id.tvProductDescription);

        rbProduct = (RatingBar) findViewById(R.id.rbProduct);
        imgProductImg = (ImageView) findViewById(R.id.tvProductImg);

        btnWishlist = (ImageButton) findViewById(R.id.btnAddWishlist);
        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishlist();
                Toast.makeText(Product.this,"added to wishlist",Toast.LENGTH_LONG).show();
            }
        });


        btnBookMap = findViewById(R.id.btn_book_map);
        btnBookMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //closeBook();
                //dubin?
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra(Index.BOOK_ISBN,id);
                startActivity(intent);
            }
        });
        btnAddBorrow = findViewById(R.id.btn_Add_Borrow);
        //Toast.makeText(Product.this, "out", Toast.LENGTH_SHORT).show();
        btnAddBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product.this, chat.class);
                intent.putExtra("user1", userId);
                intent.putExtra("user2",userBookId );
                intent.putExtra("bookBorrowed", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query qBook = databaseBooks.child("books").child(id);
        qBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                Book book = bookSnapshot.getValue(Book.class);
                book.setImgUrl();
                tvProductName.setText(book.getName());
                tvProductAuthor.setText(book.getAuthor());
                tvProductGenre.setText(book.getGenre());
                rbProductRating.setRating(book.getRating());
                tvProductDescription.setText(book.getDescription());
                Log.d("book_cover_product", ""+book.getImgUrl()+", "+book.getName());
                Picasso.get().load(book.getImgUrl()).error(R.drawable.icon_book).into(imgProductImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnCloseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Index.class);
                startActivity(intent);
            }
        });
        ratingAction();
    }

    private void ratingAction()
    {
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer intRate= (int) rbProduct.getRating();
                databaseBooks.child("books").child(id).child("rating").child(userId).setValue(intRate.toString());
                Toast.makeText(Product.this, "Thank You For Rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToWishlist()
    {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        databaseBooks.child("users").child(userId).child("wishlist").child(id).setValue(currentDate);
    }

    private void closeBook()
    {
        Query qUser = databaseBooks.child("users");
        qUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                User user = null;
                LatLng userLatLng, MyLatLng;
                for (DataSnapshot userSnapshot  : usersSnapshot.getChildren())
                {
                  if (user == null)
                      user =  userSnapshot.getValue(User.class);

                  else{
                      userLatLng = userSnapshot.getValue(User.class).getLocation();
                      MyLatLng = new LatLng(currentLocation().getLatitude(),currentLocation.getLongitude()); // maybe currentlocation will reutrn null
                    //TODO: complete
                  }


                }
                User closestUser = user;
                user.getLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double getDistance(LatLng StartP, LatLng EndP) {
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
        return Radius * c;
    }

}