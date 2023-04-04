package com.example.socialmedia.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.SearchAdapter;
import com.example.socialmedia.Adapter.userAdapter;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Search extends Fragment {
    RecyclerView searchrv;
    ArrayList<User> list = new ArrayList<User>();
    FirebaseDatabase db;
    FirebaseAuth auth;
    EditText searchBar ;
    SearchAdapter searchAdapter;
//    ArrayList list1 = new ArrayList();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        db= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        searchrv = view.findViewById(R.id.rvv);
        searchBar = view.findViewById(R.id.search_bar);


//        us = new userAdapter(getContext(), list);
        searchAdapter = new SearchAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchrv.setLayoutManager(linearLayoutManager);
        searchrv.setHasFixedSize(true);
        searchrv.setAdapter(searchAdapter);
//        readuser();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchuser(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        db.getReference().child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User userr = dataSnapshot.getValue(User.class);
//                    if (dataSnapshot.exists()){
//                        list1.add(userr);
//                        Log.d("list1", "onDataChange: "+list1);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        String s = searchBar.getText().toString();
//        searchuser(s);
//        db.getReference().child("Users").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User user = dataSnapshot.getValue(User.class);
//                    user.setUserID(dataSnapshot.getKey());
//                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
//                        list.add(user);
//                    }
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        return  view;
    }

    private void searchuser(String s) {
        Query query= db.getReference().child("Users").orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    list.add(user);
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




//    private void readuser() {
//        db.getReference().child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (TextUtils.isEmpty(searchBar.getText().toString())){
//                    list.clear();
//                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                        User user = snapshot1.getValue(User.class);
//                        list.add(user);
//                    }
//                }
//                searchAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


}
