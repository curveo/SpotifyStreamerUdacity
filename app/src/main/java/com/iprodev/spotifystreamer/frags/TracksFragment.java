package com.iprodev.spotifystreamer.frags;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.model.TracksAdapter;

import java.util.ArrayList;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TracksFragment extends Fragment {
    public static final String TAG = "TracksFragment";
    private static TracksFragment sInstance;

    //Metadata constants
    public static final String ARTIST_NAME = "artist_name";
    public static final String ARTIST_ID = "artist_id";

    private ArrayList<Track> mTracks;
    private TracksAdapter mAdapter;
    private SpotifyService mService;
    private TracksFragCallback mCallback;
    private int mSelectedPosition;

    public interface TracksFragCallback {
        public void onTrackSelected(Track track);
        public void onNoTracksAvailable();
    }

    public static TracksFragment getInstance(SpotifyService service, TracksFragCallback callback) {
        if (sInstance == null)
            sInstance = new TracksFragment();
        sInstance.mService = service;
        sInstance.mCallback = callback;

        return sInstance;
    }

    public void setCallback(TracksFragCallback callback) {
        mCallback = callback;
    }

    public void loadFragData(SpotifyService service, String aName, String aID) {
        mService = service;
        loadTracks(aID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTracks = (ArrayList<Track>) savedInstanceState.getSerializable("mTracks");
            mSelectedPosition = savedInstanceState.getInt("mSelectedPosition");
        }
        View root = inflater.inflate(R.layout.frag_tracks, container, false);

        ListView mTracksListView = (ListView) root.findViewById(R.id.results_artists_tracks_list);
        if (mTracks == null)
            mTracks = new ArrayList<Track>();
        mAdapter = new TracksAdapter(getActivity(), mTracks);
        mTracksListView.setAdapter(mAdapter);

        mTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = mAdapter.getItem(position);
                mCallback.onTrackSelected(track);
                mSelectedPosition = position;
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTracks != null && mTracks.size() > 0)
            outState.putSerializable("mTracks", mTracks);
        outState.putInt("mSelectedPosition", mSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    public void loadTracks(final String artistId) {
        new AsyncTask<String, Void, Tracks>() {

            @Override
            protected Tracks doInBackground(String... artistIds) {
                TreeMap<String, Object> params = new TreeMap<String, Object>();
                //Must be included else api call will fail! This should probably be localized.
                params.put("country", "US");
                final Tracks tracks = mService.getArtistTopTrack(artistIds[0], params);
                return tracks;
            }

            @Override
            protected void onPostExecute(Tracks tracks) {
                mTracks.clear();
                if (null != tracks && tracks.tracks.size() > 0) {
                    mTracks.addAll(tracks.tracks);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.spotify_icon)
                            .setTitle(getString(R.string.no_tracks))
                            .setPositiveButton(R.string.alert_dialog_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Nothing to do but dismiss dialog
                                            mCallback.onNoTracksAvailable();
                                        }
                                    })
                            .show();
                }
                mAdapter.notifyDataSetChanged();
            }
        }.execute(artistId);
    }

    public void restTracks() {
        mTracks.clear();
        mAdapter.notifyDataSetChanged();
    }

    public Track getPreviousTrack() {
        if (mSelectedPosition > 0)
            return mTracks.get(--mSelectedPosition);
        return null;
    }

    public Track getNextTrack() {
        if ((mSelectedPosition + 1) < mTracks.size())
            return mTracks.get(++mSelectedPosition);
        return null;
    }

}
