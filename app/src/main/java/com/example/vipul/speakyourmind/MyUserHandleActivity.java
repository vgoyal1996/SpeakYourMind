package com.example.vipul.speakyourmind;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyUserHandleActivity extends AppCompatActivity {
    public static final String USER = "user";
    public static final String ARRAYLIST = "arraylist";
    public static final String MESSAGE_KEYS = "message";
    public static final String INFO = "info";
    public static String UID;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FirebaseStorage storage;
    private ViewPager viewPager;
    private UserModel userModel;
    private ImageView icam;
    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    private FirebaseAuth currUser;
    private byte[] finalImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_handle);
        userModel = (UserModel)getIntent().getSerializableExtra(USER);
        UID = userModel.getUid();
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        currUser = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        icam = (ImageView) findViewById(R.id.profile_photo);
        storage = FirebaseStorage.getInstance();
        if(UID.equals(MainActivity.USER_UID)) {
            myText.setText(MainActivity.CURRENT_USER);
            Uri uri = currUser.getCurrentUser().getPhotoUrl();
            //Glide.with(MyUserHandleActivity.this).load(uri).into(icam);
            if(icam.getDrawable()!=null) {
                Toast.makeText(MyUserHandleActivity.this, icam.getDrawable().toString(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(MyUserHandleActivity.this,"false",Toast.LENGTH_SHORT).show();
        }
        else {
            myText.setText(userModel.getUserName());
            StorageReference ref = storage.getReference().child(UID + "/" + userModel.getEmail() + ".jpg");
            Glide.with(MyUserHandleActivity.this).using(new FirebaseImageLoader()).load(ref).into(icam);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        icam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UID.equals(MainActivity.USER_UID))
                    selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MyUserHandleActivity.this);
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

                icam.setImageBitmap(thumbnail);
                finalImage = bytes.toByteArray();

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
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
                BitmapFactory.decodeFile(selectedImagePath, options);
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
                finalImage = bos.toByteArray();
                icam.setImageBitmap(bm);
            }
            if(finalImage!=null){
                StorageReference ref = storage.getReference().child(UID+"/"+userModel.getEmail()+".jpg");
                UploadTask uploadTask = ref.putBytes(finalImage);
                final Uri[] downloadUrl = new Uri[1];
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MyUserHandleActivity.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MyUserHandleActivity.this,"Profile photo changed",Toast.LENGTH_SHORT).show();
                        downloadUrl[0] = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl[0]).build();
                        currUser.getCurrentUser().updateProfile(profileUpdates);
                    }
                });
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(currUser.getCurrentUser().getEmail().equals(userModel.getEmail())){
            BlankFragment1 frag1 = new BlankFragment1();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARRAYLIST, (ArrayList<? extends Parcelable>) userModel.getStatusModelList());
            bundle.putParcelableArrayList(MESSAGE_KEYS, (ArrayList<? extends Parcelable>) userModel.getMessageKeyModelList());
            frag1.setArguments(bundle);
            adapter.addFragment(frag1, "Recent Activity");
        }
        else{
            BlankFragment3 frag3 = new BlankFragment3();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARRAYLIST, (ArrayList<? extends Parcelable>) userModel.getStatusModelList());
            bundle.putParcelableArrayList(MESSAGE_KEYS, (ArrayList<? extends Parcelable>) userModel.getMessageKeyModelList());
            frag3.setArguments(bundle);
            adapter.addFragment(frag3, "Recent Activity");
        }
        BlankFragment2 frag2 = new BlankFragment2();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(INFO,userModel);
        frag2.setArguments(bundle2);
        //adapter.addFragment(frag1, "Recent Activity");
        adapter.addFragment(frag2, "About");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
