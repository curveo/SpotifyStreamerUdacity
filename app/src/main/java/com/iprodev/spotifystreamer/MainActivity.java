package com.iprodev.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iprodev.spotifystreamer.com.iprodev.spotifystreamer.model.ArtistsAdaper;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.transform.Result;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";

    private ArtistsAdaper mResultsAdapter;
    private ArrayList<Artist> mResults;
    private ListView mResultsList;
    private EditText mSearchText;
    private Button mClearSearchBtn;
    private ImageView mSpotIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResults = new ArrayList<Artist>();
        mClearSearchBtn = (Button) findViewById(R.id.search_main_clear_btn);
        mSearchText = (EditText) findViewById(R.id.search_main);
        mResultsList = (ListView) findViewById(R.id.results_artists_list);
        mSpotIcon = (ImageView)findViewById(R.id.spot_icon);
        setHandlers();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadSearchData("Foo Fighters");
    }

    private void setHandlers() {
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* Not implemented */}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { /* Not implemented */}

            @Override
            public void afterTextChanged(Editable editable) {
                String input = mSearchText.getText().toString();
                //Enable the clear button if there is any text.
                mClearSearchBtn.setVisibility((input.length() > 0) ? View.VISIBLE : View.GONE);
                //Only search if there are 3 characters or more.
                if(input.length() >= 3) {
                    loadSearchData(input);
                }
            }
        });
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    loadSearchData(v.getText().toString());
//                    handled = true;
//                }
//                return handled;
//            }
//        });
        mClearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(true);
                mResults.clear();
                mResultsAdapter.notifyDataSetChanged();
            }
        });

        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = (Artist) adapterView.getAdapter().getItem(i);
                Log.d(TAG, "artist id: " + artist.id);
                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra("artist_name", artist.name);
                intent.putExtra("artist_id", artist.id);
                startActivity(intent);

                //TODO: launch top ten activity

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public class SpotifySearchResult {
        private String mThumbURL;
        private String mArtistName;

        public SpotifySearchResult(String thumb, String artist) {
            mThumbURL = thumb;
            mArtistName = artist;
        }


    }
    private void updateUI(boolean clearUI) {
        View noResults = (View) findViewById(R.id.noresults_text);
        if (!clearUI) {
            mSpotIcon.setVisibility(View.GONE);
            noResults.setVisibility((mResults.size() == 0) ? View.VISIBLE : View.GONE);
            mResultsList.setVisibility((mResults.size() == 0) ? View.GONE : View.VISIBLE);
        } else {
            mSearchText.setText("");
            mClearSearchBtn.setVisibility(View.GONE);
            mSpotIcon.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }

    }
}