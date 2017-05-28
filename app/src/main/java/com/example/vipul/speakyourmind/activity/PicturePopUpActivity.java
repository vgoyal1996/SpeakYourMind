package com.example.vipul.speakyourmind.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.other.CircleTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class PicturePopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_pop_up);

        ImageView profileDisplay = (ImageView)findViewById(R.id.profile_display);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.pop_up_layout);
        layout.getBackground().setAlpha(180);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Uri uri = auth.getCurrentUser().getPhotoUrl();
        Picasso.with(PicturePopUpActivity.this).load(uri).transform(new CircleTransformation()).into(profileDisplay);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,R.anim.profile_dialog_shrink);
    }
}
