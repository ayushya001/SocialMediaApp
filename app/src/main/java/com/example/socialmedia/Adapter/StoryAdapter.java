package com.example.socialmedia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Activity.AddStoryActivity;
import com.example.socialmedia.Activity.StoryActivity;
import com.example.socialmedia.Model.StoryModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    Context mcontext;
    ArrayList<StoryModel> mstory;

    public StoryAdapter(Context mcontext, ArrayList<StoryModel> mstory) {
        this.mcontext = mcontext;
        this.mstory = mstory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0){
            View view = LayoutInflater.from(mcontext)
                    .inflate(R.layout.add_story_items,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mcontext)
                    .inflate(R.layout.story_items,parent,false);
            return new StoryAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        StoryModel story = mstory.get(i);
       userInfo(holder,story.getSuserid(),i);

        if (holder.getAdapterPosition() != 0) {
            seenStory(holder, story.getSuserid());
        }

        if (holder.getAdapterPosition() == 0){
            myStory(holder.storyAddText, holder.storyAdd, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() == 0){
                    myStory(holder.storyAddText, holder.storyAdd, true);
                } else {
                    // TODO: go to story
                    Intent intent = new Intent(mcontext, StoryActivity.class);
                    intent.putExtra("userid", story.getSuserid());
                    mcontext.startActivity(intent);
                }
            }
        });

    }







    @Override
    public int getItemCount() {
        return mstory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView storyPhoto,storyAdd,storyPhotoSeen;
        TextView storyUsername,storyAddText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyPhoto = itemView.findViewById(R.id.addStoryItems_storyPhoto);
            storyAdd = itemView.findViewById(R.id.addStoryItems_addstory);
            storyPhotoSeen = itemView.findViewById(R.id.storyItems_storyPhotoSeen);
            storyUsername = itemView.findViewById(R.id.storyItems_storyUsername);
            storyAddText = itemView.findViewById(R.id.addStoryItems_addstorytxt);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position ==0){
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, String userid, final int pos){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
//                Glide.with(mcontext).load(user.getProfilePhoto()).into(viewHolder.storyPhoto);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(viewHolder.storyPhoto);
                if (pos != 0) {
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(viewHolder.storyPhoto);
                    viewHolder.storyUsername.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    StoryModel story = snapshot.getValue(StoryModel.class);
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                        count++;
                    }
                }

                if (click) {
                    if (count > 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: go to story
                                        Intent intent = new Intent(mcontext, StoryActivity.class);
                                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mcontext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mcontext, AddStoryActivity.class);
                                        mcontext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(mcontext, AddStoryActivity.class);
                        mcontext.startActivity(intent);
                    }
                } else {
                    if (count > 0){
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() &&
                            System.currentTimeMillis() < snapshot.getValue(StoryModel.class).getTimeend()){
                        i++;
                    }
                }

                if ( i > 0){
                    viewHolder.storyPhoto.setVisibility(View.VISIBLE);
                    viewHolder.storyPhotoSeen.setVisibility(View.GONE);
                } else {
                    viewHolder.storyPhoto.setVisibility(View.GONE);
                    viewHolder.storyPhotoSeen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
