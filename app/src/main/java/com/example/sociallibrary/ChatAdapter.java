package com.example.sociallibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    private List<ChatCon> chatCons;
    private OnChatListener mOnChatListener;

    //constructor
    public ChatAdapter(List<ChatCon> mChatCons,OnChatListener onChatListener) {
        this.chatCons = mChatCons;
        this.mOnChatListener = onChatListener;

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView user1,user2;
        OnChatListener onChatListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView,OnChatListener onChatListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            user1 = (TextView) itemView.findViewById(R.id.chatUser1);
            user2 = (TextView) itemView.findViewById(R.id.chatUser2);
            this.onChatListener = onChatListener;

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            onChatListener.onChatClick(getAdapterPosition());
        }
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View chatView = inflater.inflate(R.layout.chat_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(chatView, mOnChatListener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder viewHolder, int position) {
        final long ONE_MEGABYTE = 1024 * 1024;

        // Get the data model based on position
        ChatCon chatCon = chatCons.get(position);
        TextView user1 = viewHolder.user1;
        user1.setText(chatCon.getUser1());
        TextView user2 = viewHolder.user2;
        user2.setText(chatCon.getUser2());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return chatCons.size();
    }

    public interface OnChatListener{
        void onChatClick(int position);
    }
}

