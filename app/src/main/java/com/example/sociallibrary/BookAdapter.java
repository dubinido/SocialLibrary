package com.example.sociallibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>
{
    private List<Book> books;
    private OnBookListener mOnBookListener;

    //constructor
    public BookAdapter(List<Book> mbooks,OnBookListener onBookListener) {
        this.books = mbooks;
        this.mOnBookListener=onBookListener;

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView bookName;
        TextView bookAuthor;
        TextView bookDistance;
        RatingBar bookRating;
        TextView bookGenre;
        ImageView bookImg;
        OnBookListener onBookListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView,OnBookListener onBookListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            bookName = (TextView) itemView.findViewById(R.id.bookName);
            bookAuthor=(TextView) itemView.findViewById(R.id.bookAuthor);
            bookRating=(RatingBar) itemView.findViewById(R.id.bookRating);
            bookGenre=(TextView) itemView.findViewById(R.id.bookGenre);
            bookImg=(ImageView) itemView.findViewById(R.id.bookImg);
            bookDistance=(TextView) itemView.findViewById(R.id.bookDistance);

            this.onBookListener=onBookListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookListener.onBookClick(getAdapterPosition());
        }
    }

    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.book_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bookView, mOnBookListener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder viewHolder, int position) {
        final long ONE_MEGABYTE = 1024 * 1024;

        // Get the data model based on position
        Book book = books.get(position);
        book.setImgUrl();
        TextView bookName=viewHolder.bookName;
        bookName.setText(book.getName());
        TextView bookAuthor=viewHolder.bookAuthor;
        bookAuthor.setText(book.getAuthor());
        RatingBar bookRating=viewHolder.bookRating;
        bookRating.setRating(book.getRating());
        TextView bookGenre=viewHolder.bookGenre;
        bookGenre.setText(book.getGenre());
        ImageView bookImg=viewHolder.bookImg;
        TextView bookDistance=viewHolder.bookDistance;
        //bookDistance.setText(book.getDistance()+" km away); todo: dubin add here the the user location

        Log.d("book_cover", ""+book.getImgUrl());
        Picasso.get().load(book.getImgUrl()).placeholder(R.drawable.icon_book).error(R.drawable.icon_book).into(bookImg);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return books.size();
    }

    public interface OnBookListener{
        void onBookClick(int position);
    }
}

