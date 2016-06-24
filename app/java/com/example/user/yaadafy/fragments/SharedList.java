package com.example.user.yaadafy.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.user.yaadafy.FirebaseRecyclerAdapter;
import com.example.user.yaadafy.MainActivity;
import com.example.user.yaadafy.R;
import com.example.user.yaadafy.Reminder;
import com.example.user.yaadafy.StandardNotification;
import com.example.user.yaadafy.Users;
import com.example.user.yaadafy.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by USER on 2/10/2016.
 */
public class SharedList extends Fragment {

    private final static String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";
    private Firebase ref;
    private FirebaseRecyclerAdapter<Reminder, ReminderListViewHolder> mAdapter;
    Reminder mReminder;
    String mListId;
    HashMap<String, Users> mSharedWithUsers;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private String mRepeatNo;
    private String mRepeatType;
    private String mTime;
    private String mDate;

    private Query mQuery;

    private ArrayList<Reminder> mAdapterItems;
    private ArrayList<String> mAdapterKeys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shared_list, container, false);

        setupRecyclerview(rootView);

        return rootView;
    }

    // Restoring the item list and the keys of the items: they will be passed to the adapter
    private void handleInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_ADAPTER_ITEMS) &&
                savedInstanceState.containsKey(SAVED_ADAPTER_KEYS)) {
            mAdapterItems = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_ADAPTER_ITEMS));
            mAdapterKeys = savedInstanceState.getStringArrayList(SAVED_ADAPTER_KEYS);
        } else {
            mAdapterItems = new ArrayList<Reminder>();
            mAdapterKeys = new ArrayList<String>();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleInstanceState(savedInstanceState);

        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(Constants.FIREBASE_URL_REMINDER_LISTS).child(MainActivity.mEncodedEmail);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /*private void setupFirebase() {
        Firebase.setAndroidContext(getActivity());
        String firebaseLocation = Constants.FIREBASE_URL_REMINDER_LISTS;
        mQuery = new Firebase(firebaseLocation).child(MainActivity.mEncodedEmail);
    }*/

    private void setupRecyclerview(View mView) {
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setAdapter(mMyAdapter);

        mAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderListViewHolder>(Reminder.class, R.layout.recycle_item_firebase, ReminderListViewHolder.class, ref) {

            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;


            @Override
            protected void populateViewHolder(ReminderListViewHolder viewHolder, Reminder model, int position) {
                viewHolder.titleText.setText(model.getTitle());
                viewHolder.ownerText.setText(model.getActive());
                viewHolder.dateTimeText.setText(model.getDate() + " " + model.getTime());
                final String itemToRemoveId = this.getRef(position).getKey();

                //Experimental Code to utilize the final approach
                //int ID = rb.addReminder(new Reminder(model.getTitle(),model.getDate(),model.getTime(),"null","null","null",model.getActive()));

                mCalendar.set(Calendar.MONTH, --mMonth);
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);

                //new AlarmReceiver().setAlarmAnother(getActivity().getApplicationContext(), mCalendar);

                Intent intent=new Intent(getActivity(),StandardNotification.class);
                AlarmManager manager=(AlarmManager)getActivity().getSystemService(Activity.ALARM_SERVICE);
                PendingIntent pendingIntent= PendingIntent.getService(getActivity(),
                        0, intent, 0);
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.MONTH, --mMonth);
                cal.set(Calendar.YEAR, mYear);
                cal.set(Calendar.DAY_OF_MONTH, mDay);
                cal.set(Calendar.HOUR_OF_DAY, mHour);
                cal.set(Calendar.MINUTE,mMinute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);

                /*viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog)
                                .setTitle(getActivity().getString(R.string.remove_item_option))
                                .setMessage(getActivity().getString(R.string.dialog_message_are_you_sure_remove_item))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });*/



                String title = model.getTitle();
                String letter = "A";

                if(title != null && !title.isEmpty()) {
                    letter = title.substring(0, 1);
                }

                int color = mColorGenerator.getRandomColor();

                // Create a circular icon consisting of  a random background colour and first letter of title
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound(letter, color);
                viewHolder.mThumbnailImage.setImageDrawable(mDrawableBuilder);

            }

        };
        recyclerView.setAdapter(mAdapter);
    }

    private void removeItem(String itemId) {

        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

        /* Make a map for the removal */
        HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();

        /* Remove the item by passing null */
        updatedRemoveItemMap.put("/" + Constants.FIREBASE_LOCATION_REMINDER_LISTS + "/"
                + mListId + "/" + itemId, null);


        /* Do the update */
        firebaseRef.updateChildren(updatedRemoveItemMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mMyAdapter.destroy();
    }

}
