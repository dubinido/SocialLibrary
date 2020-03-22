package com.example.sociallibrary;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookList extends ArrayAdapter<Book> {

    private Activity context;
    private List<Book> books;

    public BookList(Activity context, List<Book> books)
    {
        super(context,R.layout.book_list_layout,books);
        this.context=context;
        this.books=books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        View listViewItem=inflater.inflate(R.layout.book_list_layout,null,true);
        TextView bookName=(TextView) listViewItem.findViewById(R.id.bookName);
        TextView bookAuthor=(TextView) listViewItem.findViewById(R.id.bookAuthor);
        TextView bookRating=(TextView) listViewItem.findViewById(R.id.bookRating);
        TextView bookGenre=(TextView) listViewItem.findViewById(R.id.bookGenre);
        ImageView bookImg=(ImageView) listViewItem.findViewById(R.id.bookImg);

        Book book = books.get(position);
        bookName.setText(book.getName());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        //bookRating.setText(book.getRating().toString());

        return listViewItem;

    }
}