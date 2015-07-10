package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.iprodev.spotifystreamer.frags.SearchFragment;
import com.iprodev.spotifystreamer.frags.TracksFragment;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_ID;
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_NAME;

public class MainActivity extends BaseActivity implements SearchFragment.SearchCallbacks, TracksFragment.TracksFragCallback {

    public static final String TAG = "MainActivity";
    public static final String TRACKS_FRAG = "TracksFragment";

    private SearchFragment mSearchFrag;
    private String mQueryString;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       if(savedInstanceState != null)
            mQueryString = savedInstanceState.getString("mQueryString");

        setContentView(R.layout.activity_main);
        mSearchFrag = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        mSearchFrag.setCallbacks(this);

        if(findViewById(R.id.toptracks_container) != null) {
            isTablet = true;
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.toptracks_container, TracksFragment.getInstance(getService(),this), TRACKS_FRAG)
                        .commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mQueryString", mQueryString);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Consider optimizing network calls here.
        if(mQueryString != null)
            mSearchFrag.loadSearchData(getService(), mQueryString);
//        loadSearchData("Foo Fighters"); //TODO: TEST DATA
    }

    @Override
    protected void setHandlers() {
        //TODO: perhaps not needed now with fragments.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchViewCustom searchView = (SearchViewCustom) MenuItemCompat.getActionView(searchItem);
        searchView.setCallback(new SearchViewCustom.SearchViewCallback() {
            @Override
            public void onCollapsed() {
                //TODO: Way to handle when the search Menu is collapsed in two pane mode
//                Fragment frag = getSupportFragmentManager().findFragmentByTag("TRACKS");
//                mSearchFrag.updateUI(true);
//                getSupportFragmentManager().beginTransaction()
//                    .remove(frag)
//                    .add(R.id.main_container,mSearchFrag)
////                    .replace(R.id.main_container, mSearchFrag)
//                    .commit();
            }
        });
        searchView.setQueryHint(getString(R.string.search_hint));
//        if(mQueryString != null) searchView.setQuery(mQueryString, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit clicked with: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                if (searchText.length() >= 3) {
                    mQueryString = searchText;
                    mSearchFrag.loadSearchData(getService(), searchText);
                    return true;
                }
                return false;
            }
        });
        //Capture the clear text event to properly handle the ui updates.
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.search_src_text);
                text.setText("");
                searchView.setQuery("", false);
                mSearchFrag.updateUI(true);
                if(isTablet) {
                    TracksFragment track = TracksFragment.getInstance(getService(),MainActivity.this);
                    track.restTracks();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this,"No settings yet, implement me!",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onArtistSelected(Artist artist) {
        Log.d(TAG, "artist id: " + artist.id);
        if(!isTablet) {
            Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
            intent.putExtra(ARTIST_NAME, artist.name);
            intent.putExtra(ARTIST_ID, artist.id);
            startActivity(intent);
        } else {
            TracksFragment track = TracksFragment.getInstance(getService(),this);
            track.loadFragData(getService(), artist.name, artist.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.toptracks_container, track, TRACKS_FRAG)
                    .commit();
//            TracksFragment track = (TracksFragment) getSupportFragmentManager().findFragmentByTag(TRACKS_FRAG);
//            track.loadFragData(getService(), artist.name, artist.id);
        }


        //TODO: Load the top tracks fragment in two pane mode.
//        TracksFragment tracksFrag = TracksFragment.getInstance(getService(),artist.name,artist.id);
//        getSupportFragmentManager().beginTransaction()
//                .remove(mSearchFrag)
//                .add(R.id.main_container, tracksFrag, "TRACKS")
////                .replace(R.id.main_container, tracksFrag)
//                .commit();
    }

    @Override
    public void onTrackSelected(Track track) {
        String audioUrl = track.preview_url;
        String albumName = track.album.name;
        List<Image> images = track.album.images;
        String trackName = track.name;
        String prevURL = track.preview_url;
        Log.d(TAG, "albumname: " + albumName + ", images_count: " + images.size() + ", track_name: " + trackName + ", preview_URL: " + prevURL);
        //TODO: Launch player with the above meta data.
    }

    @Override
    public void onNoTracksAvailable() {
        //TODO: maybe put focus on the listview in two pane mode?
    }
}