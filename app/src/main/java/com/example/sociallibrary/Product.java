package com.example.sociallibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Product extends AppCompatActivity {
    Book book;
    DatabaseReference databaseBooks;

    TextView tvProductId;
    TextView tvProductName;
    TextView tvProductAuthor;
    RatingBar tvProductRate;
    TextView tvProductGenre;
    Button btnCloseProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        Intent intent = getIntent();

        String id= intent.getStringExtra(Index.BOOK_ID);

        databaseBooks = FirebaseDatabase.getInstance().getReference("books").child(id);

        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductAuthor = (TextView) findViewById(R.id.tvProductAuthor);
        tvProductRate = (RatingBar) findViewById(R.id.tvProductRate);
        tvProductGenre = (TextView) findViewById(R.id.tvProductGenre);
        btnCloseProduct = (Button) findViewById(R.id.btnCloseProduct);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                Book book = bookSnapshot.getValue(Book.class);
                tvProductName.setText(book.getName());
                tvProductAuthor.setText(book.getAuthor());
                tvProductGenre.setText(book.getGenre());
                tvProductRate.setNumStars((int) Math.round(book.getRating()));

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
    }
}