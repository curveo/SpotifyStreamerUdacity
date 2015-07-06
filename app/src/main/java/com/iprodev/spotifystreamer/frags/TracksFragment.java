package com.iprodev.spotifystreamer.frags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.model.TracksAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by curtis on 7/6/15.
 */
public class TracksFragment extends Fragment {
    public static final String TAG = "TracksFragment";
    private static TracksFragment sInstance;

    //Metadata constants
    public static final String ARTIST_NAME = "artist_name";
    public static final String ARTIST_ID = "artist_id";

    private ArrayList<Track> mTracks;
    private ListView mTracksListView;
    private TracksAdapter mAdapter;
    private SpotifyService mService;

    public static TracksFragment getInstance(SpotifyService service, String aName, String aID){
        if(sInstance == null) {
            sInstance = new TracksFragment();
        }

        Bundle bnd = new Bundle();
        bnd.putString(ARTIST_NAME, aName);
        bnd.putString(ARTIST_ID, aID);

        sInstance.mService = service;
        sInstance.setArguments(bnd);

        return sInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTracks(getArguments().getString(ARTIST_ID));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_tracks, container, false);

        mTracksListView = (ListView) root.findViewById(R.id.results_artists_tracks_list);
//        loadTracks(getArguments().getString(ARTIST_ID));

        mTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "I'm a player, Implement Me!", Toast.LENGTH_LONG).show();
                Track track = mAdapter.getItem(position);
                String albumName = track.album.name;
                List<Image> images = track.album.images;
                String trackName = track.name;
                String prevURL = track.preview_url;
                Log.d(TAG, "albumname: " + albumName + ", images_count: " + images.size() + ", track_name: " + trackName + ", preview_URL: " + prevURL);
                //TODO: Launch player with the above meta data.
            }
        });

        return root;
    }

    public void loadTracks(final String artistId) {
        new AsyncTask<String, Void, Tracks>() {

            @Override
            protected Tracks doInBackground(String... artistIds) {
                TreeMap<String,Object> params = new TreeMap<String, Object>();
                //Must be included else api call will fail! This should probably be localized.
                params.put("country", "US");
                final Tracks tracks = mService.getArtistTopTrack(artistIds[0], params);
                return tracks;
            }

            @Override
            protected void onPostExecute(Tracks tracks) {
                if(tracks != null) {
                    if(mTracks == null)
                        mTracks = new ArrayList<Track>();
                    mTracks.addAll(tracks.tracks);
                    if(mAdapter == null)
                        mAdapter = new TracksAdapter(getActivity(), mTracks);
                    mTracksListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.spotify_icon)
                            .setTitle(getString(R.string.no_tracks))
                            .setPositiveButton(R.string.alert_dialog_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            ArtistActivity.this.onBackPressed();
                                        }
                                    })
                            .show();
                }
            }
        }.execute(artistId);
    }

}
