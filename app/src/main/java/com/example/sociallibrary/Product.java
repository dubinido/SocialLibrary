package com.example.sociallibrary;

import android.content.Intent;
import android.os.Bundle;
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
    TextView tvProductRate;
    TextView tvProductGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        Intent intent = getIntent();

        String id= intent.getStringExtra(Index.BOOK_ID);

        databaseBooks = FirebaseDatabase.getInstance().getReference("books").child(id);

        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductAuthor = (TextView) findViewById(R.id.tvProductAuthor);
        tvProductRate = (TextView) findViewById(R.id.tvProductRate);
        tvProductGenre = (TextView) findViewById(R.id.tvProductGenre);
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
                tvProductRate.setText(book.getRating().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
