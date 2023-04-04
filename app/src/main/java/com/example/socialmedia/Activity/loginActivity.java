package com.example.socialmedia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase db;
    Button lginbtn;
    EditText lemail,lpassword;
    ProgressDialog d1;
    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
       lginbtn = findViewById(R.id.button);
       lemail = findViewById(R.id.lgnemail);
       lpassword = findViewById(R.id.password);
        TextView register = findViewById(R.id.Register);
        d1 = new ProgressDialog(loginActivity.this);
        d1.setTitle("Signing In");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(inte);
                finish();
            }
        });
        lginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = lemail.getText().toString();
                String password = lpassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    lemail.setError("email cannot be empty");
                }else if(TextUtils.isEmpty(password)){
                    lpassword.setError("password cannot be empty");
                }else{
                    d1.show();
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                d1.dismiss();
                                Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                startActivity(intent);

                            }else {
                                if (d1.isShowing()){
                                    d1.dismiss();
                                }
                                Toast.makeText(loginActivity.this, "Email and Password Doesn't Match", Toast.LENGTH_SHORT).show();
                                Log.e("error", "onComplete: "+task.getException().toString() );
                                lemail.setText("");
                                lpassword.setText("");

                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentuser = auth.getCurrentUser();
        if(currentuser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
}