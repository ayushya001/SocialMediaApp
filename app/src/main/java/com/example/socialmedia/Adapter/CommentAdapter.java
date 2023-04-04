package com.example.socialmedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Activity.MainActivity;
import com.example.socialmedia.Model.CommentModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.holder> {

    ArrayList<CommentModel> list;
    Context context;

    public CommentAdapter(ArrayList<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_design,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {


        final  CommentModel model = list.get(position);
        String comment = model.getCommentBody().toString();
                FirebaseDatabase.getInstance().getReference().child("Users").child(model.getCommentedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).into(holder.userimage);
                holder.commentbody.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+"   "+ comment));
                holder.userimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainActivity.class);
                        intent.putExtra("publisherid" , model.getCommentedBy());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String text = TimeAgo.using(model.getCommentedAt());
        holder.time.setText(text);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class holder extends RecyclerView.ViewHolder {
        ImageView userimage;
        TextView commentbody,time;
        public holder(@NonNull View itemView) {
            super(itemView);
            userimage = itemView.findViewById(R.id.userimage);
            commentbody = itemView.findViewById(R.id.text);
            time = itemView.findViewById(R.id.time);
        }
    }
}
