package com.example.sociallibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder>
{

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        Button btnGenre;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            btnGenre = (Button) itemView.findViewById(R.id.btnGenre);
        }

    }

    private List<Genre> genres;

    // Pass in the contact array into the constructor
    public GenreAdapter(List<Genre> mGenres) {
        genres = mGenres;
    }

    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View genreView = inflater.inflate(R.layout.genre_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(genreView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(GenreAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Genre genre = genres.get(position);

        Button btnGenre = viewHolder.btnGenre;
        btnGenre.setText(genre.getGenreName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return genres.size();
    }
}

