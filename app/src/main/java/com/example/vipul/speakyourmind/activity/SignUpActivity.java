package com.example.vipul.speakyourmind.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText phoneEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog pd;
    private String name;
    private String email;
    private String password;
    private String phone;
    private Task<AuthResult> createdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        myText.setText("NEW HANDLE");
        TextView headingText = (TextView)findViewById(R.id.sign_up_heading_text);
        headingText.setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/stocky.ttf"));
        TextView nameText = (TextView)findViewById(R.id.sign_up_name);
        nameText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        TextView emailText = (TextView)findViewById(R.id.sign_up_email);
        emailText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        TextView passwordText = (TextView)findViewById(R.id.sign_up_password);
        passwordText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        final TextView phoneText = (TextView)findViewById(R.id.sign_up_phone);
        phoneText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        nameEditText = (EditText)findViewById(R.id.name_edit_text);
        nameEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        emailEditText = (EditText)findViewById(R.id.email_edit_text);
        emailEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        passwordEditText = (EditText)findViewById(R.id.password_edit_Text);
        passwordEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
        phoneEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        Button createAccountButton = (Button)findViewById(R.id.create_account_button);
        createAccountButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();
                pd = ProgressDialog.show(SignUpActivity.this,"Creating Handle","Please wait...",true,false);
                Thread t = new Thread(new UserThread(SignUpActivity.this,name,email,password,phone));
                t.start();
            }
        });
    }

    private class UserThread implements Runnable{
        Context context;
        String finalName,finalEmail,finalPassword,finalPhone;

        public UserThread(Context context,String finalName,String finalEmail,String finalPassword,String finalPhone) {
            this.context = context;
            this.finalName = finalName;
            this.finalEmail = finalEmail;
            this.finalPassword = finalPassword;
            this.finalPhone = finalPhone;
        }

        @Override
        public void run() {
            if(TextUtils.isEmpty(finalName)) {
                Message m = new Message();
                m.what = 2;
                handler.sendMessage(m);
            }
            else if(TextUtils.isEmpty(finalEmail)) {
                Message m = new Message();
                m.what = 4;
                handler.sendMessage(m);
            }
            else if(TextUtils.isEmpty(finalPassword)) {
                Message m = new Message();
                m.what = 3;
                handler.sendMessage(m);
            }
            else if(TextUtils.isEmpty(finalPhone)) {
                Message m = new Message();
                m.what = 5;
                handler.sendMessage(m);
            }
            else{
                createdUser = auth.createUserWithEmailAndPassword(finalEmail,finalPassword);
                createdUser.addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Message m = new Message();
                            m.what = 6;
                            m.obj = task.getException().getMessage();
                            handler.sendMessage(m);
                        }
                        else{
                            String uid = createdUser.getResult().getUser().getUid();
                            Map<String,Object> userDetails = new HashMap<>();
                            userDetails.put(uid,new UserModel(name,email,password,phone));
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child(uid).push();
                            ref.updateChildren(userDetails);
                            handler.sendEmptyMessage(1);
                        }
                    }
                });
            }
        }

        private Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                pd.dismiss();
                switch (msg.what){
                    case 1:
                        startActivity(new Intent(SignUpActivity.this, UserProfileActivity.class));
                        overridePendingTransition(R.anim.anim_enter,R.anim.anim_leave);
                        finish();
                        break;
                    case 2:
                        Toast.makeText(SignUpActivity.this, "Please enter name!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(SignUpActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(SignUpActivity.this, "Please enter email address!", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(SignUpActivity.this, "Please enter phone number!", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(SignUpActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}
