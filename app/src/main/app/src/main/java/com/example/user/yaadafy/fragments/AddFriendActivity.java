package com.example.user.yaadafy.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.example.user.yaadafy.MainActivity;
import com.example.user.yaadafy.R;
import com.example.user.yaadafy.Users;
import com.example.user.yaadafy.utils.Constants;
import com.firebase.client.Firebase;

/**
 * Represents the Add Friend screen and functionality
 */
public class AddFriendActivity extends MainActivity {
    private EditText mEditTextAddFriendEmail;
    private AutocompleteFriendAdapter mFriendsAutocompleteAdapter;
    private String mInput;
    private ListView mListViewAutocomplete;
    private Firebase mUsersRef;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        /**
         * Create Firebase references
         */
        mUsersRef = new Firebase(Constants.FIREBASE_URL_USERS);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /**
         * Set interactive bits, such as click events/adapters
         */

        mEditTextAddFriendEmail.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
                /* Get the input after every textChanged event and transform it to lowercase */
            mInput = mEditTextAddFriendEmail.getText().toString().toLowerCase();

            /* Clean up the old adapter */
            if (mFriendsAutocompleteAdapter != null) mFriendsAutocompleteAdapter.cleanup();
            /* Nullify the adapter data if the input length is less than 2 characters */
            if (mInput.equals("") || mInput.length() < 2) {
                mListViewAutocomplete.setAdapter(null);

            /* Define and set the adapter otherwise. */
            } else {
                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(AddFriendActivity.this, Users.class,
                        R.layout.single_autocomplete_item, mUsersRef.orderByChild(Constants.FIREBASE_PROPERTY_EMAIL)
                        .startAt(mInput).endAt(mInput + "~").limitToFirst(5), mEncodedEmail);

                mListViewAutocomplete.setAdapter(mFriendsAutocompleteAdapter);
            }

        }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFriendsAutocompleteAdapter != null) {
            mFriendsAutocompleteAdapter.cleanup();
        }
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        mListViewAutocomplete = (ListView) findViewById(R.id.list_view_friends_autocomplete);
        mEditTextAddFriendEmail = (EditText) findViewById(R.id.edit_text_add_friend_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
