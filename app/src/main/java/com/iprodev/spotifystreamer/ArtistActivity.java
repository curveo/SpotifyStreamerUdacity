package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iprodev.spotifystreamer.frags.PlayerFragment;
import com.iprodev.spotifystreamer.frags.TracksFragment;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_ID;
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_NAME;


public class ArtistActivity extends BaseActivity implements TracksFragment.TracksFragCallback {
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
        frag.loadFragData(getService(), mArtistName, artitsId);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(mArtistName);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onTrackSelected(Track track) {
        String audioUrl = track.preview_url;

        String albumName = track.album.name;
        List<Image> images = track.album.images;
        String imageUrl = null;
        for(Image i : images) {
            if(i.height >= 300 ) {
                imageUrl = i.url;
                break;
            }
        }
        String trackName = track.name;
        String prevURL = track.preview_url;
        Log.d(TAG, "albumname: " + albumName + ", images_count: " + images.size() + ", track_name: " + trackName + ", preview_URL: " + prevURL);

        //TODO: Launch player with the above meta data.
        Intent i = new Intent(this, PlayerActivity.class);
        Bundle bnd = new Bundle();
        bnd.putString(PlayerFragment.ARTIST_NAME, mArtistName);
        bnd.putString(PlayerFragment.ALBUM_NAME, albumName);
        bnd.putString(PlayerFragment.IMAGE_URL, imageUrl);
        bnd.putString(PlayerFragment.TRACK_NAME, trackName);
        bnd.putString(PlayerFragment.PREVIEW_URL, prevURL);
        i.putExtras(bnd);
        startActivity(i);
    }

    @Override
    public void onNoTracksAvailable() {
        //Back when no tracks are found.
        onBackPressed();
    }

    @Override
    protected void setHandlers() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Lets use the activity backstack instead so the state of the search results is
                // maintained.
                onBackPressed();
                return true;
            case R.id.action_settings:
                Toast.makeText(this,"No settings yet, implement me!",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
