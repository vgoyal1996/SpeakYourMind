package com.example.vipul.speakyourmind.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.comparators.ChatComparator;
import com.example.vipul.speakyourmind.fragment.ChatFragment;
import com.example.vipul.speakyourmind.fragment.FeedFragment;
import com.example.vipul.speakyourmind.model.ChatModel;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class ChatActivity extends AppCompatActivity {

    private ChatView mChatView;
    public static final String PERSON_POS = "pos";
    String recipient_id;
    String sender_id;
    int f=0;
    //ArrayList<ChatModel> conversation = new ArrayList<>();
    Set<ChatModel> conversations = new TreeSet<>(new ChatComparator());
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatView = (ChatView)findViewById(R.id.chat_view);
        recipient_id = (String)getIntent().getExtras().get(PERSON_POS);
        sender_id = FeedFragment.USER_UID;
        mChatView.setBackgroundResource(R.drawable.back);
        mChatView.setSendButtonColor(Color.parseColor("#E74C3C"));
        mChatView.setDateSeparatorColor(Color.parseColor("#17202A"));
        mChatView.setSendTimeTextColor(Color.parseColor("#17202A"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        TextView myText = (TextView)findViewById(R.id.mytext);
        myText.setText(ChatFragment.personIds.get(recipient_id));
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference().child("Messages");
        //Query queryRef = reference.orderByChild("senderId").equalTo(sender_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(f==0) {
                    Iterable<DataSnapshot> results = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : results) {
                        HashMap<String, String> hm = (HashMap<String, String>) snapshot.getValue();
                        if ((hm.get("receiverId").equals(recipient_id) && hm.get("senderId").equals(sender_id)) || (hm.get("receiverId").equals(sender_id) && hm.get("senderId").equals(recipient_id))) {
                            ChatModel model = new ChatModel(hm.get("senderId"), hm.get("receiverId"), hm.get("message"), hm.get("timestamp"));
                            //conversation.add(model);
                            conversations.add(model);
                        }
                    }
                    //Collections.sort(conversation, new ChatComparator());
                    for (ChatModel model : conversations) {
                        Message chatMsg;
                        Date date = null;
                        try {
                            date = sdf.parse(model.getTimestamp());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        if (model.getSenderId().equals(sender_id)) {
                            chatMsg = new Message.Builder()
                                    .setUser(new User(0, ChatFragment.personIds.get(model.getSenderId()), BitmapFactory.decodeResource(getResources(), R.drawable.face_2)))
                                    .setRightMessage(true)
                                    .setCreatedAt(cal)
                                    .hideIcon(true)
                                    .setUsernameVisibility(false)
                                    .setMessageText(model.getMessage())
                                    .build();
                            mChatView.send(chatMsg);
                        } else {
                            chatMsg = new Message.Builder()
                                    .setUser(new User(1, ChatFragment.personIds.get(model.getSenderId()), BitmapFactory.decodeResource(getResources(), R.drawable.face_1)))
                                    .setRightMessage(false)
                                    .setCreatedAt(cal)
                                    .hideIcon(true)
                                    .setUsernameVisibility(false)
                                    .setMessageText(model.getMessage())
                                    .build();
                            mChatView.receive(chatMsg);
                        }
                    }
                    f = 1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                if(f==1) {
                    HashMap<String,String> temp = (HashMap<String, String>) dataSnapshot.getValue();
                    if(temp.get("senderId").equals(recipient_id)&&temp.get("receiverId").equals(sender_id)){
                        ChatModel m = new ChatModel(temp.get("senderId"),temp.get("receiverId"),temp.get("message"),temp.get("timestamp"));
                        //conversation.add(m);
                        boolean isAdded = conversations.add(m);
                        if(isAdded) {
                            Date date = null;
                            try {
                                date = sdf.parse(m.getTimestamp());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            Message message = new Message.Builder()
                                    .setUser(new User(1, ChatFragment.personIds.get(recipient_id), BitmapFactory.decodeResource(getResources(), R.drawable.face_1)))
                                    .setRightMessage(false)
                                    .setUsernameVisibility(false)
                                    .hideIcon(true)
                                    .setCreatedAt(cal)
                                    .setMessageText(m.getMessage())
                                    .hideIcon(true)
                                    .build();
                            //Set to chat view
                            mChatView.send(message);
                            //Reset edit text
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new message
                String date = sdf.format(new Date());
                ChatModel m = new ChatModel(sender_id,recipient_id,mChatView.getInputText(),date);
                String key = reference.push().getKey();
                reference.child(key).setValue(m);
                //conversation.add(m);
                conversations.add(m);
                Message message = new Message.Builder()
                        .setUser(new User(0,ChatFragment.personIds.get(sender_id),BitmapFactory.decodeResource(getResources(), R.drawable.face_2)))
                        .setRightMessage(true)
                        .setUsernameVisibility(false)
                        .hideIcon(true)
                        .setCreatedAt(Calendar.getInstance())
                        .setMessageText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text
                mChatView.setInputText("");
            }
        });
    }
}
