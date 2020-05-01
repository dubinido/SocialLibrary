package com.example.sociallibrary;

import java.util.ArrayList;

public class ChatCon {
    private String user1,user2,isbn,chatId;
    boolean borrow;
    private long time;

    public ChatCon(String user1, String user2, String isbn,long time, String chatId,boolean borrow) {
        this.user1 = user1;
        this.user2 = user2;
        this.isbn = isbn;
        this.time = time;
        this.chatId = chatId;
        this.borrow = borrow;
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
}
