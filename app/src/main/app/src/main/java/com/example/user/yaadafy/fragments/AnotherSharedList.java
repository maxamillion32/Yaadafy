package com.example.user.yaadafy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.yaadafy.MainActivity;
import com.example.user.yaadafy.R;
import com.example.user.yaadafy.utils.Constants;
import com.firebase.client.Firebase;

import java.util.Calendar;

/**
 * Created by USER on 2/23/2016.
 */
public class AnotherSharedList extends Fragment {

    private Firebase ref;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private String mRepeatNo;
    private String mRepeatType;
    private String mTime;
    private String mDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_another_shared_list, container, false);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


}
