package com.example.socialmedia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.socialmedia.Adapter.CommentAdapter;
import com.example.socialmedia.Model.CommentModel;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ImageView postimage,userimage,back,likeimg,commentimg,shareimg,postbtn;
    TextView username,desc,time;
    EditText cmt;
    ShimmerRecyclerView commentrv;

    String postid;
    String postedBy;
    String user_name;
    CommentAdapter commentAdapter;

    ArrayList<CommentModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postimage = findViewById(R.id.postimage);
        userimage = findViewById(R.id.userimage);
        back = findViewById(R.id.back);
        commentrv = findViewById(R.id.commentrv);
        commentrv.showShimmerAdapter();
        cmt = findViewById(R.id.commenttext);
        time = findViewById(R.id.time);
        postbtn = findViewById(R.id.postcomment);
        username = findViewById(R.id.username);
        desc = findViewById(R.id.text);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        FirebaseDatabase.getInstance().getReference().child("Users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).into(userimage);
                user_name = user.getName().toString();
                Log.d("username", "onDataChange: "+user_name.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("posts").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel postModel = snapshot.getValue(PostModel.class);
                Picasso.get().load(postModel.getPostImage()).into(postimage);
                desc.setText(Html.fromHtml("<b>" + user_name+"</b>"+"    " + postModel.getPostDescription()));





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentModel cm = new CommentModel();
                cm.setCommentBody(cmt.getText().toString());
                cm.setCommentedBy(FirebaseAuth.getInstance().getUid());
                cm.setCommentedAt(new Date().getTime());


                FirebaseDatabase.getInstance().getReference().child("posts").child(postid).child("comments")
                        .push().setValue(cm).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cmt.setText("");
                                Log.d("time", "onSuccess: "+cm.getCommentedAt());
                                notifycomment();
                            }
                        });

            }
        });

        commentAdapter = new CommentAdapter(list,getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentrv.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("posts").child(postid).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CommentModel model = dataSnapshot.getValue(CommentModel.class);
                    model.setCommentid(snapshot.getKey());
                    list.add(model);
                }
                commentrv.setAdapter(commentAdapter);
                commentrv.hideShimmerAdapter();
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void notifycomment() {
        NotificationModel model = new NotificationModel();
        model.setNotificationBy(FirebaseAuth.getInstance().getUid());
        model.setNotificationType("comments");
        model.setPostedby(postedBy);
        model.setNotificationAt(new Date().getTime());

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(postedBy).push().setValue(model);
    }
}