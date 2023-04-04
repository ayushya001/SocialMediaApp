package com.example.socialmedia.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Model.Follow;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.holder> {


    ArrayList<Follow> list1;
    Context context;

    public FollowerAdapter(ArrayList<Follow> list1, Context context) {
        this.list1 = list1;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfriend_design,parent,false);
        return new FollowerAdapter.holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
         Follow follow = list1.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.user_icon2)
                                .into(holder.followers);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







    }

    @Override
    public int getItemCount() {
        return list1.size();
    }

    public class holder extends RecyclerView.ViewHolder{
        ImageView followers;

        public holder(@NonNull View itemView) {
            super(itemView);
            followers = itemView.findViewById(R.id.followers);
        }
    }
}
