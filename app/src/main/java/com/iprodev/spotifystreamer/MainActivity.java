package com.iprodev.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.iprodev.spotifystreamer.com.iprodev.spotifystreamer.model.ArtistsAdaper;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";

    private ArtistsAdaper mResultsAdapter;
    private ArrayList<Artist> mResults;
    private ListView mResultsList;
    private ImageView mSpotIcon;
    private String mQueryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
            mQueryString = savedInstanceState.getString("mQueryString");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResults = new ArrayList<Artist>();
        mResultsList = (ListView) findViewById(R.id.results_artists_list);
        mSpotIcon = (ImageView)findViewById(R.id.spot_icon);
        setHandlers();
        if(mQueryString != null)
            loadSearchData(mQueryString);
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

    private void setHandlers() {
        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = (Artist) adapterView.getAdapter().getItem(i);
                Log.d(TAG, "artist id: " + artist.id);
                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra("artist_name", artist.name);
                intent.putExtra("artist_id", artist.id);
                startActivity(intent);
            }
        });
    }

    private void loadSearchData(final String search) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ArtistsPager results = getService().searchArtists(search);
                Log.d(TAG, results.toString());
                if (mResults == null) {
                    mResults = new ArrayList<Artist>();
                } else {
                    mResults.clear();
                }
                mResults.addAll(results.artists.items);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mResults.size() > 0) {
                    if(mResultsAdapter == null) {
                        mResultsAdapter = new ArtistsAdaper(MainActivity.this, mResults);
                        mResultsList.setAdapter(mResultsAdapter);
                    }
                    mResultsAdapter.notifyDataSetChanged();
                }
                updateUI(false);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"onQueryTextSubmit clicked with: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                if(searchText.length() >= 3) {
                    mQueryString = searchText;
                    loadSearchData(searchText);
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
                updateUI(true);
                mResults.clear();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(boolean clearUI) {
        View noResults = (View) findViewById(R.id.noresults_text);
        if (!clearUI) {
            mSpotIcon.setVisibility(View.INVISIBLE);
            noResults.setVisibility((mResults.size() == 0) ? View.VISIBLE : View.INVISIBLE);
            mResultsList.setVisibility((mResults.size() == 0) ? View.INVISIBLE : View.VISIBLE);
        } else {
            mSpotIcon.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }
}