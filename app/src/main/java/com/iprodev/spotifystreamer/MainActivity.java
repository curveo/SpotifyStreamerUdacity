package com.iprodev.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private ResultsAdapter mResultsAdapter;
    private ArrayList<Artist> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView resultsList = (ListView) findViewById(R.id.results_artists_list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists("Beyonce");
                Log.d("MainActivity", results.toString());
                for(Artist al: results.artists.items) {
                    if(mResults == null) {
                        mResults = new ArrayList<Artist>();
                    }
                    mResults.add(al);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mResults.size() > 0) {
                            if(mResultsAdapter == null) {
                                mResultsAdapter = new ResultsAdapter(MainActivity.this, mResults);
                                resultsList.setAdapter(mResultsAdapter);
//                                resultsList.notify();

                            }

                        } else {
                            //TODO: Set the list to "no results
                        }

                    }
                });
//
            }
        }).start();
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