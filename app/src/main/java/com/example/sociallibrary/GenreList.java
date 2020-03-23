package com.example.sociallibrary;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class GenreList extends ArrayAdapter<Genre> {

    private Activity context;
    private List<Genre> genres;

    public GenreList(Activity context, List<Genre> genres)
    {
        super(context, R.layout.genre_list_layout, genres);
        this.context=context;
        this.genres=genres;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        View listViewItem=inflater.inflate(R.layout.genre_list_layout,null,true);
        Button btnGenre = (Button) listViewItem.findViewById(R.id.btnGenre);

        Genre genre = genres.get(position);
        btnGenre.setText(genre.getGenreName());

        return listViewItem;

    }
}
