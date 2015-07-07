package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_NAME;
import static com.iprodev.spotifystreamer.frags.TracksFragment.ARTIST_ID;

import kaaes.spotify.webapi.android.models.Artist;

public class MainActivity extends BaseActivity implements SearchFragment.SearchCallbacks {

    public static final String TAG = "MainActivity";

    private SearchFragment mSearchFrag;
    private String mQueryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            mSearchFrag = SearchFragment.getInstance(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, mSearchFrag, "SEARCH")
                    .commit();
        } else {
            mQueryString = savedInstanceState.getString("mQueryString");
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
//        loadSearchData("Foo Fighters"); //TODO: TEST DATA
    }

    @Override
    protected void setHandlers() {

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
        Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
        intent.putExtra(ARTIST_NAME, artist.name);
        intent.putExtra(ARTIST_ID, artist.id);
        startActivity(intent);


        //TODO: Load the top tracks fragment in two pane mode.
//        TracksFragment tracksFrag = TracksFragment.getInstance(getService(),artist.name,artist.id);
//        getSupportFragmentManager().beginTransaction()
//                .remove(mSearchFrag)
//                .add(R.id.main_container, tracksFrag, "TRACKS")
////                .replace(R.id.main_container, tracksFrag)
//                .commit();
    }

}