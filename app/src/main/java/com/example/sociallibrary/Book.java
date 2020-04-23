package com.example.sociallibrary;

import java.util.HashMap;

import static java.lang.Double.valueOf;

public class Book {

    private String id;
    private String name;
    private String author;
    private HashMap<String,String> rating;
    private String description;
    private String imgUrl;
    private String genre;

    public Book() {

    }

    public Book(String id,String name, String author,HashMap<String,String> rating, String description, String genre) {
        this.name = name;
        this.rating = rating;
        this.id = id;
        this.author = author;
        this.description = description;
        this.imgUrl = "http://covers.openlibrary.org/b/isbn/"+id+"-S.jpg";
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

    public float getRating() {
        float sum =0;
        float counter = 0;
        if (rating==null)
            return 0;
        for (HashMap.Entry<String, String> entry : rating.entrySet()) {
            sum+=Integer.parseInt(entry.getValue());
            counter++;
        }
        if (counter>0)
            return sum/counter;
        else
            return counter;
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

    public void setImgUrl() {
        this.imgUrl ="https://covers.openlibrary.org/b/isbn/"+id+"-M.jpg";
    }
}
