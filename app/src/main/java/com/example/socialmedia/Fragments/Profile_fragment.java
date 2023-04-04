package com.example.socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Activity.Editprofile;
import com.example.socialmedia.Activity.OptionsActivity;
import com.example.socialmedia.Activity.Suggestion;
import com.example.socialmedia.Adapter.FollowerAdapter;
import com.example.socialmedia.Adapter.PhotoAdapter;
import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;


public class Profile_fragment extends Fragment {
    RecyclerView rv;
    ArrayList<PostModel> list;
    PhotoAdapter photoAdapter;
    FollowerAdapter followerAdapter;
    ImageView profileig,addphoto,option;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    FirebaseUser firebaseUser;
    Button editprofile;
    LinearLayout midbar;
    TextView username,followers,numposts,numfollowing,bio;
    String data;
    String profileid;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        data = prefs.getString("profileid" , "none");
        SharedPreferences.Editor editor = prefs.edit();

        Log.d("ayushya", "onCreateView: sharedpref"+data);




        rv = view.findViewById(R.id.recycler_view);
        mAuth =FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        username = view.findViewById(R.id.username);
        numposts = view.findViewById(R.id.posts);
        midbar = view.findViewById(R.id.mid_bar);
        bio = view.findViewById(R.id.bio);

        option = view.findViewById(R.id.options);
        numfollowing = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        editprofile= view.findViewById(R.id.edit_profile);

        profileig = view.findViewById(R.id.image_profile);
//        addphoto = view.findViewById(R.id.addphoto);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


//        profileid  =firebaseUser.getUid();



        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext() , 3);
        rv.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext() , list);
        rv.setAdapter(photoAdapter);


        if (data.equals("none")|| data.equals(firebaseUser.getUid())){
            profileid = firebaseUser.getUid();
            Log.d("ayushya", "onCreateView: current"+profileid.toString());
            editprofile.setText("Edit Profile");
        } else {
//            Log.d("ayushya", "onCreateView: "+profileid.toString());
            profileid = data;
            Log.d("ayushya", "onCreateView: shared"+data);
            Log.d("ayushya", "onCreateView: "+" sharedvalue hi aarha hai ");


            checkFollow();

        }

        editor.remove("profileid");
        editor.commit();

        myphotos();
        getpostcount();
        nofollowing();
        nofollowers();
        userinfo();

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getContext(), Editprofile.class);
                startActivity(intent);

            }
        });
//        option.bringToFront();
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent =  new Intent(getContext(),OptionsActivity.class);
               startActivity(intent);
            }
        });







//        option.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext() , Suggestion.class);
//                startActivity(intent);
//
//            }
//        });
        return view;
    }
    private void checkFollow() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileid).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    editprofile.setText("following");
                }else{
                    editprofile.setText("follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nofollowers() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileid).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nofollowing() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numfollowing.setText(snapshot.getChildrenCount()+"");
                Log.d("ayush", "onDataChange: ");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getpostcount() {

        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostModel post = snapshot1.getValue(PostModel.class);
                    if (post.getPostedBy().equals(profileid)){
                        i++;
                    }
                }
                numposts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userinfo() {
        db.getReference().child("Users").child(profileid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            username.setText(user.getName());

                            Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(profileig);

                            db.getReference().child("Users").child(mAuth.getUid()).child("bio").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        midbar.setVisibility(View.VISIBLE);
                                        bio.setText(user.getBio());

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });





                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void myphotos() {
//        list = new ArrayList<>();
//        photoAdapter = new PhotoAdapter(getContext(), list);
//        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
//        myfriendrv.setLayoutManager(layoutManager3);
//        myfriendrv.setNestedScrollingEnabled(false);
//        myfriendrv.setAdapter(photoAdapter);

        db.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    if (postModel.getPostedBy().equals(profileid)){
                        list.add(postModel);
                    }
                }
                Collections.reverse(list);
                photoAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}