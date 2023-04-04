package com.example.socialmedia.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.socialmedia.Activity.loginActivity;
import com.example.socialmedia.Model.PostModel;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class Add_Post extends Fragment {
    ImageView addimg,postimg,profileimg;
    EditText postdesc;
    Button postbtn;
    TextView username;
    Uri uri;
    ProgressDialog dialog;
    FirebaseDatabase db=FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_addpost, container, false);
        addimg =view.findViewById(R.id.addimage);
        postimg =view.findViewById(R.id.postimage);
        profileimg=view.findViewById(R.id.profileimg);
        postdesc=view.findViewById(R.id.postdescription);
        postbtn=view.findViewById(R.id.postbtn);
        username =view.findViewById(R.id.username);
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("post Uploading");
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        db.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user  = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(profileimg);
                    username.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Textchange();
        addimgfunction();
        pstbtn();
        return  view;
    }

    private void pstbtn() {
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                final StorageReference ref =storage.getReference().child("posts").child(auth.getUid()).child(new Date().getTime()+"");
                        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Toast.makeText(getContext(), "uploaded successfully", Toast.LENGTH_SHORT).show();
                                        PostModel postModel = new PostModel();
                                        String pimg = uri.toString();
                                        postModel.setPostImage(pimg);
                                        String postidd = db.getReference().push().getKey();
                                        postModel.setPostDescription(postdesc.getText().toString());
                                        postModel.setPostid(postidd);
                                        postModel.setPostedBy(auth.getCurrentUser().getUid());
                                        postModel.setPostedAt(new Date().getTime());
                                        db.getReference().child("posts").child(postidd).setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), "Photo uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                postdesc.setText("");
                                                postimg.setVisibility(View.GONE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (dialog.isShowing()){
                                                    dialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }
                                });


                            }
                        });

            }
        });
    }

    private void addimgfunction() {
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });
    }

    private void Textchange() {
        postdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!postdesc.getText().toString().isEmpty()){
                    postbtn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.follow_unactive_btn));
                    postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    postbtn.setEnabled(true);
                }else{
                    postbtn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.follow_active_btn));
                    postbtn.setTextColor(getContext().getResources().getColor(R.color.grey));
                    postbtn.setEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData()!=null){
            uri = data.getData();
            postimg.setImageURI(uri);
            postimg.setVisibility(View.VISIBLE);

            postbtn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.follow_unactive_btn));
            postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
            postbtn.setEnabled(true);

        }
    }
}
