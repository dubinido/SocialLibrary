package com.example.sociallibrary;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
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
    private User user;

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
        this.user=null;
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public double getDistance(com.google.android.gms.maps.model.LatLng StartP, LatLng EndP) {
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
        tvUserDistance.setText(new DecimalFormat("##.#").format(Radius*c)+" Km");
    }

    public double getGrade()
    {
        double grade = this.getRating()+(5/this.getDistance());
    }


    public double comapare(Book book)
    {
        double grade1=this.getGrade();
        double grade2=book.getGrade();
        return grade1-grade2;
    }

}
