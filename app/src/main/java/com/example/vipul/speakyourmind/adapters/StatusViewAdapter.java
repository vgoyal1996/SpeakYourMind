package com.example.vipul.speakyourmind.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.fragment.FeedFragment;
import com.example.vipul.speakyourmind.model.CommentModel;
import com.example.vipul.speakyourmind.model.GalleryModel;
import com.example.vipul.speakyourmind.model.LikeModel;
import com.example.vipul.speakyourmind.model.MessageKeyModel;
import com.example.vipul.speakyourmind.model.StatusModel;
import com.example.vipul.speakyourmind.model.UserModel;
import com.example.vipul.speakyourmind.other.CustomFilter;
import com.example.vipul.speakyourmind.other.PicassoCache;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private CommentAdapter adapter;
    public static HashMap<String,GalleryModel> galleryModelHashMap = new HashMap<>();
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
    public void onBindViewHolder(final StatusViewAdapter.StatusViewHolder holder, final int position) {
        holder.statusText.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Aller_It.ttf"));
        holder.userText.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Aller_It.ttf"));
        final String uid = statusModels.get(position).getUid();
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
        if(!uid.equals(FeedFragment.USER_UID)){
            holder.userText.setText(users.get(uid).getUserName());
        }
        else{
            holder.userText.setText("You");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.card_view.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
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
            int startID = 0;
            for(int i=0;i<size;i++){
                StorageReference storageRef = reference.child(uid+"/"+parts[0]+"/photo"+i+".png/");
                final ImageView imageView = new ImageView(context);
                imageView.setId(View.generateViewId());
                if(i==0)
                    startID=imageView.getId();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(220, 220);
                params.setMargins(10,10,10,10);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        PicassoCache.getPicassoInstance(context).load(uri).into(imageView);
                    }
                });
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.photoLinearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Intent intent = new Intent(context,GalleryPopUpActivity.class);
                        //TODO pass values in intent to GalleryPopUpActivity
                         Drawable id = imageView.getDrawable();
                        GalleryModel m = null;
                        for(Map.Entry<String,GalleryModel> map : galleryModelHashMap.entrySet()){
                            m = map.getValue();
                            if(m.getStartingID()<=id) {
                                m.setClickedID(id);
                                break;
                            }
                        }
                        intent.putExtra(GalleryPopUpActivity.CLICKED_ID,m.getClickedID());
                        intent.putExtra(GalleryPopUpActivity.NUMBER_OF_IMAGES,m.getGallerySize());
                        intent.putExtra(GalleryPopUpActivity.STARTING_ID,m.getStartingID());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);*/
                        //Activity activity = (Activity) context;
                        //activity.overridePendingTransition(R.anim.profile_dialog_grow,0);
                    }
                });
            }
            GalleryModel galleryModel = new GalleryModel(startID,-1,size);
            galleryModelHashMap.put(parts[0],galleryModel);
            holder.statusText.setVisibility(View.INVISIBLE);
            holder.statusText.setEnabled(false);
        }
        final List<LikeModel> likeModelList;
        if(statusModels.get(position).getLikeList()==null)
            likeModelList = new ArrayList<>();
        else
            likeModelList = statusModels.get(position).getLikeList();
        int likeCount=0;
        if(likeModelList!=null) {
            for (int i = 0; i < likeModelList.size(); i++) {
                if (likeModelList.get(i).isLiked())
                    likeCount++;
                if(likeModelList.get(i).getUserUid().equals(FeedFragment.USER_UID))
                    holder.likeButton.setLiked(true);
            }
        }
        /*if(likeCount!=0)
            holder.likeButton.setText("LIKE "+likeCount);*/
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                    listener.onItemClick(holder.getAdapterPosition());
            }
        });
        final DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setLiked(true);
                LikeModel model = null;
                for (int i = 0; i < likeModelList.size(); i++) {
                    if (likeModelList.get(i).getUserUid().equals(FeedFragment.USER_UID)) {
                        model = likeModelList.get(i);
                        model.setLiked(true);
                        likeModelList.set(i, model);
                        break;
                    }
                }
                if (model != null)
                    dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(model.getLikeUid()).child("liked").setValue(String.valueOf(model.isLiked()));
                else {
                    String likeUid = dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").push().getKey();
                    model = new LikeModel(FeedFragment.USER_UID, true, likeUid);
                    LikeModel modelForDb = new LikeModel(FeedFragment.USER_UID, true);
                    dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(likeUid).setValue(modelForDb);
                    likeModelList.add(model);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeButton.setLiked(false);
                LikeModel model = null;
                for (int i = 0; i < likeModelList.size(); i++) {
                    if (likeModelList.get(i).getUserUid().equals(FeedFragment.USER_UID)) {
                        model = likeModelList.get(i);
                        model.setLiked(false);
                        likeModelList.set(i, model);
                        break;
                    }
                }
                //if (model != null)
                dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(model.getLikeUid()).child("liked").setValue(model.isLiked());
                /*else {
                    String likeUid = dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").push().getKey();
                    model = new LikeModel(MainActivity.USER_UID, true, likeUid);
                    LikeModel modelForDb = new LikeModel(MainActivity.USER_UID, true);
                    dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(likeUid).setValue(modelForDb);
                    likeModelList.add(model);
                }*/
            }
        });
        /*holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arr = holder.likeButton.getText().toString().split(" ");
                int c;
                if(arr.length!=1)
                    c = Integer.parseInt(arr[1]);
                else
                    c = 0;
                LikeModel model = null;
                for (int i = 0; i < likeModelList.size(); i++) {
                    if (likeModelList.get(i).getUserUid().equals(MainActivity.USER_UID)) {
                        model = likeModelList.get(i);
                        if (model.isLiked()) {
                            model.setLiked(false);
                            c--;
                        } else {
                            model.setLiked(true);
                            c++;
                        }
                        likeModelList.set(i, model);
                        break;
                    }
                }
                if (model != null)
                    dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(model.getLikeUid()).child("liked").setValue(String.valueOf(model.isLiked()));
                else {
                    String likeUid = dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").push().getKey();
                    model = new LikeModel(MainActivity.USER_UID, true, likeUid);
                    LikeModel modelForDb = new LikeModel(MainActivity.USER_UID, true);
                    dbReference.child(uid + "/statusList/" + messageKeys.get(position).getMessageKey()).child("likes").child(likeUid).setValue(modelForDb);
                    likeModelList.add(model);
                    c++;
                }
                if(c!=0)
                    holder.likeButton.setText("LIKE "+c);
                else
                    holder.likeButton.setText("LIKE");
            }
        });*/
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.commentView.getVisibility()==View.GONE) {
                    List<CommentModel> commentModelList = statusModels.get(position).getCommentList();
                    holder.commentView.setVisibility(View.VISIBLE);
                    holder.commentLayout.setVisibility(View.VISIBLE);
                    if(commentModelList!=null){
                        adapter = new CommentAdapter(context,commentModelList);
                        LinearLayoutManager manager = new LinearLayoutManager(context);
                        holder.commentView.setLayoutManager(manager);
                        holder.commentView.setAdapter(adapter);
                    }
                }
                else{
                    holder.commentView.setVisibility(View.GONE);
                    holder.commentLayout.setVisibility(View.GONE);
                }
            }
        });
        holder.updateCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = holder.writeCommentEditText.getText().toString();
                if(TextUtils.isEmpty(comment))
                    return;
                String commentUid = dbReference.child(uid+"/statusList/"+messageKeys.get(position).getMessageKey()).child("comments").push().getKey();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());
                CommentModel modelForApp = new CommentModel(FeedFragment.DISPLAY_NAME,date,comment);
                dbReference.child(uid+"/statusList/"+messageKeys.get(position).getMessageKey()).child("comments").child(commentUid).setValue(modelForApp);
                List<CommentModel> commentModelList = statusModels.get(position).getCommentList();
                if(commentModelList!=null) {
                    commentModelList.add(modelForApp);
                }
                else{
                    commentModelList = new ArrayList<>();
                    commentModelList.add(modelForApp);
                }
                statusModels.get(position).setCommentList(commentModelList);
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
                else{
                    adapter = new CommentAdapter(context,commentModelList);
                    LinearLayoutManager manager = new LinearLayoutManager(context);
                    holder.commentView.setLayoutManager(manager);
                    holder.commentView.setAdapter(adapter);
                }
                holder.writeCommentEditText.setText("");
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
        private LikeButton likeButton;
        private Button commentButton;
        private RecyclerView commentView;
        private LinearLayout commentLayout;
        private EditText writeCommentEditText;
        private Button updateCommentButton;

        public StatusViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mainLayout = (RelativeLayout)view.findViewById(R.id.relative_card);
            photoLinearLayout = (LinearLayout)view.findViewById(R.id.photo_linear_layout);
            statusText = (TextView)view.findViewById(R.id.status_text);
            userText = (TextView)view.findViewById(R.id.user_name);
            card_view = (CardView) view.findViewById(R.id.card_view);
            date_time = (TextView) view.findViewById(R.id.date_time);
            likeButton = (LikeButton) view.findViewById(R.id.like_button);
            commentButton = (Button) view.findViewById(R.id.comment_button);
            commentView = (RecyclerView)view.findViewById(R.id.comment_recycler_view);
            commentLayout = (LinearLayout)view.findViewById(R.id.comment_layout);
            writeCommentEditText = (EditText)view.findViewById(R.id.write_comment_editText);
            updateCommentButton = (Button)view.findViewById(R.id.update_comment_button);
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
