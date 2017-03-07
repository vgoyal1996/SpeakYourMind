package com.example.vipul.speakyourmind;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static com.example.vipul.speakyourmind.MyUserHandleActivity.UID;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment1 extends Fragment {


    public BlankFragment1() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Bundle b = getActivity().getIntent().getExtras();
        final ArrayList<StatusModel> modelList = getArguments().getParcelableArrayList(MyUserHandleActivity.ARRAYLIST);
        //final String uid = b.getString(MyUserHandleActivity.UID);
        View view = inflater.inflate(R.layout.fragment_blank_fragment1, container, false);
        RecyclerView userStatusView = (RecyclerView) view.findViewById(R.id.status_recycler_view);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        userStatusView.setBackground(bitmapDrawable);
        final EditText updateStatusEditText = (EditText)view.findViewById(R.id.update_status_editText);
        Button updateButton = (Button)view.findViewById(R.id.update_button);
        updateStatusEditText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        updateButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        Collections.sort(modelList,new StatusComparator());
        final StatusViewAdapter adapter = new StatusViewAdapter(view.getContext(),modelList,null);
        userStatusView.setLayoutManager(new LinearLayoutManager(getContext()));
        userStatusView.setAdapter(adapter);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(UID);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = updateStatusEditText.getText().toString().trim();
                if(!text.equals("")){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String date = sdf.format(new Date());
                    StatusModel newModel = new StatusModel(text,date);
                    //StatusModel newModel2  = new StatusModel(text,date,uid);
                    reference.child("statusList").push().setValue(newModel);
                    modelList.add(newModel);
                    adapter.notifyDataSetChanged();
                    updateStatusEditText.setText("");
                }
            }
        });
        return view;
    }

}
