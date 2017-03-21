package com.example.vipul.speakyourmind;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.StatusViewHolder> implements Filterable {
    private Context context;
    private List<StatusModel>statusModels;
    private Map<String,UserModel> users;
    private List<MessageKeyModel> messageKeys;
    private RecyclerViewItemClickListener listener;
    private CustomFilter filter;
    private StorageReference reference;
    private static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private static final String[] WEEK_DAYS = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    public void setListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public static interface RecyclerViewItemClickListener{
        void onItemClick(int position);
    }

    public StatusViewAdapter(Context context,List<StatusModel> statusModels, Map<String, UserModel> users) {
        this.context = context;
        this.statusModels = statusModels;
        this.users = users;
    }

    public StatusViewAdapter(Context context,List<StatusModel> statusModels, Map<String, UserModel> users, List<MessageKeyModel> messageKeys) {
        this.context = context;
        this.statusModels = statusModels;
        this.users = users;
        this.messageKeys = messageKeys;
    }

    public List<MessageKeyModel> getMessageKeys() {
        return messageKeys;
    }

    public void setMessageKeys(List<MessageKeyModel> messageKeys) {
        this.messageKeys = messageKeys;
    }

    public void setStatusModels(List<StatusModel> statusModels) {
        this.statusModels = statusModels;
    }

    public List<StatusModel> getStatusModels() {
        return statusModels;
    }

    public void setUsers(Map<String, UserModel> users) {
        this.users = users;
    }

    public StatusViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public StatusViewAdapter.StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cv = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_layout,parent,false);
        return new StatusViewAdapter.StatusViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final StatusViewAdapter.StatusViewHolder holder, int position) {
        holder.statusText.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Aller_It.ttf"));
        holder.userText.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Aller_It.ttf"));
        String uid = statusModels.get(position).getUid();
        String creationDate = statusModels.get(position).getCreationDateAndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar c = null;
        try{
            Date d = sdf.parse(creationDate);
            c = Calendar.getInstance();
            c.setTime(d);

        }catch (ParseException e){
            e.printStackTrace();
        }
        holder.card_view.setBackgroundColor(Color.parseColor("#E3F2FD"));
        String date = WEEK_DAYS[c.get(Calendar.DAY_OF_WEEK)-1]+","+c.get(Calendar.DAY_OF_MONTH)+" "+MONTH_NAMES[c.get(Calendar.MONTH)]+" "+c.get(Calendar.YEAR);
        if(users!=null) {
            if(!uid.equals(MainActivity.USER_UID)){
                holder.userText.setText(users.get(uid).getUserName());
            }
            else{
                holder.userText.setText("You");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.card_view.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        }
        else {
            holder.userText.setText("");
        }
        holder.date_time.setText("posted on "+date);
        String msg = statusModels.get(position).getMessage();
        if(!msg.contains(messageKeys.get(position).getMessageKey())) {
            holder.photoLinearLayout.setVisibility(View.INVISIBLE);
            holder.statusText.setText(statusModels.get(position).getMessage());
        }
        else{
            String parts[] = msg.split(" ");
            reference = FirebaseStorage.getInstance().getReference();
            int size = Integer.parseInt(parts[1]);
            for(int i=0;i<size;i++){
                StorageReference storageRef = reference.child(uid+"/"+parts[0]+"/photo"+i+".png/");
                ImageView imageView = new ImageView(context);
                imageView.setId(i);
                //imageView.setPadding(10, 10, 10, 10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(220, 220);
                params.setMargins(10,10,10,10);
                Glide.with(context).using(new FirebaseImageLoader()).load(storageRef).into(imageView);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.photoLinearLayout.addView(imageView);
            }
            holder.statusText.setVisibility(View.INVISIBLE);
            holder.statusText.setEnabled(false);
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                    listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return statusModels.size();
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private RelativeLayout mainLayout;
        private LinearLayout photoLinearLayout;
        private TextView statusText,userText,date_time;
        private CardView card_view;

        public StatusViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mainLayout = (RelativeLayout)view.findViewById(R.id.relative_card);
            photoLinearLayout = (LinearLayout)view.findViewById(R.id.photo_linear_layout);
            statusText = (TextView)view.findViewById(R.id.status_text);
            userText = (TextView)view.findViewById(R.id.user_name);
            card_view = (CardView) view.findViewById(R.id.card_view);
            date_time = (TextView) view.findViewById(R.id.date_time);
        }
    }
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(this,statusModels);
        }
        return filter;
    }

}
