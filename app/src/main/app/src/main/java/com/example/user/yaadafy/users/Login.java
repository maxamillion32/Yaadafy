package com.example.user.yaadafy.users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.yaadafy.MainActivity;
import com.example.user.yaadafy.R;
import com.example.user.yaadafy.Users;
import com.example.user.yaadafy.appIntro.OnBoardScreenActivity;
import com.example.user.yaadafy.utils.Constants;
import com.example.user.yaadafy.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2/5/2016.
 */
public class Login extends MainActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REGISTER_REQUEST_CODE = 1;
    private static final String LOG_TAG = Login.class.getSimpleName();
    protected Firebase.AuthStateListener mAuthListener;
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* References to the Firebase */
    private Firebase mFirebaseRef;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;
    private EditText mEditTextEmailInput, mEditTextPasswordInput;
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mSharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        TextView mTextView = (TextView) findViewById(R.id.textView2);
        Typeface mTextCustom = Typeface.createFromAsset(getAssets(),"fonts/Baron.ttf");
        mTextView.setTypeface(mTextCustom);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        initializeScreen();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());


                boolean isFirstStart = getPrefs.getBoolean("firstStart",true);

                if (isFirstStart) {

                    Intent i = new Intent(Login.this, OnBoardScreenActivity.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();

                    e.putBoolean("firstStart",false);

                    e.apply();

                }

            }
        });

        t.start();


    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * This is the authentication listener that maintains the current user session
         * and signs in automatically on application launch
         */
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.dismiss();

                /**
                 * If there is a valid session to be restored, start MainActivity.
                 * No need to pass data via SharedPreferences because app
                 * already holds userName/provider data from the latest session
                 */
                if (authData != null) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        /* Add auth listener to Firebase ref */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);

        /**
         * Get the newly registered user email if present, use null as default value
         */
        //String signupEmail = mSharedPref.getString(Constants.KEY_SIGNUP_EMAIL, null);

        /**
         * Fill in the email editText and remove value from SharedPreferences if email is present
         */

    }

    public void initializeScreen() {
        mEditTextEmailInput = (EditText) findViewById(R.id.input_emails);
        mEditTextPasswordInput = (EditText) findViewById(R.id.input_passwords);
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        /* Setup Google Sign In */
        setupGoogleSignIn();
    }

    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInPassword() {
        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        /**
         * If email and password are not empty show progress dialog and try to authenticate
         */
        if (email.equals("")) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.equals("")) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(email, password, new MyAuthResultHandler(Constants.PASSWORD_PROVIDER));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onSignInPressed(View view) {
        signInPassword();
    }

    public void onSignUpPressed(View view) {
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
    }


    private class MyAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;
        String unprocessedEmail;

        public MyAuthResultHandler(String provider) {
            this.provider = provider;
        }

        /**
         * On successful authentication call setAuthenticatedUser if it was not already
         * called in
         */
        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            Log.i(LOG_TAG, provider + " " + getString(R.string.log_message_auth_successful));

            if (authData != null) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor spe = sp.edit();
                /**
                 * If user has logged in with Google provider
                 */
                if (authData.getProvider().equals(Constants.PASSWORD_PROVIDER)) {
                    setAuthenticatedUserPasswordProvider(authData);
                } else
                /**
                 * If user has logged in with Password provider
                 */
                    if (authData.getProvider().equals(Constants.GOOGLE_PROVIDER)) {
                        setAuthenticatedUserGoogle(authData);
                    } else {
                        Log.e(LOG_TAG, getString(R.string.log_error_invalid_provider) + authData.getProvider());
                    }

                /* Save provider name and encodedEmail for later use and start MainActivity */
                spe.putString(Constants.KEY_PROVIDER, authData.getProvider()).apply();
                spe.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail).apply();

                /* Go to main activity */
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();

            /**
             * Use utility method to check the network connection state
             * Show "No network connection" if there is no connection
             * Show Firebase specific error message otherwise
             */
            switch (firebaseError.getCode()) {
                case FirebaseError.INVALID_EMAIL:
                case FirebaseError.USER_DOES_NOT_EXIST:
                    mEditTextEmailInput.setError(getString(R.string.error_message_email_issue));
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    mEditTextPasswordInput.setError(firebaseError.getMessage());
                    break;
                case FirebaseError.NETWORK_ERROR:
                    showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                    break;
                default:
                    showErrorToast(firebaseError.toString());
            }
        }
    }

    private void loginWithGoogle(String token) {
        mFirebaseRef.authWithOAuthToken(Constants.GOOGLE_PROVIDER, token, new MyAuthResultHandler(Constants.GOOGLE_PROVIDER));
    }

    private void showErrorToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
    }

    private void setAuthenticatedUserPasswordProvider(AuthData authData) {

        final String unprocessedEmail = authData.getProviderData().get(Constants.FIREBASE_PROPERTY_EMAIL).toString().toLowerCase();
        /**
         * Encode user email replacing "." with ","
         * to be able to use it as a Firebase db key
         */
        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

    }

    /**
     * Helper method that makes sure a user is created if the user
     * logs in with Firebase's Google login provider.
     *
     * @param authData AuthData object returned from onAuthenticated
     */
    private void setAuthenticatedUserGoogle(final AuthData authData) {
        /**
         * If google api client is connected, get the lowerCase user email
         * and save in sharedPreferences
         */
        final String unprocessedEmail;
        if (mGoogleApiClient.isConnected()) {
            unprocessedEmail = mGoogleAccount.getEmail().toLowerCase();
            mSharedPrefEditor.putString(Constants.KEY_GOOGLE_EMAIL, unprocessedEmail).apply();
        } else {

            /**
             * Otherwise get email from sharedPreferences, use null as default value
             * (this mean that user resumes his session)
             */
            unprocessedEmail = mSharedPref.getString(Constants.KEY_GOOGLE_EMAIL, null);
        }

        /**
         * Encode user email replacing "." with "," to be able to use it
         * as a Firebase db key
         */
        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

        /* Get username from authData */
        final String userName = (String) authData.getProviderData().get(Constants.PROVIDER_DATA_DISPLAY_NAME);

        /* Make a user */
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(Utils.encodeEmail(unprocessedEmail));

        HashMap<String, Object> userAndUidMapping = new HashMap<String, Object>();

        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /* Create a HashMap version of the user to add */
        Users newUser = new Users(userName, Utils.encodeEmail(unprocessedEmail), timestampJoined);
        HashMap<String, Object> newUserMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newUser, Map.class);

        /* Add the user and UID to the update map */
        userAndUidMapping.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + Utils.encodeEmail(unprocessedEmail),
                newUserMap);
        userAndUidMapping.put("/" + Constants.FIREBASE_LOCATION_UID_MAPPINGS + "/"
                + authData.getUid(),Utils.encodeEmail(unprocessedEmail));

        /* Update the database; it will fail if a user already exists */
        mFirebaseRef.updateChildren(userAndUidMapping, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    /* Try just making a uid mapping */
                    mFirebaseRef.child(Constants.FIREBASE_LOCATION_UID_MAPPINGS)
                            .child(authData.getUid()).setValue(Utils.encodeEmail(unprocessedEmail));
                }
            }
        });
    }

    /* Sets up the Google Sign In Button : https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton */
    private void setupGoogleSignIn() {
        //SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        //signInButton.setSize(SignInButton.SIZE_WIDE);
        /*signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });*/
    }

    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();

    }

    protected void logout() {

        /* Logout if mProvider is not null */
        if (mProvider != null) {
            mFirebaseRef.unauth();

            if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {

                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                //nothing
                            }
                        });
            }
        }
    }

    private void takeUserToLoginScreenOnUnAuth() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            /* Signed in successfully, get the OAuth token */
            mGoogleAccount = result.getSignInAccount();
            getGoogleOAuthTokenAndLogin();


        } else {
            if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                showErrorToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
            } else {
                showErrorToast("Error handling the sign in: " + result.getStatus().getStatusMessage());
            }
            mAuthProgressDialog.dismiss();
        }
    }

    /**
     * Gets the GoogleAuthToken and logs in.
     */
    private void getGoogleOAuthTokenAndLogin() {
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String mErrorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format(getString(R.string.oauth2_format), new Scope(Scopes.PROFILE)) + " email";

                    token = GoogleAuthUtil.getToken(Login.this, mGoogleAccount.getEmail(), scope);
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(LOG_TAG, getString(R.string.google_error_auth_with_google) + transientEx);
                    mErrorMessage = getString(R.string.google_error_network_error) + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(LOG_TAG, getString(R.string.google_error_recoverable_oauth_error) + e.toString());

                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(LOG_TAG, " " + authEx.getMessage(), authEx);
                    mErrorMessage = getString(R.string.google_error_auth_with_google) + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mAuthProgressDialog.dismiss();
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */
                    loginWithGoogle(token);
                } else if (mErrorMessage != null) {
                    showErrorToast(mErrorMessage);
                }
            }
        };

        task.execute();
    }

}
