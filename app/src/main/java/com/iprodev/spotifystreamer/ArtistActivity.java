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
    private ResultsAdapter mAdapter;

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
                        mAdapter = new ResultsAdapter(ArtistActivity.this, mTracks);
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

    public static class ResultsAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Track> mResults;

        public ResultsAdapter(Context c, ArrayList<Track> results) {
            mContext = c;
            mResults = results;
        }

        @Override
        public int getCount() {
            return mResults.size();
        }

        @Override
        public Track getItem(int position) {
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
                convertView = inflator.inflate(R.layout.row_artist_track, parent, false);
                holder = new ViewHolder();
                holder.mImageV = (ImageView) convertView.findViewById(R.id.result_artist_thumb);
                holder.mTrackName = (TextView) convertView.findViewById(R.id.track_name);
                holder.mTrackAlbumName = (TextView) convertView.findViewById(R.id.track_album_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Track track = getItem(position);


            if(track.album.images.size() > 0 ) {
                Image thumb = null;
                for(Image thumbUrl: track.album.images) {
                    //TODO conditionally grab the correct url based on size.
                    if(thumbUrl.height >= 100 && thumbUrl.height <= 300) {
                        thumb = thumbUrl;
                    }
                }
//                new AsyncTask<String, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(String... strings) {
//                        String url = strings[0];
//                        try {
//                            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//                            InputStream in = new BufferedInputStream(conn.getInputStream());
//                            byte[] buffer = new byte[1024];
//                            while(in.read() != -1) {
//
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                    }
//                }.execute(result.images.get(0).url);

                Picasso.with(mContext).load(thumb.url).into(holder.mImageV);
            } else {
                holder.mImageV.setImageResource(R.drawable.artist_placeholder);
            }
            holder.mTrackName.setTag(track); //TODO: Dont think we need this..
            holder.mTrackName.setText(track.name);
            holder.mTrackAlbumName.setText(track.album.name);

            return convertView;
        }

        class ViewHolder {
            ImageView mImageV;
            TextView mTrackName;
            TextView mTrackAlbumName;
        }
    }
}
