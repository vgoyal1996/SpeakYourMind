package com.example.vipul.speakyourmind.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.adapters.ChatViewAdapter;
import com.example.vipul.speakyourmind.other.SimpleDividerItemDecoration;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    public static HashMap<String,String> personIds = new HashMap<>();
    private View view;
    private HashMap<String,String> personProfilePic = new HashMap<>();

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.chat_layout);
        layout.setBackground(bitmapDrawable);
        Firebase ref = new Firebase("https://speakyourmind-d0d3a.firebaseio.com/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new GetUsersTask().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    class GetUsersTask extends AsyncTask<DataSnapshot,Void,ArrayList<String>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(DataSnapshot... params) {
            Iterable<DataSnapshot> uids = params[0].getChildren();
            ArrayList<String> person_ids = new ArrayList<>();
            for(DataSnapshot child : uids){
                HashMap<String, String> values = (HashMap<String, String>) child.getValue();
                if(child.getKey().equals("Messages")||child.getKey().equals("notifications"))
                    continue;
                if(!child.getKey().equals(FeedFragment.USER_UID)) {
                    personIds.put(child.getKey(), values.get("userName"));
                    person_ids.add(child.getKey());
                }
                personProfilePic.put(child.getKey(),child.getKey() + "/" + FeedFragment.users.get(child.getKey()).getEmail() + ".jpg");
            }
            return person_ids;
        }

        @Override
        protected void onPostExecute(ArrayList<String> persons) {
            super.onPostExecute(persons);
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(new ChatViewAdapter(getActivity().getApplicationContext(),persons,personProfilePic, mListener));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }
}
