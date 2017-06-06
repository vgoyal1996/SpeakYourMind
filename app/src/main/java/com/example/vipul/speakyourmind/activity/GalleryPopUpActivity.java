package com.example.vipul.speakyourmind.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vipul.speakyourmind.R;

public class GalleryPopUpActivity extends AppCompatActivity {
    public static final String NUMBER_OF_IMAGES="images";
    public static final String STARTING_ID="start";
    public static final String CLICKED_ID="clicked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_pop_up);

        int nImages = (int)getIntent().getExtras().get(NUMBER_OF_IMAGES);
        int startID = (int)getIntent().getExtras().get(STARTING_ID);
        int clickedID = (int)getIntent().getExtras().get(CLICKED_ID);
        ImageView profileDisplay = (ImageView)findViewById(R.id.profile_display);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.pop_up_layout);
        layout.getBackground().setAlpha(180);
        //FirebaseAuth auth = FirebaseAuth.getInstance();
        //Uri uri = auth.getCurrentUser().getPhotoUrl();
        profileDisplay.setImageResource(clickedID);
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
