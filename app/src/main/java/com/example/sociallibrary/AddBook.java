package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddBook extends AppCompatActivity {

    EditText addName,addIsbn,addAuthor,addDesc;
    Spinner addSpinner;
    DatabaseReference dbRef;
    List<Genre> genreList;
    String[] genres;
    String addGenre;
    Button addSubmit, addReturn;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        addName=(EditText) findViewById(R.id.addName);
        addAuthor=(EditText) findViewById(R.id.addAuthor);
        addDesc=(EditText) findViewById(R.id.addDesc);
        addIsbn=(EditText) findViewById(R.id.addIsbn);
        addSpinner=(Spinner)findViewById(R.id.addSpinner);
        addSubmit=(Button) findViewById(R.id.addSubmit);
        addReturn=(Button) findViewById(R.id.addReturn);

        dbRef= FirebaseDatabase.getInstance().getReference();
        genreList=new ArrayList<>();

        addSpinner.setSelection(0, true);
        View v = addSpinner.getSelectedView();

        setGenreSpinner();
        addReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Index.class);
                startActivity(intent);
            }
        });

        addSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=addIsbn.getText().toString();
                dbRef.child("books").child(id).child("name").setValue(addName.getText().toString());
                dbRef.child("books").child(id).child("id").setValue(addIsbn.getText().toString());
                dbRef.child("books").child(id).child("author").setValue(addAuthor.getText().toString());
                dbRef.child("books").child(id).child("description").setValue(addDesc.getText().toString());
                dbRef.child("books").child(id).child("genre").setValue(addGenre);
                Toast.makeText(AddBook.this, addName.getText().toString()+" has been added", Toast.LENGTH_SHORT).show();

                addDesc.setText("");
                addAuthor.setText("");
                addIsbn.setText("");
                addName.setText("");
            }
        });
    }

    private void setGenreSpinner(){
        Query qGenres = dbRef.child("genres");
        qGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                genreList.clear();
                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren())
                {
                    Genre genre = genreSnapshot.getValue(Genre.class);
                    genreList.add(genre);
                }
                genres=new String[genreList.size()];
                for (int counter = 0; counter < genreList.size(); counter++) {
                    genres[counter]=genreList.get(counter).getGenreName();
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddBook.this,android.R.layout.simple_spinner_item,genres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addSpinner.setAdapter(adapter);
                addSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        addGenre=String.valueOf(parent.getItemAtPosition(position));
                    }
                    @Override
                    public void onNothingSelected(AdapterView <?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
