package com.example.user.yaadafy.appIntro;

import android.content.Intent;
import android.os.Bundle;

import com.example.user.yaadafy.users.Login;
import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by USER on 2/19/2016.
 */
public class OnBoardScreenActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

        ReminderIntro reminderIntro = new ReminderIntro();
        AddFriendIntro addFriendIntro = new AddFriendIntro();
        ShareReminderIntro shareReminderIntro = new ShareReminderIntro();

        addSlide(reminderIntro);
        addSlide(addFriendIntro);
        addSlide(shareReminderIntro);

        showStatusBar(false);

    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }

    private void loadMainActivity(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

}
