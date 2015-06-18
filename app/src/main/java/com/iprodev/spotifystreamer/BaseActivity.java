package com.iprodev.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by curtis on 6/18/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SpotifyService mSpotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
    }

    private void initService() {
        mSpotifyService =  new SpotifyApi().getService();
    }

    protected SpotifyService getService() {
        return mSpotifyService;
    }
}
