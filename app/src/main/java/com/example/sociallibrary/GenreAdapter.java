package com.example.sociallibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder>
{

    public int row_index;
    private List<Genre> genres;
    private OnGenreListener  mOnGenreListener;

    //constructor
    public GenreAdapter(List<Genre> mGenres,OnGenreListener onGenreListener) {
        this.genres = mGenres;
        this.mOnGenreListener=onGenreListener;
        row_index = 0;

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView btnGenre;
        OnGenreListener onGenreListener;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnGenreListener onGenreListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            btnGenre = (TextView) itemView.findViewById(R.id.btnGenre);
            this.onGenreListener=onGenreListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onGenreListener.onGenreClick(getAdapterPosition());
            row_index = getAdapterPosition();
            notifyDataSetChanged();
        }
    }


    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View genreView = inflater.inflate(R.layout.genre_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(genreView,mOnGenreListener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(GenreAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Genre genre = genres.get(position);

        TextView btnGenre = viewHolder.btnGenre;
        btnGenre.setText(genre.getGenreName());
        if (row_index==position)
            btnGenre.setBackgroundResource(R.drawable.roundedbuttonpress);
        else
            btnGenre.setBackgroundResource(R.drawable.roundedbutton);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return genres.size();
    }

    public interface OnGenreListener{
        void onGenreClick(int position);
    }
}