package com.example.vipul.speakyourmind.activity;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.fragment.BlankFragment1;
import com.example.vipul.speakyourmind.fragment.BlankFragment2;
import com.example.vipul.speakyourmind.fragment.BlankFragment3;
import com.example.vipul.speakyourmind.fragment.FeedFragment;
import com.example.vipul.speakyourmind.model.StatusModel;
import com.example.vipul.speakyourmind.model.UserModel;
import com.example.vipul.speakyourmind.other.PicassoCache;
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
        currUser = FirebaseAuth.getInstance();
        icam = (ImageView) findViewById(R.id.profile_photo);
        CollapsingToolbarLayout collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        collapsing_container.setCollapsedTitleTypeface(Typeface.create("cursive",Typeface.NORMAL));
        collapsing_container.setExpandedTitleTypeface(Typeface.create("cursive",Typeface.NORMAL));
        storage = FirebaseStorage.getInstance();
        if(UID.equals(FeedFragment.USER_UID)) {
            collapsing_container.setTitle(FeedFragment.CURRENT_USER);
            Uri uri = currUser.getCurrentUser().getPhotoUrl();
            PicassoCache.getPicassoInstance(MyUserHandleActivity.this).load(uri).into(icam);
        }
        else {
            collapsing_container.setTitle(userModel.getUserName());
            StorageReference ref = storage.getReference().child(UID + "/" + userModel.getEmail() + ".jpg");
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    PicassoCache.getPicassoInstance(MyUserHandleActivity.this).load(uri).into(icam);
                }
            });
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        icam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UID.equals(FeedFragment.USER_UID))
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
                bm.compress(Bitmap.CompressFormat.JPEG
                        , 100, bos);
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
        List<StatusModel> list = userModel.getStatusModelList();
        for(int i=0;i<list.size();i++){
            StatusModel model = list.get(i);
            model.setUid(UID);
            list.set(i,model);
        }
        if(currUser.getCurrentUser().getEmail().equals(userModel.getEmail())){
            BlankFragment1 frag1 = new BlankFragment1();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARRAYLIST, (ArrayList<? extends Parcelable>) list);
            bundle.putParcelableArrayList(MESSAGE_KEYS, (ArrayList<? extends Parcelable>) userModel.getMessageKeyModelList());
            frag1.setArguments(bundle);
            adapter.addFragment(frag1, "Recent Activity");
        }
        else{
            BlankFragment3 frag3 = new BlankFragment3();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARRAYLIST, (ArrayList<? extends Parcelable>) list);
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
