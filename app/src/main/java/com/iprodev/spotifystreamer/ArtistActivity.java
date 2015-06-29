package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.iprodev.spotifystreamer.com.iprodev.spotifystreamer.model.TracksAdapter;

import java.util.ArrayList;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistActivity extends BaseActivity {

    private ArrayList<Track> mTracks;
    private ListView mTracksListView;
    private TracksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String artistName = i.getStringExtra("artist_name");
        String artitsId = i.getStringExtra("artist_id");
        Log.d("ArtistActivity", "onCreate artist_name: " + artistName + ", artist_id: " + artitsId);

        setContentView(R.layout.activity_artist);
        mTracksListView = (ListView) findViewById(R.id.results_artists_tracks_list);
        loadTracks(artitsId);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(artistName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    private void loadTracks(String artistId) {
        new AsyncTask<String, Void, Tracks>() {
            @Override
            protected Tracks doInBackground(String... artists) {
                TreeMap<String,Object> params = new TreeMap<String, Object>();
                params.put("country", "US");
                final Tracks tracks = getService().getArtistTopTrack(artists[0], params);
                return tracks;
            }

            @Override
            protected void onPostExecute(Tracks tracks) {
                if(tracks != null) {
                    if(mTracks == null)
                        mTracks = new ArrayList<Track>();
                    mTracks.addAll(tracks.tracks);
                    if(mAdapter == null)
                        mAdapter = new TracksAdapter(ArtistActivity.this, mTracks);
                    mTracksListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute(artistId);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
