package com.iprodev.spotifystreamer.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.frags.PlayerFragment;
import com.iprodev.spotifystreamer.frags.TracksFragment;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_ID;
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_NAME;


public class ArtistActivity extends BaseActivity implements TracksFragment.TracksFragCallback, PlayerFragment.TransportCallbacks {
    private static final String TAG = "ArtistActivity";
    private String mArtistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        mArtistName = i.getStringExtra(ARTIST_NAME);
        String artitsId = i.getStringExtra(ARTIST_ID);
        Log.d("ArtistActivity", "onCreate artist_name: " + mArtistName + ", artist_id: " + artitsId);

        setContentView(R.layout.activity_artist);
        TracksFragment frag = (TracksFragment) getSupportFragmentManager().findFragmentById(R.id.toptracks_fragment);
        frag.setCallback(this);
        frag.loadFragData(getService(), artitsId);

        ActionBar actionBar = getSupportActionBar();
        if(mArtistName != null)
            actionBar.setSubtitle(mArtistName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayerFragment frag = (PlayerFragment) getSupportFragmentManager().findFragmentByTag(PlayerFragment.TAG);
        if (frag != null)
            frag.setCallback(this);
    }

    @Override
    public void onTrackSelected(Track track) {
        String albumName = track.album.name;
        List<Image> images = track.album.images;
        String imageUrl = null;
        for (Image i : images) {
            if (i.height >= 300) {
                imageUrl = i.url;
                break;
            }
        }
        String trackName = track.name;
        String prevURL = track.preview_url;
        Log.d(TAG, "albumname: " + albumName + ", images_count: " + images.size() + ", track_name: "
                + trackName + ", preview_URL: " + prevURL);

        Bundle bnd = new Bundle();
        bnd.putString(PlayerFragment.ARTIST_NAME, mArtistName);
        bnd.putString(PlayerFragment.ALBUM_NAME, albumName);
        bnd.putString(PlayerFragment.IMAGE_URL, imageUrl);
        bnd.putString(PlayerFragment.TRACK_NAME, trackName);
        bnd.putString(PlayerFragment.PREVIEW_URL, prevURL);

        //Initiate the PlayerFragment and add to backstack.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PlayerFragment prev = (PlayerFragment) getSupportFragmentManager().findFragmentByTag(PlayerFragment.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        //Get and show player.
        //TODO: Pull the callbacks up and implement next and previous track.
        final TracksFragment frag = (TracksFragment) getSupportFragmentManager().findFragmentById(R.id.toptracks_fragment);
        PlayerFragment playerFrag = PlayerFragment.getInstance(this, bnd, false);
        playerFrag.show(ft, PlayerFragment.TAG);
    }

    @Override
    public void onNoTracksAvailable() {
        //Back when no tracks are found.
        onBackPressed();
    }

    @Override
    protected void setHandlers() {
        /* nothing to do here */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public Track getPreviousTrack() {
        TracksFragment frag = (TracksFragment) getSupportFragmentManager().findFragmentById(R.id.toptracks_fragment);
        return frag.getPreviousTrack();
    }

    @Override
    public Track getNextTrack() {
        TracksFragment frag = (TracksFragment) getSupportFragmentManager().findFragmentById(R.id.toptracks_fragment);
        return frag.getNextTrack();
    }

    @Override
    public void onServiceError(Exception e) {
        showServiceError(e);
        onBackPressed();
    }

    @Override
    public void onMediaPlayerError(Exception e) {
        onServiceError(e);
    }


}
