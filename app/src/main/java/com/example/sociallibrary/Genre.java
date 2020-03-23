package com.example.sociallibrary;

public class Genre {

    private String genreId;
    private String genreName;

    public Genre() {
    }

    public Genre(String genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public String getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }
}
