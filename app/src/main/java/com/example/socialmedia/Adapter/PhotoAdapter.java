package com.example.socialmedia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoAdapter extends  RecyclerView.Adapter<PhotoAdapter.holder> {

    Context context;
    ArrayList<PostModel> list;

    public PhotoAdapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false);
        return new PhotoAdapter.holder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
     PostModel post= list.get(position);
        Picasso.get().load(post.getPostImage()).placeholder(R.drawable.user_icon2).into(holder.postimg);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        ImageView postimg;
        public holder(@NonNull View itemView) {
            super(itemView);
            postimg = itemView.findViewById(R.id.post_image);
        }
    }
}
