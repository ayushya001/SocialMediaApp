package com.example.socialmedia.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.socialmedia.Adapter.PostAdapter;
import com.example.socialmedia.Adapter.StoryAdapter;
import com.example.socialmedia.Model.PostModel;
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
import java.util.Collections;
import java.util.List;

public class Home extends Fragment {
    ShimmerRecyclerView storyrv;
    ShimmerRecyclerView postrv;
    ConstraintLayout homeconstraint;
    PostAdapter postAdapter;
    StoryAdapter storyAdapter;
    List<String> followingList;
    ImageView imageView;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<PostModel> list;
    ArrayList<StoryModel> mstory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView = view.findViewById(R.id.imageView);
        homeconstraint= view.findViewById(R.id.homeConstraint);

        db.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        postrv = view.findViewById(R.id.postrv);
        postrv.showShimmerAdapter();
        storyrv = view.findViewById(R.id.srv);
        storyrv.showShimmerAdapter();


        checkFollowing();




        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                followingList.add(FirebaseAuth.getInstance().getUid());
                Log.d("ayushya", "onDataChange: "+followingList.toString());
                readPosts();
                readStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readStory() {
        mstory = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), mstory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);

//        layoutManager.setStackFromEnd(true);
        storyrv.setLayoutManager(layoutManager);
        storyrv.setNestedScrollingEnabled(false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                mstory.clear();
                mstory.add(new StoryModel("", "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid(), 0,0));
                for (String id : followingList) {
                    int countStory = 0;
                    StoryModel story = null;
                    if(id != FirebaseAuth.getInstance().getCurrentUser().getUid()){

                        for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                            story = snapshot.getValue(StoryModel.class);
                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                countStory++;
                            }
                        }
                        if (countStory > 0){
                            mstory.add(story);
                        }

                    }
                }

                storyrv.setAdapter(storyAdapter);
                Collections.reverse(list);
                storyAdapter.notifyDataSetChanged();
                storyrv.hideShimmerAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void readPosts () {
        list = new ArrayList<>();
        postAdapter = new PostAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postrv.setLayoutManager(layoutManager);
        postrv.setNestedScrollingEnabled(false);
//        postrv.setAdapter(postAdapter);


        db.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostModel post = snapshot.getValue(PostModel.class);
                    for (String id : followingList) {
                        if (post.getPostedBy().equals(id)) {
                            list.add(post);
                        }
                    }
                }
                postrv.setAdapter(postAdapter);
                Collections.reverse(list);
                postAdapter.notifyDataSetChanged();
                postrv.hideShimmerAdapter();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}










