package com.example.socialmedia.Activity;

import static com.example.socialmedia.R.id.menu_account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.socialmedia.Fragments.Add_Post;
import com.example.socialmedia.Fragments.Home;
import com.example.socialmedia.Fragments.Profile_fragment;
import com.example.socialmedia.Fragments.Search;
import com.example.socialmedia.Fragments.notifications_fragment;
import com.example.socialmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bv;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

//        toolbar.setTitleTextColor(Color.BLACK);

        bv = findViewById(R.id.bv);
//        bv.setOnItemSelectedListener(R.id.men);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS" , MODE_PRIVATE).edit();
            editor.putString("profileid" , publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fm , new Profile_fragment()).commit();
            bv.setSelectedItemId(menu_account);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fm,new Home()).commit();
        }


        bvChange();

    }
    public void bvChange(){
        bv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;
                switch (item.getItemId()){
                    case R.id.menu_home:

                        temp=new Home();
                        break;
                    case menu_account:

                        temp=new Profile_fragment();
                        break;
                    case R.id.menu_search:

                        temp = new Search();
                        break;
                    case R.id.menu_notification:

                        temp = new notifications_fragment();
                        break;
                    case R.id.menu_add:

                        temp = new Add_Post();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fm,temp).commit();
                return true;
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_item,menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.setting:
//                auth.signOut();
//                startActivity(new Intent(getApplicationContext(),loginActivity.class));
//                finish();
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}