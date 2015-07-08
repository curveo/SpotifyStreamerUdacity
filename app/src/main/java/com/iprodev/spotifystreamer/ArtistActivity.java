package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iprodev.spotifystreamer.frags.TracksFragment;

import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_ID;
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_NAME;


public class ArtistActivity extends BaseActivity {
    private static final String TAG = "ArtistActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String artistName = i.getStringExtra(ARTIST_NAME);
        String artitsId = i.getStringExtra(ARTIST_ID);
        Log.d("ArtistActivity", "onCreate artist_name: " + artistName + ", artist_id: " + artitsId);

        setContentView(R.layout.activity_artist);
        ((TracksFragment)getSupportFragmentManager().findFragmentById(R.id.toptracks_fragment))
                .loadFragData(getService(), artistName, artitsId);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(artistName);
        actionBar.setDisplayHomeAsUpEnabled(true);

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
