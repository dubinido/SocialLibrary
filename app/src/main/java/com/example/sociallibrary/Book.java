package com.example.sociallibrary;

import com.google.android.gms.maps.model.LatLng;

public class Book {

    private String id;
    private String name;
    private String author;
    private Double rating;
    private String description;
    private String imgUrl;
    private String genre;

    public Book() {

    }

    public Book(String id,String name, String author, Double rating, String description, String imgUrl, String genre) {
        this.name = name;
        this.id = id;
        this.author = author;
        this.rating = rating;
        this.description = description;
        this.imgUrl = imgUrl;
        this.genre=genre;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getGenre() {
        return genre;
    }
}