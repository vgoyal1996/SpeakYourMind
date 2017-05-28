package com.example.vipul.speakyourmind.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.fragment.FeedFragment;
import com.example.vipul.speakyourmind.model.StatusModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.vipul.speakyourmind.fragment.FeedFragment.USER_UID;

public class TakePhotoActivity extends AppCompatActivity {
    protected static List<String> photos;
    private BaseAdapter adapter;
    private int reqCode = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select images to display");
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryConfig config = new GalleryConfig.Build()
                        .limitPickPhoto(8)
                        .singlePhoto(false)
                        .hintOfPick("only 8 photos can be selected")
                        .build();
                GalleryActivity.openActivity(TakePhotoActivity.this, reqCode, config);
            }
        });

        Button addPhotoButton = (Button)findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());
                String key = dbRef.child(USER_UID).child("statusList").push().getKey();
                StatusModel listModel = new StatusModel(key+" "+photos.size(),date, USER_UID);
                StatusModel firebaseModel = new StatusModel(key+" "+photos.size(),date);
                FeedFragment.addToStatuses(listModel,firebaseModel);
                dbRef.child(USER_UID).child("statusList").child(key).setValue(firebaseModel);
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://speakyourmind-d0d3a.appspot.com");
                for(int i=0;i<photos.size();i++){
                    StorageReference ref = reference.child(USER_UID+"/"+key+"/photo"+i+".png/");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(photos.get(i), options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(photos.get(i), options);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] imageInByte = bos.toByteArray();
                    UploadTask uploadTask = ref.putBytes(imageInByte);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TakePhotoActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.v("ADD_PHOTO",e.getMessage());
                            Log.v("ADD_PHOTO_EXCEPTION",e.getCause().getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(TakePhotoActivity.this,"success",Toast.LENGTH_SHORT).show();
                            taskSnapshot.getDownloadUrl();
                        }
                    });
                }
                photos.clear();
                onBackPressed();
            }
        });

        GridView gv = (GridView) findViewById(R.id.gv);
        gv.setAdapter(adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return photos == null ? 0 : photos.size();
            }

            @Override
            public Object getItem(int position) {
                if (photos == null) {
                    return null;
                }
                return photos.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {
                final ImageView view = new ImageView(TakePhotoActivity.this);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        256));
                final String path = (String) getItem(position);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
                opts.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
                view.setImageBitmap(bitmap);
                if(photos.size()<8){
                    fab.setVisibility(View.VISIBLE);
                }
                if(photos.size()==8){
                    fab.setVisibility(View.GONE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),""+path, Toast.LENGTH_LONG).show();
                        final Dialog settingsDialog = new Dialog(TakePhotoActivity.this);
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        View view1 = getLayoutInflater().inflate(R.layout.image_layout, null);
                        settingsDialog.setContentView(view1);
                        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView load_image = (ImageView) view1.findViewById(R.id.load_image);
                        BitmapFactory.Options opts1 = new BitmapFactory.Options();
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , ViewGroup.LayoutParams.MATCH_PARENT);
                        load_image.setLayoutParams(layoutParams);
                        load_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        opts1.inPreferredConfig = Bitmap.Config.ARGB_4444;
                        opts1.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(path, opts1);
                        load_image.setImageBitmap(bitmap);
                        settingsDialog.show();
                        Button close_window = (Button) view1.findViewById(R.id.close_window);
                        close_window.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                settingsDialog.dismiss();
                            }
                        });
                        Button delete_this_item = (Button) view1.findViewById(R.id.delete_this_item);
                        delete_this_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                photos.remove(path);
                                adapter.notifyDataSetChanged();
                                settingsDialog.dismiss();
                            }
                        });
                    }
                });

                return view;
            }
        });
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (12 == requestCode && resultCode == Activity.RESULT_OK) {
            photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,R.anim.dialog_out);
    }
}
