package com.iprodev.spotifystreamer;

import android.os.Bundle;

import com.iprodev.spotifystreamer.frags.PlayerFragment;

/**
 * Created by curtis on 7/8/15.
 */
public class PlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        //Get the Player instance and set the extras.
        PlayerFragment frag = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_container);
        frag.setExtras(extras);

        String car = "http://www.podtrac.com/pts/redirect.mp3/traffic.libsyn.com/theadamcarollashow/2015-07-08acs_2015-07-07-200824-7770-0-8-0.64k.mp3";
        String foo = "http://p.scdn.co/mp3-preview/d3b0c09d22f6b9685345c10a347ef2624413fb85";
        frag.startAudio(extras.getString(PlayerFragment.PREVIEW_URL));
//        preview https://p.scdn.co/mp3-preview/d3b0c09d22f6b9685345c10a347ef2624413fb85
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setHandlers() {

    }
}
