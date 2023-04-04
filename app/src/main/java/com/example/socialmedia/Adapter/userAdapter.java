package com.example.socialmedia.Adapter;

import static com.example.socialmedia.R.color.black;
import static com.example.socialmedia.R.color.grey;
import static com.example.socialmedia.R.color.white;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Model.Follow;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class userAdapter  extends  RecyclerView.Adapter<userAdapter.holder>{
    Context context;
    ArrayList<User> list;

    private FirebaseUser firebaseUser;


    public userAdapter(Context context, ArrayList<User> list) {
        this.list = list;
        this.context = context;



    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_sample,parent,false);
        return new holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
//        User user = list.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = list.get(position);
        Follow follow = new Follow();
        follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
        follow.setFollowedAt(new Date().getTime());
//
//        holder.img.setImageResource(dataholder.get(position).getImage());
//        holder.username.setText(dataholder.get(position).getHeader());




        Glide.with(context).load(user.getProfilePhoto()).placeholder(R.drawable.user_icon1).into(holder.userprofile);
        holder.username.setText(list.get(position).getName());
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("Users").child(user.getUserID()).child("followers")
                        .child(FirebaseAuth.getInstance().getUid());




        holder.followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Follow follow = new Follow();
                try{
                    if (holder.followbtn.getText().toString().equals("follow")){

                        ref.setValue(follow)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        ref.child(FirebaseAuth.getInstance().getUid()).child("Following").child(user.getUserID()).setValue(true);
                                        notificationfollow(user.getUserID());
                                    }
                                });
                    } else {
                          ref.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        ref.child(FirebaseAuth.getInstance().getUid()).child("Following").child(user.getUserID()).removeValue();
                                    }
                                });
                    }

                }catch (Exception e){

                }

            }
        });


        isFollowed(user.getUserID() , holder.followbtn);







//        Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(holder.userprofile);

    }

    private void isFollowed(String userID, Button button) {
        //    private void isFollowed(String id, Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).exists()) {
                    button.setText("following");
                    button.setBackground(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
//                    followbtn.setText("");
                    button.setTextColor(ContextCompat.getColor(context, black));

                } else {
                    button.setText("follow");
                    button.setBackground(ContextCompat.getDrawable(context, R.drawable.follow_unactive_btn));
//                    followbtn.setText("");
                    button.setTextColor(ContextCompat.getColor(context, white));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void notificationfollow(String userID) {
        NotificationModel model = new NotificationModel();

        model.setNotificationAt(new Date().getTime());
        model.setNotificationType("following");

        model.setPostid(model.getPostid());
        model.setNotificationBy(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference().child("Notification").child(userID).push()
                .setValue(model);
    }

////        isFollowed(user.getUserID() , holder.followbtn);

//
//
//
//    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder{
        ImageView userprofile;
        TextView username;
        Button followbtn;

        public holder(@NonNull View itemView) {
            super(itemView);

            followbtn = itemView.findViewById(R.id.button2);
            username = itemView.findViewById(R.id.username);
            userprofile = itemView.findViewById(R.id.profileimg);
        }
    }
}
