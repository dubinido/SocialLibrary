package com.example.sociallibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;

import java.util.ArrayList;


public class chat extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_chat;
    ImageButton fab;
    String user1, user2, bookBorrowed, userName,userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        activity_chat = findViewById(R.id.activity_chat);

        //the keys to the path to the chat
        user1 = getIntent().getExtras().getString("user1");
        user2 = getIntent().getExtras().getString("user2");
        bookBorrowed = getIntent().getExtras().getString("bookBorrowed");

        DatabaseReference user1Ref = FirebaseDatabase.getInstance().getReference("users").child(user1);
        user1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(User.class).getUserName();
                userId = dataSnapshot.getValue(User.class).getId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("chat").child(user1 + "_" + user2 + "_" +bookBorrowed).push().setValue(
                        new ChatMessage(input.getText().toString(), userName,userId));
                input.setText("");
            }
        });

        //Load Content
        displayChatMessage();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void displayChatMessage() {
        ListView listOfMessage = findViewById(R.id.list_of_message);

        //Suppose you want to retrieve "chats" in your Firebase DB:
        Log.d("TAG", "displayChatMessage:"+ user1 + "_" + user2 + "_" +bookBorrowed);
        Query ref = FirebaseDatabase.getInstance().getReference("chat").child(user1 + "_" + user2 + "_" +bookBorrowed);
        //The error said the constructor expected FirebaseListOptions - here you create them:
        FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(ref, ChatMessage.class)
                        .setLayout(R.layout.list_item)
                        .build();
        Log.d("ido", "outside");
        adapter = new FirebaseListAdapter<ChatMessage>(options)
        {
            @Override
            protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
                // get references to the views of list_views.xml
                TextView messageText , messageUser, messageTime;
                RelativeLayout listItem;
                messageText = v.findViewById(R.id.message_text);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);

                listItem = v.findViewById(R.id.list_item);


                if (model.getMessageUserId().equals(user1))
                    listItem.setBackgroundColor(Color.GREEN);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));
                Log.d("ido", "inside");
            }
        };
        listOfMessage.setAdapter(adapter);

    }
}