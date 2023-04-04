package com.example.socialmedia.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Editprofile extends AppCompatActivity {

    ImageView close;
    ImageView image_profile;
    TextView save;
    TextView tv_change;
    MaterialEditText fullname;
    MaterialEditText username;
    MaterialEditText bio;
    FirebaseUser firebaseUser;
    ProgressDialog d;
    Uri uri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        username = findViewById(R.id.username);
        username.setEnabled(false);
        bio = findViewById(R.id.bio);
        d = new ProgressDialog(this);
        d.setTitle("Updating....");
        d.setCanceledOnTouchOutside(false);

//        Toast.makeText(this, "You can't change your username! Contact admin.", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                username.setText(user.getName());
                bio.setText(user.getBio());
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.user_icon2).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
//                CropImage.activity().setAspectRatio(1 , 1)
//                        .setCropShape(CropImageView.CropShape.OVAL)
//                        .start(EditProfileActivity.this);
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                image_profile.setImageURI(result);
                uri =  Uri.parse(result.toString());
            }
        });

//        image_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CropImage.activity().setAspectRatio(1 , 1)
//                        .setCropShape(CropImageView.CropShape.OVAL)
//                        .start(EditProfileActivity.this);
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.show();

                updateProfile(username.getText().toString() , bio.getText().toString(),uri.toString());
            }
        });
    }

    private void updateProfile(String toString, String toString1, String s) {

        storageRef = storage.getReference().child("profilePhoto").child(new Date().getTime() + "");
        storageRef.putFile(Uri.parse(s)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     HashMap User = new HashMap();
                     User.put("bio", toString1);
                     User.put("name", toString);
                     User.put("profilePhoto",s);

                     FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                             .updateChildren(User).addOnSuccessListener(new OnSuccessListener() {
                                 @Override
                                 public void onSuccess(Object o) {
                                     d.dismiss();
                                     Toast.makeText(Editprofile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                     finish();

                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.e("EditProfile", "onFailure:editprofile "+e.toString() );

                                 }
                             });

                 }
             });

            }
        });


    }

//    private void updateProfile(String fullname, String username, String bio) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
////        HashMap<String , Object> hashMap = new HashMap<>();
////        hashMap.put("fullname" , fullname);
////        hashMap.put("username" , username);
////        hashMap.put("bio" , bio);
////
////        reference.updateChildren(hashMap);
//
//    }
//
//    private String getFileExtension (Uri uri){
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
//
//    private void uploadImage(){
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Uploading");
//        pd.show();
//
//        if (mImageUri != null){
//            final StorageReference filereference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
//
//            uploadTask = filereference.putFile(mImageUri);
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()){
//                        throw task.getException();
//                    }
//
//                    return filereference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()){
//                        Uri downloadUri = task.getResult();
//                        String myUrl = downloadUri.toString();
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//                        HashMap<String , Object> hashMap = new HashMap<>();
//                        hashMap.put("imageurl" , "" + myUrl);
//
//                        reference.updateChildren(hashMap);
//                        pd.dismiss();
//                    } else {
//                        Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(EditProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            mImageUri = result.getUri();
//
//            uploadImage();
//        } else {
//            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
//        }
//    }
}

