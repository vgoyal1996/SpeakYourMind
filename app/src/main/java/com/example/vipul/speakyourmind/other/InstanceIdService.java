package com.example.vipul.speakyourmind.other;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by VIPUL on 10/10/2017.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.v(TAG,"Refreshed Token: "+instanceId);
        if(firebaseUser!=null){
            FirebaseDatabase.getInstance().getReference()
                    .child(firebaseUser.getUid())
                    .child("instanceId")
                    .setValue(instanceId);
        }
    }
}
