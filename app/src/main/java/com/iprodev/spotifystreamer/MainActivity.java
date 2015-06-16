package com.iprodev.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.xml.transform.Result;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ResultsAdapter mResultsAdapter;
    private ArrayList<Artist> mResults;
    private ListView mResultsList;

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        return super.onKeyUp(keyCode, event);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText mSearchText = (EditText) findViewById(R.id.search_main);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadSearchData(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

//        mSearchText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                String input = ((EditText)v).getText().toString();
//                if(input.length() > 1) {
//                    loadSearchData(input);
//                }
//                return false;
//            }
//        });
        mResultsList = (ListView) findViewById(R.id.results_artists_list);
//        loadSearchData("Foo Fighters");
    }

    private void loadSearchData(final String search) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(search);
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
                        mResultsAdapter = new ResultsAdapter(MainActivity.this, mResults);
                        mResultsList.setAdapter(mResultsAdapter);
                    }
                    mResultsAdapter.notifyDataSetChanged();


                } else {
                    //TODO: Set the list to "no results
                }
                updateUI();
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

    private void updateUI() {
        findViewById(R.id.noresults_text).setVisibility((mResults.size() == 0) ? View.VISIBLE:View.GONE);
        mResultsList.setVisibility((mResults.size() == 0) ? View.GONE:View.VISIBLE);
    }

    public static class ResultsAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Artist> mResults;

        public ResultsAdapter(Context c, ArrayList<Artist> results) {
            mContext = c;
            mResults = results;
        }

        @Override
        public int getCount() {
            return mResults.size();
        }

        @Override
        public Artist getItem(int position) {
            return mResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           //ViewHolder design patter for scroll performance.
            ViewHolder holder;
            if(convertView == null) {
                LayoutInflater inflator = LayoutInflater.from(mContext);
                convertView = inflator.inflate(R.layout.row_result, parent, false);
                holder = new ViewHolder();
                holder.mImageV = (ImageView) convertView.findViewById(R.id.result_artist_thumb);
                holder.mArtist = (TextView) convertView.findViewById(R.id.result_artist_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Artist result = getItem(position);
            if(result.images.size() > 0 ) {
                for(Image thumbUrl: result.images) {
                    //TODO conditionaly grab the correct url based on size.
                }
                Picasso.with(mContext).load(result.images.get(0).url).into(holder.mImageV);
            }
            holder.mArtist.setText(result.name);

            return convertView;
        }

        class ViewHolder {
            ImageView mImageV;
            TextView mArtist;
        }
    }
}