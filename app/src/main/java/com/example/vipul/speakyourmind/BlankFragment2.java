package com.example.vipul.speakyourmind;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment2 extends Fragment {


    public BlankFragment2() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
        TextView handleUserName = (TextView)view.findViewById(R.id.handle_user_name);
        TextView handleEmail = (TextView)view.findViewById(R.id.handle_email);
        TextView handlePhone = (TextView)view.findViewById(R.id.handle_phone);
        TextView userNameText = (TextView)view.findViewById(R.id.name_textView);
        TextView emailText = (TextView)view.findViewById(R.id.email_textView);
        TextView phoneText = (TextView)view.findViewById(R.id.phone_textView);
        handleUserName.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        handleEmail.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        handlePhone.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        userNameText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        emailText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        phoneText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Aller_It.ttf"));
        UserModel model = (UserModel) getArguments().getSerializable(MyUserHandleActivity.INFO);
        userNameText.setText(model.getUserName());
        emailText.setText(model.getEmail());
        phoneText.setText(model.getPhone());
        return view;
    }

}
