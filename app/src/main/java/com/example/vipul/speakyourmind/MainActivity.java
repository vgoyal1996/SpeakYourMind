package com.example.vipul.speakyourmind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    public static String USER_UID;
    public static String DISPLAY_NAME;
    public static String CURRENT_USER;
    private SearchView sv;
    //private Emojicon
    private EditText updateStatusEditText;
    private RecyclerView statusView;
    private static List<StatusModel> statuses;
    private StatusViewAdapter adapter;
    private static Map<String,UserModel> users;
    private TextView myText;
    private ImageView search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference reference;
    private List<MessageKeyModel> messageKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
            }
        );
        auth = FirebaseAuth.getInstance();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.main_linear_layout);
        mainLayout.setBackground(bitmapDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        myText = (TextView)findViewById(R.id.mytext);
        USER_UID = auth.getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        statusView = (RecyclerView)findViewById(R.id.status_view);
        sv= (SearchView) findViewById(R.id.msearch1);
        //search = (ImageView) findViewById(R.id.search);
        updateStatusEditText = (EditText) findViewById(R.id.update_status_editText);
        updateStatusEditText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        Button updateButton = (Button) findViewById(R.id.update_button_main);
        ImageButton addPhotoButton = (ImageButton)findViewById(R.id.add_photos_status_button);
        updateButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Aller_It.ttf"));
        /*updateStatusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStatusEditText.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = updateStatusEditText.getText().toString().trim();
                if(!text.equals("")){
                    setTextStatus(text);
                    updateStatusEditText.setText("");
                }
            }
        });
        //setEmojiconFragment(false);
        statuses = new ArrayList<>();
        users = new HashMap<>();
        adapter = new StatusViewAdapter(getApplicationContext());
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                //Toast.makeText(getApplicationContext(),""+query,Toast.LENGTH_LONG).show();
                return false;
            }
        });
        Firebase ref = new Firebase("https://speakyourmind-d0d3a.firebaseio.com/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new UpdateUITask().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        adapter.setListener(new StatusViewAdapter.RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this,MyUserHandleActivity.class);
                String clickedUid = statuses.get(position).getUid();
                intent.putExtra(MyUserHandleActivity.USER,users.get(clickedUid));
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                overridePendingTransition(R.anim.dialog_in,0);
            }
        });
    }


    class UpdateUITask extends AsyncTask<DataSnapshot,Void,List<StatusModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<StatusModel> doInBackground(DataSnapshot... dataSnapshots) {
            messageKeys = new ArrayList<>();
            Iterable<DataSnapshot> uids = dataSnapshots[0].getChildren();
            for(DataSnapshot child : uids){
                HashMap<String,String> values = (HashMap<String,String>)child.getValue();
                UserModel user = new UserModel(child.getKey(),values.get("userName"),values.get("email"),values.get("password"),values.get("phone"));
                List<StatusModel> userStatuses = new ArrayList<>();
                List<MessageKeyModel> individualMessageKeys = new ArrayList<>();
                if(values.containsKey("statusList")) {
                    Object o = values.get("statusList");
                    HashMap<String,HashMap<String,String> > map = (HashMap<String, HashMap<String,String>>)o;
                    for(Map.Entry<String,HashMap<String,String> > m : map.entrySet()){
                        HashMap<String,String> temp = m.getValue();
                        messageKeys.add(new MessageKeyModel(m.getKey(),temp.get("creationDateAndTime")));
                        individualMessageKeys.add(new MessageKeyModel(m.getKey(),temp.get("creationDateAndTime")));
                        List<CommentModel> statusComments = null;
                        List<LikeModel> statusLikes = null;
                        if(temp.containsKey("comments")){
                            statusComments = new ArrayList<>();
                            Object commentObject = temp.get("comments");
                            HashMap<String,HashMap<String,String> > commentMap = (HashMap<String, HashMap<String,String>>)commentObject;
                            for(Map.Entry<String,HashMap<String,String> > comMap : commentMap.entrySet()){
                                HashMap<String,String> cMap = comMap.getValue();
                                statusComments.add(new CommentModel(cMap.get("userName"),cMap.get("dateOfComment"),cMap.get("comment")));
                            }
                        }
                        if(temp.containsKey("likes")){
                            statusLikes = new ArrayList<>();
                            Object commentObject = temp.get("likes");
                            HashMap<String,HashMap<String,Object> > likeMap = (HashMap<String, HashMap<String,Object>>)commentObject;
                            for(Map.Entry<String,HashMap<String,Object> > lMap : likeMap.entrySet()){
                                HashMap<String,Object> getLikeMap = lMap.getValue();
                                Boolean isLiked = (Boolean) getLikeMap.get("liked");
                                statusLikes.add(new LikeModel((String) getLikeMap.get("userUid"),isLiked,lMap.getKey()));
                            }
                        }
                        userStatuses.add(new StatusModel(temp.get("message"),temp.get("creationDateAndTime"),statusComments,statusLikes));
                        statuses.add(new StatusModel(temp.get("message"),temp.get("creationDateAndTime"),child.getKey(),statusComments,statusLikes));
                    }
                    Collections.sort(statuses,new StatusComparator());
                    Collections.sort(messageKeys, new MessageKeyComparator());
                }
                user.setMessageKeyModelList(individualMessageKeys);
                user.setStatusModelList(userStatuses);
                users.put(child.getKey(),user);
            }
            return statuses;
        }

        @Override
        protected void onPostExecute(List<StatusModel> statusModels) {
            super.onPostExecute(statusModels);
            swipeRefreshLayout.setRefreshing(false);
            CURRENT_USER = users.get(USER_UID).getUserName();
            //myText.setText(users.get(USER_UID).getUserName());
            DISPLAY_NAME = auth.getCurrentUser().getDisplayName();
            myText.setText(DISPLAY_NAME);
            adapter.setStatusModels(statusModels);
            adapter.setUsers(users);
            adapter.setMessageKeys(messageKeys);
            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
            statusView.setLayoutManager(manager);
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this,R.anim.list_layout_controller);//R.anim.list_layout_controller
            statusView.setLayoutAnimation(controller);
            statusView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_button:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setTextStatus(String text){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());
        StatusModel newModel = new StatusModel(text,date,USER_UID);
        StatusModel newModel2 = new StatusModel(text,date);
        String updateUid = reference.child(USER_UID).child("statusList").push().getKey();
        messageKeys.add(new MessageKeyModel(updateUid,date));
        MessageKeyModel uid = new MessageKeyModel(updateUid,date);
        UserModel userModel = users.get(USER_UID);
        List<MessageKeyModel> message = userModel.getMessageKeyModelList();
        if(message!=null){
            message.add(uid);
            userModel.setMessageKeyModelList(message);
        }
        else{
            message = new ArrayList<>();
            message.add(uid);
            userModel.setMessageKeyModelList(message);
        }
        addToStatuses(newModel,newModel2);
        reference.child(USER_UID).child("statusList").child(updateUid).setValue(newModel2);
        adapter.notifyDataSetChanged();
    }

    public static void addToStatuses(StatusModel listModel,StatusModel firebaseModel){
        statuses.add(listModel);
        UserModel userModel = users.get(USER_UID);
        List<StatusModel> list = userModel.getStatusModelList();
        if(list!=null) {
            list.add(firebaseModel);
            userModel.setStatusModelList(list);
        }
        else{
            list = new ArrayList<>();
            list.add(firebaseModel);
            userModel.setStatusModelList(list);
        }
        users.remove(USER_UID);
        users.put(USER_UID,userModel);
    }

}
