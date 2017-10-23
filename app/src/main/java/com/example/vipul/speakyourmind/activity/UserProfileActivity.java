package com.example.vipul.speakyourmind.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.other.CircleDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {
    private EditText profileEditText;
    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    private ImageView profilePic;
    private byte[] imageInByte = null;
    private ProgressDialog pd;
    private FirebaseUser currUser;
    private String uid,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currUser.getUid();
        email = currUser.getEmail();
        TextView userProfileHeading = (TextView)findViewById(R.id.user_profile_heading_text);
        TextView userProfileEmail = (TextView)findViewById(R.id.user_profile_email_text);
        TextView userProfilePic = (TextView)findViewById(R.id.user_profile_pic_text);
        profileEditText = (EditText)findViewById(R.id.user_profile_user_name);
        profilePic = (ImageView)findViewById(R.id.user_profile_pic);
        Button update = (Button)findViewById(R.id.user_profile_update_button);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        myText.setText("NEW HANDLE");
        userProfileHeading.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/stocky.ttf"));
        userProfileEmail.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        userProfilePic.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        update.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        profileEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = profileEditText.getText().toString().trim();
                pd = ProgressDialog.show(UserProfileActivity.this,"Updating","Please wait...",true,false);
                Thread t = new Thread(new UserThread(UserProfileActivity.this,userName));
                t.start();
            }
        });
    }

    private class UserThread implements Runnable {
        Context context;
        String userName;

        public UserThread(Context context, String userName) {
            this.context = context;
            this.userName = userName;
        }


        @Override
        public void run() {
            if(TextUtils.isEmpty(userName)){
                Message m = new Message();
                m.what = 2;
                handler.sendMessage(m);
            }
            if(imageInByte!=null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage.getReference().child(uid+"/"+email+".jpg");
                UploadTask uploadTask = ref.putBytes(imageInByte);
                final Uri[] downloadUrl = new Uri[1];
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Message m = new Message();
                        m.what = 3;
                        m.obj = exception.getMessage();
                        handler.sendMessage(m);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl[0] = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName).setPhotoUri(downloadUrl[0]).build();
                        currUser.updateProfile(profileUpdates);
                        handler.sendEmptyMessage(1);
                    }
                });
            }
            else{
                handler.sendEmptyMessage(1);
            }
        }

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                pd.dismiss();
                switch (msg.what){
                    case 1:Toast.makeText(UserProfileActivity.this,"Sign up successful!!!\n" +
                            "Please login to continue",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserProfileActivity.this,LogInActivity.class));
                        overridePendingTransition(R.anim.anim_enter,R.anim.anim_leave);
                        finish();
                        break;
                    case 2:Toast.makeText(UserProfileActivity.this,"Please enter the Handle name",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:Toast.makeText(UserProfileActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();

                }
            }
        };

    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                profilePic.setImageBitmap(thumbnail);
                imageInByte = bytes.toByteArray();

            } else if (requestCode == SELECT_FILE) {
                /*Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                imageInByte = bos.toByteArray();*/
                final Uri imageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap bm = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                imageInByte = bos.toByteArray();
                CircleDrawable circle = new CircleDrawable(bm,true);
                profilePic.setImageDrawable(circle);
            }
        }
    }
}
