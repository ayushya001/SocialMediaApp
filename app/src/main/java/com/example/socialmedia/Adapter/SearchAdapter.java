package com.example.socialmedia.Adapter;

import static com.example.socialmedia.R.color.black;
import static com.example.socialmedia.R.color.white;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Activity.MainActivity;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
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
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.holder> {
    ArrayList<User> mlist;
    Context context;
    private FirebaseUser firebaseUser;

    public SearchAdapter(ArrayList<User> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_sample,parent,false);
        return new SearchAdapter.holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mlist.get(position);
//        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getName());

        Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(holder.userprofile);
        isFollowed(user.getId() , holder.followbtn);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.followbtn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , MainActivity.class);
                intent.putExtra("publisherid" , user.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followbtn.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    private void addNotifications(String userid) {
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setNotificationAt(new Date().getTime());
        notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
        notificationModel.setNotificationType("follow");
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid).push().setValue(notificationModel);


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        ImageView userprofile;
        TextView username;
        Button followbtn;
        public holder(@NonNull View itemView) {
            super(itemView);
            followbtn = itemView.findViewById(R.id.button2);
            username = itemView.findViewById(R.id.username);
            userprofile = itemView.findViewById(R.id.profileimg);
        }
    } private void isFollowed (final String userid , final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                    button.setBackground(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
//                    followbtn.setText("");
                    button.setTextColor(ContextCompat.getColor(context, black));

                } else {
                    button.setText("follow");
                    button.setBackground(ContextCompat.getDrawable(context,R.drawable.follow_unactive_btn));
//                    followbtn.setText("");
                    button.setTextColor(ContextCompat.getColor(context, white));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
