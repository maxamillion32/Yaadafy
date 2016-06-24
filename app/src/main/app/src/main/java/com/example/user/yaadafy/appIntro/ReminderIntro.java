package com.example.user.yaadafy.appIntro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.yaadafy.R;

/**
 * Created by USER on 2/19/2016.
 */
public class ReminderIntro extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reminder_intro, container, false);

        TextView mTextView = (TextView) rootView.findViewById(R.id.textView21);
        Typeface mTextCustom = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Baron.ttf");
        mTextView.setTypeface(mTextCustom);

        return rootView;
    }

}