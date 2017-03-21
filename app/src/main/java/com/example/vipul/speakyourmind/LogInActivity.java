package com.example.vipul.speakyourmind;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        final EditText emailText = (EditText)findViewById(R.id.login_email);
        emailText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        final EditText passwordText = (EditText)findViewById(R.id.login_password);
        passwordText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        Button logInButton = (Button)findViewById(R.id.login_button);
        logInButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        Button createAccountButton = (Button)findViewById(R.id.create_new_account_button);
        createAccountButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        TextView forgotPasswordText = (TextView)findViewById(R.id.text_forgot_password);
        forgotPasswordText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        TextView headText = (TextView)findViewById(R.id.heading_text);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/stocky.ttf");
        headText.setTypeface(custom_font);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        myText.setText("LOGIN");
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this,ForgotPasswordActivity.class));
            }
        });
        createAccountButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00000000));
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                pd = ProgressDialog.show(LogInActivity.this,"Logging In","Please wait...",true,false);
                Thread t = new Thread(new LogInThread(LogInActivity.this,email,password));
                t.start();
            }
        });
    }

    public class LogInThread implements Runnable{
        Context context;
        //Task<AuthResult> task;
        String email,password;

        public LogInThread(Context context,String email,String password) {
            this.context = context;
            this.email = email;
            this.password = password;
            //this.task = task;
        }

        @Override
        public void run() {
            if(TextUtils.isEmpty(email)) {
                Message m = new Message();
                m.what = 2;
                handler.sendMessage(m);
            }
            else if(TextUtils.isEmpty(password)) {
                Message m = new Message();
                m.what = 3;
                handler.sendMessage(m);
            }
            else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            handler.sendEmptyMessage(0);
                            //Toast.makeText(LogInActivity.this,"Wrong EmailId or password",Toast.LENGTH_SHORT).show();
                        } else {
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
                    case 0:
                        Toast.makeText(LogInActivity.this,"Wrong EmailId or password",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        startActivity(new Intent(LogInActivity.this,MainActivity.class));
                        finish();
                        break;
                    case 2:
                        Toast.makeText(LogInActivity.this, "Please enter email address!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(LogInActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }
}
