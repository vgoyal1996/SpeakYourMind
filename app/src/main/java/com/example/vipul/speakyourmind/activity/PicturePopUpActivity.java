package com.example.vipul.speakyourmind.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.other.CircleTransformation;
import com.example.vipul.speakyourmind.other.PicassoCache;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PicturePopUpActivity extends AppCompatActivity {
    public static final String POP_UP_FLAG = "flag";
    private int flag;
    public static final String POP_UP_PICTURE = "picture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_pop_up);

        flag = (int) getIntent().getExtras().get(POP_UP_FLAG);
        final ImageView profileDisplay = (ImageView)findViewById(R.id.profile_display);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.pop_up_layout);
        layout.getBackground().setAlpha(180);
        if(flag==0) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Uri uri = auth.getCurrentUser().getPhotoUrl();
            PicassoCache.getPicassoInstance(PicturePopUpActivity.this).load(uri).transform(new CircleTransformation()).into(profileDisplay);
        }
        else{
            String picture = (String)getIntent().getExtras().get(POP_UP_PICTURE);
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(picture);
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    PicassoCache.getPicassoInstance(PicturePopUpActivity.this).load(uri).transform(new CircleTransformation()).into(profileDisplay);
                }
            });
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(flag==0)
            overridePendingTransition(0,R.anim.profile_dialog_shrink);
    }
}
