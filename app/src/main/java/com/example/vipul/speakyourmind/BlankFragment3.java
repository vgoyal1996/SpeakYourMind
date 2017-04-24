package com.example.vipul.speakyourmind;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment3 extends Fragment {


    public BlankFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ArrayList<StatusModel> modelList = getArguments().getParcelableArrayList(MyUserHandleActivity.ARRAYLIST);
        final ArrayList<MessageKeyModel> messageList = getArguments().getParcelableArrayList(MyUserHandleActivity.MESSAGE_KEYS);
        //final String uid = b.getString(MyUserHandleActivity.UID);
        View view = inflater.inflate(R.layout.fragment_blank_fragment3, container, false);
        RecyclerView userStatusView = (RecyclerView) view.findViewById(R.id.status_recycler_view_3);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        userStatusView.setBackground(bitmapDrawable);
        //final EditText updateStatusEditText = (EditText)view.findViewById(R.id.update_status_editText);
        //Button updateButton = (Button)view.findViewById(R.id.update_button);
        Collections.sort(modelList,new StatusComparator());
        Collections.sort(messageList, new MessageKeyComparator());
        final StatusViewAdapter adapter = new StatusViewAdapter(view.getContext(),modelList,MainActivity.users,messageList);
        userStatusView.setLayoutManager(new LinearLayoutManager(getContext()));
        userStatusView.setAdapter(adapter);
        return view;
    }

}
