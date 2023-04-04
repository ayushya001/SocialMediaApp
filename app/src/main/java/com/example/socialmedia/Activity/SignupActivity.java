package com.example.socialmedia.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {
    EditText Rname,Remail,Rpassword;
    Button btn;
    TextView login;
    FirebaseDatabase db;
    FirebaseAuth auth;
    Uri uri;
    ProgressDialog progressDialog,pdialog;
    Dialog d1;
    ActivityResultLauncher<String> galleryLauncher;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    FirebaseAuth mauth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setTitle("Signing up");
        Rname = findViewById(R.id.Rname);
        Remail = findViewById(R.id.Remail);
        Rpassword = findViewById(R.id.rpassword);
        login = findViewById(R.id.Login);
        pdialog = new ProgressDialog(SignupActivity.this);
        pdialog.setTitle("Uploading...");
        pdialog.setCanceledOnTouchOutside(false);
        processDialog();






        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),loginActivity.class);
                startActivity(intent);
            }
        });
        btn = findViewById(R.id.Rbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =Rname.getText().toString();
                String email =Remail.getText().toString();
                String password =Rpassword.getText().toString();
//                String idd = auth.getUid();

                if (TextUtils.isEmpty(name)){
                    Rname.setError("Name cannot be empty");
                    Rname.requestFocus();
                }if (TextUtils.isEmpty(password)){
                    Rpassword.setError("password cannot be empty");
                    Rpassword.requestFocus();
                }if (TextUtils.isEmpty(email)){
                    Remail.setError("email cannot be empty");
                    Remail.requestFocus();
                }
                else{
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                User user = new User(name,email,password);
                                user.setId(auth.getUid());
                                db.getReference().child("Users").child(auth.getUid()).setValue(user);
                                progressDialog.dismiss();
                                d1.show();






                            }else{
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("error", "onComplete: "+task.getException().toString() );
                                Rname.setText("");
                                Rpassword.setText("");
                                Remail.setText("");
                            }
                        }
                    });
                }



            }
        });

    }

    private void  processDialog() {
        d1= new Dialog(this);
        d1.setContentView(R.layout.personal_details);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            d1.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        }
        d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        d1.setCancelable(false);


        ImageView add = d1.findViewById(R.id.add_pic);
        ImageView skip =d1.findViewById(R.id.skip);
        ImageView pic = d1.findViewById(R.id.circleImageView);
        EditText bio = d1.findViewById(R.id.bio);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SignupActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");

            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                pic.setImageURI(result);
                uri =  Uri.parse(result.toString());



            }
        });

        Button submit = d1.findViewById(R.id.button3);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pdialog.show();


                    if (uri != null && bio!=null) {
                        pdialog.show();
                        StorageReference reference = storage.getReference().child("profilePhoto").child(new Date().getTime() + "");
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Toast.makeText(getApplicationContext(), "uploaded successfully", Toast.LENGTH_SHORT).show();

                                        db.getReference().child("Users").child(mauth.getUid()).child("profilePhoto").setValue(uri.toString());
                                        db.getReference().child("Users").child(mauth.getUid()).child("bio").setValue(bio.getText().toString());

                                        pdialog.dismiss();

                                        Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        d1.dismiss();



                                    }
                                });

                            }
                        });

                    }
                    else{
//                        if (pdialog.isShowing()){
//                            pdialog.dismiss();
//                        }
                        Toast.makeText(SignupActivity.this, "Set your profile picture and bio", Toast.LENGTH_SHORT).show();
                    }
                }

        });

    }
}