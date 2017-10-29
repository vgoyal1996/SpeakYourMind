package com.example.vipul.speakyourmind.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.fragment.ChatFragment;
import com.example.vipul.speakyourmind.fragment.FeedFragment;
import com.example.vipul.speakyourmind.other.CircleTransformation;
import com.example.vipul.speakyourmind.other.PicassoCache;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.vipul.speakyourmind.R.menu.drawer;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ChatFragment.OnListFragmentInteractionListener {
    private int currentID= R.id.nav_feed;
    private FirebaseAuth auth;
    private ImageView profilePic;
    private TextView nameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        profilePic = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView);
        nameText = (TextView)navigationView.getHeaderView(0).findViewById(R.id.name_text);
        emailText = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView);
        navigationView.setNavigationItemSelectedListener(this);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popupIntent = new Intent(DrawerActivity.this,PicturePopUpActivity.class);
                popupIntent.putExtra(PicturePopUpActivity.POP_UP_FLAG,0);
                startActivity(popupIntent);
                overridePendingTransition(R.anim.profile_dialog_grow,0);
            }
        });

        if(savedInstanceState==null)
            selectFragment(R.id.nav_feed);
        else{
            currentID = savedInstanceState.getInt("position");
        }
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragMan = getFragmentManager();
                Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                if(fragment instanceof FeedFragment)
                    currentID = 0;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectFragment(id);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position",currentID);
    }

    private void selectFragment(int id){
        currentID = id;
        if (id == R.id.nav_feed) {
            Fragment fragment = new FeedFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment,"visible_fragment");
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if (id == R.id.nav_chat) {
            Fragment fragment = new ChatFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment,"visible_fragment");
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if(id == R.id.nav_sign_out){
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        }
        setActionBarTitle(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setActionBarTitle(int position){
        if(position==R.id.nav_feed){
            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar_layout);
            TextView myText = (TextView)findViewById(R.id.mytext);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            myText.setText("Welcome "+auth.getCurrentUser().getDisplayName());
            Uri uri = auth.getCurrentUser().getPhotoUrl();
            int width = profilePic.getDrawable().getIntrinsicWidth();
            int height = profilePic.getDrawable().getIntrinsicHeight();
            PicassoCache.getPicassoInstance(DrawerActivity.this).load(uri).resize(width,height).centerCrop().transform(new CircleTransformation()).into(profilePic);
            nameText.setText(auth.getCurrentUser().getDisplayName());
            emailText.setText(auth.getCurrentUser().getEmail());
            //getSupportActionBar().setTitle("name");
        }
    }

    @Override
    public void onListFragmentInteraction(String item) {
        //Toast.makeText(this,item,Toast.LENGTH_LONG).show();
    }
}
