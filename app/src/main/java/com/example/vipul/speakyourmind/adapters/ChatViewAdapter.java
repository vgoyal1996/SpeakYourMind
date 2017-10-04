package com.example.vipul.speakyourmind.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.activity.ChatActivity;
import com.example.vipul.speakyourmind.fragment.ChatFragment;
import com.example.vipul.speakyourmind.fragment.ChatFragment.OnListFragmentInteractionListener;
import com.example.vipul.speakyourmind.other.CircleTransformation;
import com.example.vipul.speakyourmind.other.PicassoCache;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;


public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private HashMap<String,String> profilePic;

    public ChatViewAdapter(Context context, List<String> items,HashMap<String,String> profilePic, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.profilePic = profilePic;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = ChatFragment.personIds.get(mValues.get(position));
        holder.mIdView.setText(holder.mItem);
        //PicassoCache.getPicassoInstance(context).load(profilePic.get(mValues.get(position))).resize(width,height).centerCrop().transform(new CircleTransformation()).into(holder.mProfilPic);
        //holder.mContentView.setText(mValues.get(position).content);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(profilePic.get(mValues.get(position)));
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int width = holder.mProfilePic.getDrawable().getIntrinsicWidth();
                int height = holder.mProfilePic.getDrawable().getIntrinsicHeight();
                PicassoCache.getPicassoInstance(context).load(uri).resize(width,height).centerCrop().transform(new CircleTransformation()).into(holder.mProfilePic);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(ChatActivity.PERSON_POS,mValues.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        private final ImageView mProfilePic;
        //public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.chat_user_name);
            mProfilePic = (ImageView)view.findViewById(R.id.chat_profic_pic);
           // mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
