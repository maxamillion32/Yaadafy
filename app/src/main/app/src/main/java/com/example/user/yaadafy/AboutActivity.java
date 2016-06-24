package com.example.user.yaadafy;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by USER on 2/22/2016.
 */
public class AboutActivity extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView myEmailId = (TextView) findViewById(R.id.my_email);
        myEmailId.setText("rohanoid5@gmail.com");
        Linkify.addLinks(myEmailId, Linkify.EMAIL_ADDRESSES);

        TextView sayanEmailId = (TextView) findViewById(R.id.sayan_email);
        sayanEmailId.setText("banerjeesayan.1995@gmail.com");
        Linkify.addLinks(sayanEmailId, Linkify.EMAIL_ADDRESSES);

        TextView roshniEmailId = (TextView) findViewById(R.id.roshni_email);
        roshniEmailId.setText("roshni.n95@gmail.com");
        Linkify.addLinks(roshniEmailId, Linkify.EMAIL_ADDRESSES);

        TextView mTextView = (TextView) findViewById(R.id.about_header);
        Typeface mTextCustom = Typeface.createFromAsset(getAssets(),"fonts/Baron.ttf");
        mTextView.setTypeface(mTextCustom);

        TextView mTextView2 = (TextView) findViewById(R.id.firebase_name);
        Typeface mTextCustom2 = Typeface.createFromAsset(getAssets(),"fonts/Baron.ttf");
        mTextView2.setTypeface(mTextCustom2);

    }
}
