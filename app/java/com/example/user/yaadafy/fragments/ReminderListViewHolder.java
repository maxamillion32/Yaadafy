package com.example.user.yaadafy.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.yaadafy.R;

/**
 * Created by USER on 2/14/2016.
 */
public class ReminderListViewHolder extends RecyclerView.ViewHolder {

    TextView titleText,dateTimeText,ownerText;
    ImageView mThumbnailImage;
    ImageView mDeleteButton;

    public ReminderListViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.recycle_title_fb);
        dateTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time_fb);
        ownerText = (TextView) itemView.findViewById(R.id.owner_name_fb);
        mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image_fb);
        //mDeleteButton = (ImageView) itemView.findViewById(R.id.delete_image_fb);
    }


}
