package com.example.socialmedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Activity.CommentActivity;
import com.example.socialmedia.Fragments.Profile_fragment;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.holder> {

    ArrayList<PostModel> list;
    Context context;


    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

//    public PostAdapter(String username, String photouri) {
//        this.username = username;
//        this.photouri = photouri;
//    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postrv_design, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        PostModel model = list.get(position);


        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.ic_add_image).into(holder.postimg);

        String text = TimeAgo.using(model.getPostedAt());
        Log.d("ayush", "onBindViewHolder: " + model.getPostImage());
        holder.time.setText(text);
        Log.d("ayush", "onBindViewHolder: " + model.getPostImage());
        holder.desc.setText(model.getPostDescription());
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", model.getPostedBy());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fm,
                        new Profile_fragment()).addToBackStack(null).commit();

            }
        });
        holder.userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", model.getPostedBy());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fm,
                        new Profile_fragment()).addToBackStack(null).commit();

            }
        });
        holder.commentimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostid());
                intent.putExtra("postedBy", model.getPostedBy());

                context.startActivity(intent);
            }
        });


        String descc = model.getPostDescription();
        if (descc.equals("")) {
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setText(model.getPostDescription());
            holder.desc.setVisibility(View.VISIBLE);
        }
//        isLikes(model.getPostid(), holder.likeimg);
        noLikes(holder.like, model.getPostid());
        nocomments(holder.comment, model.getPostid());

        FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostid()).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.like.setText(snapshot.getChildrenCount() + "");
                if (snapshot.child(FirebaseAuth.getInstance().getUid()).exists()) {
                    holder.likeimg.setImageResource(R.drawable.ic_heartred);
                    holder.likeimg.setTag("liked");
                } else {
                    holder.likeimg.setImageResource(R.drawable.ic_heartsvg);
                    holder.likeimg.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                PostModel model1 = snapshot.getValue(PostModel.class);
//                        Picasso.get().load(model1.getPostImage()).placeholder(R.drawable.ic_add_image).into(holder.postimg);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getPostedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(holder.userimage);
                        holder.username.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.likeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeimg.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("posts")
                            .child(model.getPostid()).child("likes")
                            .child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    notifylike(model.getPostid(), model.getPostedBy());

                                }
                            });
//                    holder.likeimg.setImageResource(R.drawable.ic_heartred);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("posts")
                            .child(model.getPostid()).child("likes")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
//                    holder.likeimg.setImageResource(R.drawable.ic_heartsvg);
                }
            }
        });


    }

    private void notifylike(String postid, String postedBy) {
        NotificationModel model = new NotificationModel();
        model.setNotificationBy(FirebaseAuth.getInstance().getUid());
        model.setNotificationType("likes");
        model.setNotificationAt(new Date().getTime());
        model.setPostedby(postedBy);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(postedBy).push().setValue(model);
    }


    private void nocomments(TextView comment, String postid) {
        FirebaseDatabase.getInstance().getReference().child("posts").child(postid)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comment.setText(snapshot.getChildrenCount() + "");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void noLikes(TextView like, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postid).child("likes");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                like.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder {

        ImageView userimage, postimg, likeimg, commentimg, shareimg;
        TextView like, comment, share, username, time, desc;

        public holder(@NonNull View itemView) {
            super(itemView);
            userimage = itemView.findViewById(R.id.userimage);
            postimg = itemView.findViewById(R.id.postimg);
            likeimg = itemView.findViewById(R.id.likeimg);
            commentimg = itemView.findViewById(R.id.commentimg);
            shareimg = itemView.findViewById(R.id.shareimg);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            desc = itemView.findViewById(R.id.desc);
        }
    }


}
