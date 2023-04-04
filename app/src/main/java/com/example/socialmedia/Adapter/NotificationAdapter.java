package com.example.socialmedia.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Activity.CommentActivity;
import com.example.socialmedia.Activity.MainActivity;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.holder> {

    Context context;
    ArrayList<NotificationModel> list4;
    String name;



    public NotificationAdapter(ArrayList<NotificationModel> list4, Context context) {
        this.context = context;
        this.list4 = list4;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_design,parent,false);
        return new holder(view);

    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {



         NotificationModel temp = list4.get(position);
//        name = FirebaseDatabase.getInstance().getReference().child("Users").child(temp.getNotificationBy()).addListenerForSingleValueEvent();
        FirebaseDatabase.getInstance().getReference().child("Users").child(temp.getNotificationBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(holder.frndimg);


                if (temp.getNotificationType().equals("follow")){
                    holder.notification.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" "+"start follow ing you"));

                }else if(temp.getNotificationType().equals("comments")){
                    holder.notification.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" "+"Commented on your post"));
                }else {
                    holder.notification.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" "+"Liked your post"));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String text = TimeAgo.using(temp.getNotificationAt());
        holder.time.setText(text);

        holder.notificationstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (temp.getNotificationType().equals("follow")) {

                    Intent intent = new Intent(context , MainActivity.class);
                    intent.putExtra("publisherid" ,temp.getNotificationBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getUid())
                            .child(temp.getNotificationid()).child("seen").setValue(true);

                }else {
                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getUid())
                            .child(temp.getNotificationid()).child("seen").setValue(true);

                }
            }
        });
        Boolean check  = temp.isSeen();
        if (check == true){
            holder.notificationstatus.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        }
    }



    @Override
    public int getItemCount() {
        return list4.size();
    }

    public class  holder extends RecyclerView.ViewHolder{
        ImageView frndimg;
        TextView time,notification;
        ConstraintLayout notificationstatus;

        public holder(@NonNull View itemView) {
            super(itemView);
            frndimg = itemView.findViewById(R.id.friend_img);
            notification = itemView.findViewById(R.id.notification);
            time = itemView.findViewById(R.id.time);
            notificationstatus = itemView.findViewById(R.id.notificationstatus);
        }
    }
}
