package com.example.user.yaadafy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.user.yaadafy.MainActivity;
import com.example.user.yaadafy.R;
import com.example.user.yaadafy.Reminder;
import com.example.user.yaadafy.Users;
import com.example.user.yaadafy.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by USER on 1/31/2016.
 */
public class PeopleList extends Fragment {

    private FriendAdapter mFriendAdapter;
    private static final String LOG_TAG = "SOMETHING_GOOD";
    private ListView mListView;
    private Reminder mShoppingList;
    private String mListId;
    private Firebase mActiveListRef, mSharedWithRef;
    private ValueEventListener mActiveListRefListener, mSharedWithListener;
    private HashMap<String, Users> mSharedWithUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_people_list,container, false);
        FloatingActionButton mFab = (FloatingActionButton) rootView.findViewById(R.id.search_friend);
        mListView = (ListView) rootView.findViewById(R.id.list_view_friends_share);
        final SharedPreferences mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),AddFriendActivity.class);
                startActivity(i);
            }
        });

        Intent intent = getActivity().getIntent();
        mListId = intent.getStringExtra(Constants.KEY_LIST_ID);
        if (mListId == null)
            mListId = "text";

        /**
         * Create Firebase references
         */
        Firebase currentUserFriendsRef = new Firebase(Constants.FIREBASE_URL_USER_FRIENDS).child(MainActivity.mEncodedEmail);
        mActiveListRef = new Firebase(Constants.FIREBASE_URL_REMINDER_LISTS).child(MainActivity.mEncodedEmail).child(mListId);
        mSharedWithRef = new Firebase (Constants.FIREBASE_URL_LISTS_SHARED_WITH).child(mListId);

        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reminder shoppingList = dataSnapshot.getValue(Reminder.class);

                if (shoppingList != null) {
                    Gson gson = new Gson();
                    String json = mPrefs.getString(Constants.KEY_OBJECT_ID, "");
                    Reminder obj = gson.fromJson(json, Reminder.class);
                    obj = shoppingList;
                    mFriendAdapter.setShoppingList(obj);
                } else {
                    Log.e(LOG_TAG,"It was null");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });


        mSharedWithListener = mSharedWithRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSharedWithUsers = new HashMap<String, Users>();
                for (DataSnapshot currentUser : dataSnapshot.getChildren()) {
                    mSharedWithUsers.put(currentUser.getKey(), currentUser.getValue(Users.class));
                }
                mFriendAdapter.setSharedWithUsers(mSharedWithUsers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });

        mFriendAdapter = new FriendAdapter(getActivity(), Users.class,
                R.layout.single_user_item, currentUserFriendsRef, mListId);

        /* Set adapter for the listView */
        mListView.setAdapter(mFriendAdapter);

        return rootView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Set adapter for the listView */
        //mFriendAdapter.cleanup();
        //mActiveListRef.removeEventListener(mActiveListRefListener);
        mSharedWithRef.removeEventListener(mSharedWithListener);
    }


}
