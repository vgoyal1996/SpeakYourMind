package com.example.vipul.speakyourmind.Photos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vipul.speakyourmind.R;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.util.List;


public class TakePhoto extends AppCompatActivity {

    protected static List<String> photos;
    private BaseAdapter adapter;
    private int reqCode = 12;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        /*if(MedicalRecordActivity.counter==0) {
            MedicalRecordActivity.counter=1;
            GalleryConfig config = new GalleryConfig.Build()
                    .limitPickPhoto(8)
                    .singlePhoto(false)
                    .hintOfPick("this is pick hint")
                    .filterMimeTypes(new String[]{"image/jpeg"})
                    .build();
            GalleryActivity.openActivity(TakeRecord.this, reqCode, config);
        }*/
        add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryConfig config = new GalleryConfig.Build()
                        .limitPickPhoto(8)
                        .singlePhoto(false)
                        .hintOfPick("this is pick hint")
                        .filterMimeTypes(new String[]{"image/jpeg"})
                        .build();
                GalleryActivity.openActivity(TakePhoto.this, reqCode, config);
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
                final ImageView view = new ImageView(TakePhoto.this);
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
                    add.setVisibility(View.VISIBLE);
                }
                if(photos.size()==8){
                    add.setVisibility(View.GONE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),""+path, Toast.LENGTH_LONG).show();
                        final Dialog settingsDialog = new Dialog(TakePhoto.this);
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
                        ImageView close_window = (ImageView) view1.findViewById(R.id.close_window);
                        close_window.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                settingsDialog.dismiss();
                            }
                        });
                        ImageView delete_this_item = (ImageView) view1.findViewById(R.id.delete_this_item);
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
}
