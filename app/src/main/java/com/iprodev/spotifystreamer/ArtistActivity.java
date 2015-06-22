package com.iprodev.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iprodev.spotifystreamer.com.iprodev.spotifystreamer.model.TracksAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
