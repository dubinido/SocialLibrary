package com.example.sociallibrary;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatCon {
    private String user1,user2,isbn,chatId;
    boolean borrow;
    private long time;
    private String name1,name2;

    public ChatCon(String user1, String user2, String isbn,long time, String chatId,boolean borrow,String name1,String name2) {
        this.user1 = user1;
        this.user2 = user2;
        this.isbn = isbn;
        this.time = time;
        this.chatId = chatId;
        this.borrow = borrow;
        this.name1=name1;
        this.name2 = name2;
    }

    public ChatCon() {
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setBorrow(boolean borrow) {
        this.borrow = borrow;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public String getIsbn() {
        return isbn;
    }

    public long getTime() {
        return time;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isBorrow() {
        return borrow;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public String getUrl()
    {
        String url = "http://covers.openlibrary.org/b/isbn/"+isbn+"-S.jpg";
        return url;
    }
}
