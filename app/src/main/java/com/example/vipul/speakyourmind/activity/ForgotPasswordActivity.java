package com.example.vipul.speakyourmind.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        final AutoCompleteTextView emailText = (AutoCompleteTextView) findViewById(R.id.email_for_reset_editText);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = prefs.getStringSet("auto_complete_set",null);
        if(set!=null){
            List<String> list = new ArrayList<>(set);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
            emailText.setAdapter(adapter);
        }
        emailText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        Button submitButton = (Button)findViewById(R.id.reset_password_button);
        submitButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        TextView headingText = (TextView)findViewById(R.id.forgot_password_heading_text);
        headingText.setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/stocky.ttf"));
        TextView emailTextView = (TextView)findViewById(R.id.forgot_password_email_text);
        emailTextView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        myText.setText("FORGOT PASSWORD");
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.sendPasswordResetEmail(emailText.getText().toString().trim()).addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful())
                            Toast.makeText(ForgotPasswordActivity.this,"Email not found",Toast.LENGTH_SHORT).show();
                        else{
                            Toast.makeText(ForgotPasswordActivity.this,"Instructions to reset your password have been sent to your Email.",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPasswordActivity.this,LogInActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
    }
}
