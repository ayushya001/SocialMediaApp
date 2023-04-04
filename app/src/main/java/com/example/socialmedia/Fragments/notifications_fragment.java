package com.example.socialmedia.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.socialmedia.Adapter.NotificationAdapter;
import com.example.socialmedia.Model.NotificationModel;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class notifications_fragment extends Fragment {
    ShimmerRecyclerView notificationrv;
    ArrayList<NotificationModel> list4;
    NotificationModel notificationModel;
    NotificationAdapter notificationAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notifications_fragment, container, false);
        notificationrv = view.findViewById(R.id.notification_rv);
        notificationrv.showShimmerAdapter();
        list4 = new ArrayList<>();
        notificationAdapter= new NotificationAdapter(list4,getContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        notificationrv.setLayoutManager(layoutManager2);
        notificationrv.setNestedScrollingEnabled(false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list4.clear();

                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            NotificationModel model = snapshot1.getValue(NotificationModel.class);
                            model.setNotificationid(snapshot1.getKey());
                            if (!model.getNotificationBy().equals(FirebaseAuth.getInstance().getUid())){
                                list4.add(model);
                            }

                        }
                        notificationrv.setAdapter(notificationAdapter);
                        notificationAdapter.notifyDataSetChanged();
                        notificationrv.hideShimmerAdapter();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return  view;
    }
}