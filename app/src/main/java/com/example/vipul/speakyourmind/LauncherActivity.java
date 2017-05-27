package com.example.vipul.speakyourmind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            startActivity(new Intent(LauncherActivity.this,LogInActivity.class));
        else {
            startActivity(new Intent(LauncherActivity.this, DrawerActivity.class));
        }
        finish();
    }

}
