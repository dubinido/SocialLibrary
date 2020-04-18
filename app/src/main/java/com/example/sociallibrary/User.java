package com.example.sociallibrary;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class User {
    String imgUrl;
    String userName;
    double lat, lng;
    HashMap<String, String> books;
    HashMap<String, String> wishlist;
    HashMap<String, String> borrowed;

    public User() {
    }

    public User(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public User(String userName,String imgUrl, double lat, double lng, HashMap<String, String> books, HashMap<String, String> wishlist, HashMap<String, String> borrowed) {
        this.userName = userName;
        this.imgUrl=imgUrl;
        this.lat = lat;
        this.lng = lng;
        this.books = books;
        this.wishlist = wishlist;
        this.borrowed = borrowed;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LatLng getLocation(){
        LatLng location = new LatLng(this.lat, this.lng);
        return location;
    }

    public HashMap<String, String> getBooks() {
        return books;
    }

    public HashMap<String, String> getWishlist() {
        return wishlist;
    }

    public HashMap<String, String> getBorrowed() {
        return borrowed;
    }
}