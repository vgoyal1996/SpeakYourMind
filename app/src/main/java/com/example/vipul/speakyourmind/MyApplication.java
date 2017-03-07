package com.example.vipul.speakyourmind;


import com.firebase.client.Firebase;

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
