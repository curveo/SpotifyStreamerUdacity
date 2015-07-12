package com.iprodev.spotifystreamer.view;

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

    @Override
    protected void onResume() {
        super.onResume();
        setHandlers();
    }

    private void initService() {
        mSpotifyService =  new SpotifyApi().getService();
    }

    protected SpotifyService getService() {
        return mSpotifyService;
    }

    /**
     * Implement to set the click and touch event handlers
     */
    protected abstract void setHandlers();
}
