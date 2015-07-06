package com.iprodev.spotifystreamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iprodev.spotifystreamer.model.TracksAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistActivity extends BaseActivity {

    private static final String TAG = "ArtistActivity";

    //Metadata constants
    public static final String ARTIST_NAME = "artist_name";
    public static final String ARTIST_ID = "artist_id";

    private ArrayList<Track> mTracks;
    private ListView mTracksListView;
    private TracksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String artistName = i.getStringExtra(ARTIST_NAME);
        String artitsId = i.getStringExtra(ARTIST_ID);
        Log.d("ArtistActivity", "onCreate artist_name: " + artistName + ", artist_id: " + artitsId);

        setContentView(R.layout.activity_artist);
        mTracksListView = (ListView) findViewById(R.id.results_artists_tracks_list);
        loadTracks(artitsId);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(artistName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void setHandlers() {
        mTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ArtistActivity.this,"I'm a player, Implement Me!", Toast.LENGTH_LONG).show();
                Track track = mAdapter.getItem(position);
                String albumName = track.album.name;
                List<kaaes.spotify.webapi.android.models.Image> images = track.album.images;
                String trackName = track.name;
                String prevURL = track.preview_url;
                Log.d(TAG, "albumname: " + albumName + ", images_count: " + images.size() + ", track_name: " + trackName + ", preview_URL: " + prevURL);
                //TODO: Launch player with the above meta data.
            }
        });
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
                //Must be included else api call will fail! This should probably be localized.
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
                } else {
                    new AlertDialog.Builder(ArtistActivity.this)
                        .setIcon(R.drawable.spotify_icon)
                        .setTitle(getString(R.string.no_tracks))
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ArtistActivity.this.onBackPressed();
                                    }
                                })
                        .show();
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
                Toast.makeText(this,"No settings yet, implement me!",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
