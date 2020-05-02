package com.example.sociallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;



public class ScanActivity extends AppCompatActivity {

    CameraView camera_view;
    boolean isDetected = false;
    Button btn_start_again;
    GoogleSignInAccount acct;
    String bookId;
    DatabaseReference dbRef;
    String userId;

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        acct = GoogleSignIn.getLastSignedInAccount(this); //this is how to save account of google in USERS table
        userId=acct.getId();
        dbRef = FirebaseDatabase.getInstance().getReference();
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        setupCamera();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }
    private void setupCamera()
    {
        btn_start_again = findViewById(R.id.btn_again);
        btn_start_again.setEnabled(isDetected);
        btn_start_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
                btn_start_again.setEnabled(isDetected);
            }
        });
        camera_view = findViewById(R.id.cameraView);
        camera_view.setLifecycleOwner(this);
        camera_view.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                    processImage(getVisionImageFromFrame(frame));
            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS).build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    private void processImage(final FirebaseVisionImage image) {
        if(!isDetected)
        {
            //Toast.makeText(this, "image size: "+ image.get, Toast.LENGTH_SHORT).show();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                @Override
                public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                    if(!isDetected)
                        processResult(firebaseVisionBarcodes);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ScanActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

   private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
       if (firebaseVisionBarcodes.size() > 0) {
           isDetected = true;
           btn_start_again.setEnabled(isDetected);

           for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {

               String id = item.getRawValue();
               bookId=id;
               Toast.makeText(this,bookId,Toast.LENGTH_SHORT).show();
               showDialog();
               //Intent intent=new Intent(getApplicationContext(),AddBook.class);
               //intent.putExtra("isbn",bookId);
               //startActivity(intent);

               break;

           }
       }
   }

    private void createDialog(String text) {
        AlertDialog. Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21 )
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
               // .setRotation(frame.getRotation())
        .build();
        return FirebaseVisionImage.fromByteArray(data,metadata);
    }

    private void showDialog()
    {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.product_dialog_box, null);


        Button btnAddYes = (Button) dialogView.findViewById(R.id.btnAddYes);
        Button btnAddNo = (Button) dialogView.findViewById(R.id.btnAddNo);
        final TextView tvdName = (TextView) dialogView.findViewById(R.id.tvdName);
        final TextView tvdAuthor = (TextView) dialogView.findViewById(R.id.tvdAuthor);
        final TextView tvdGenre = (TextView) dialogView.findViewById(R.id.tvdGenre);
        final ImageView tvdImg = (ImageView) dialogView.findViewById(R.id.tvdImg);
        Query getBook = dbRef.child("books").child(bookId);
        getBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                tvdName.setText(book.getName());
                tvdAuthor.setText(book.getAuthor());
                tvdGenre.setText(book.getGenre());
                Picasso.get().load(book.getImgUrl()).placeholder(R.drawable.icon_book).error(R.drawable.icon_book).into(tvdImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnAddNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBuilder.dismiss();
            }
        });
        btnAddYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                dbRef.child("users").child(userId).child("books").child(bookId).setValue(currentDate);
                Toast.makeText(ScanActivity.this,"You added the book!",Toast.LENGTH_LONG).show();
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
