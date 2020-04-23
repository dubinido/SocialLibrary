package com.example.sociallibrary;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Personal extends AppCompatActivity implements BookAdapter.OnBookListener{

    private GoogleSignInClient mGoogleSignInClient;
    Button btnMain, btnSignOut;
    TextView tvHelloUser;
    ImageView ivPersonImg;
    String userName,userImg, userId;

    static final Integer BOOK_LIMIT = 20;
    DatabaseReference databaseBooks;

    Query databaseUsers;
    User user;
    Set<String> wishlistIds;
    Set<String> booksIds;
    Set<String> borrowedIds;//maybe hashmap

    List<Book> wishlist;
    List<Book> books;
    List<Book> borrowed;

    RecyclerView rvWishlist, rvBooks,rvBorrowed;
    Button btnWishlist, btnBooks, btnBorrowed;
    TextView tvPersonalEmpty;

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
        userId = getUserId();

        rvBooks = findViewById(R.id.rvBooks);
        rvBorrowed = findViewById(R.id.rvBorrowed);
        rvWishlist = findViewById(R.id.rvWishlist);

        btnBooks = findViewById(R.id.btnBooks);
        btnBorrowed = findViewById(R.id.btnBorrowed);
        btnWishlist = findViewById(R.id.btnWislist);
        tvPersonalEmpty = findViewById(R.id.tvPersonalEmpty);

        tvHelloUser.setText("Hello " + userName);
        if (userImg=="")
            ivPersonImg.setImageResource(R.drawable.profile_pic);
        else
            Picasso.get().load(userImg).into(ivPersonImg);
        Log.d("user_img",userImg);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personal.this, com.example.sociallibrary.Index.class);
                startActivity(intent);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        databaseBooks = FirebaseDatabase.getInstance().getReference();

        books = new ArrayList<>();
        wishlist = new ArrayList<>();
        borrowed = new ArrayList<>();
        updateBooks2();

    }

    private void signOut() //sign out method
    {

         mGoogleSignInClient.signOut()
         .addOnCompleteListener(this, new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(Personal.this, "Singed Out successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Personal.this, com.example.sociallibrary.MainActivity.class);
        startActivity(intent);
        }
        });

    }

     private String getFirstName() // return the user first name
     {
         GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Personal.this);
         if (acct != null) {
             //String personName = acct.getDisplayName();
             String personGivenName = acct.getGivenName();
             return personGivenName;
         }
         return "";
     }

    private String getUserId() // return the user ID
    {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Personal.this);
        if (acct != null) {
            String personGivenId = acct.getId();
            return personGivenId;
        }
        return "";
    }
     private String getProfileImg() // return the user profile
     {
     GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Personal.this);
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

        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBooks.setVisibility(View.GONE);
                rvBorrowed.setVisibility(View.GONE);
                if (wishlist.isEmpty())
                {
                    rvWishlist.setVisibility(View.GONE);
                    tvPersonalEmpty.setVisibility(View.VISIBLE);

                }
                else
                {
                    rvWishlist.setVisibility(View.VISIBLE);
                    tvPersonalEmpty.setVisibility(View.GONE);
                }
                btnWishlist.setBackgroundResource(R.drawable.roundedbuttonpress);
                btnBooks.setBackgroundResource(R.drawable.roundedbutton);
                btnBorrowed.setBackgroundResource(R.drawable.roundedbutton);
            }
        });
        btnBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBorrowed.setVisibility(View.GONE);
                rvWishlist.setVisibility(View.GONE);
                if (books.isEmpty())
                {
                    rvBooks.setVisibility(View.GONE);
                    tvPersonalEmpty.setVisibility(View.VISIBLE);

                }
                else
                {
                    rvBooks.setVisibility(View.VISIBLE);
                    tvPersonalEmpty.setVisibility(View.GONE);
                }
                btnWishlist.setBackgroundResource(R.drawable.roundedbutton);
                btnBooks.setBackgroundResource(R.drawable.roundedbuttonpress);
                btnBorrowed.setBackgroundResource(R.drawable.roundedbutton);
            }
        });

        btnBorrowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBooks.setVisibility(View.GONE);
                rvWishlist.setVisibility(View.GONE);
                if (borrowed.isEmpty())
                {
                    rvBorrowed.setVisibility(View.GONE);
                    tvPersonalEmpty.setVisibility(View.VISIBLE);

                }
                else
                {
                    rvBorrowed.setVisibility(View.VISIBLE);
                    tvPersonalEmpty.setVisibility(View.GONE);
                }
                btnWishlist.setBackgroundResource(R.drawable.roundedbutton);
                btnBooks.setBackgroundResource(R.drawable.roundedbutton);
                btnBorrowed.setBackgroundResource(R.drawable.roundedbuttonpress);
            }
        });
    }
    public void updateBooks2() {
        Query createUser = databaseBooks.child("users").child(userId);

        createUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                updateBooks();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    // TODO: open up first look of list, if empty and color button
    private void updateBooks()
    {
        Query getBooks = databaseBooks.child("books");

        getBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (user==null) {
                    Toast.makeText(Personal.this, "no fucking user", Toast.LENGTH_LONG).show();
                    tvPersonalEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    wishlist.clear();
                    books.clear();
                    borrowed.clear();

                    wishlistIds = user.getWishlist().keySet();
                    booksIds = user.getBooks().keySet();
                    borrowedIds = user.getBorrowed().keySet();

                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        Book book = bookSnapshot.getValue(Book.class);
                        if (wishlistIds.contains(book.getId()))
                            wishlist.add(book);
                        if (booksIds.contains(book.getId()))
                            books.add(book);
                        if (borrowedIds.contains(book.getId()))
                            borrowed.add(book);
                    }

                    BookAdapter adapterWishlist = new BookAdapter(wishlist, Personal.this);
                    rvWishlist.setAdapter(adapterWishlist);
                    rvWishlist.setLayoutManager(new LinearLayoutManager(Personal.this));

                    BookAdapter adapterBooks = new BookAdapter(books, Personal.this);
                    rvBooks.setAdapter(adapterBooks);
                    rvBooks.setLayoutManager(new LinearLayoutManager(Personal.this));

                    BookAdapter adapterBorrowed = new BookAdapter(borrowed, Personal.this);
                    rvBorrowed.setAdapter(adapterBorrowed);
                    rvBorrowed.setLayoutManager(new LinearLayoutManager(Personal.this));

                    if (wishlist.isEmpty())
                    {
                        rvWishlist.setVisibility(View.GONE);
                        tvPersonalEmpty.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        rvWishlist.setVisibility(View.VISIBLE);
                        tvPersonalEmpty.setVisibility(View.GONE);
                    }
                    btnWishlist.setBackgroundResource(R.drawable.roundedbuttonpress);
                    btnBooks.setBackgroundResource(R.drawable.roundedbutton);
                    btnBorrowed.setBackgroundResource(R.drawable.roundedbutton);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBookClick(int position) {

    }


}
