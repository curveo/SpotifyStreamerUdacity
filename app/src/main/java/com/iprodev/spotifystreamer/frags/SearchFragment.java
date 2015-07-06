package com.iprodev.spotifystreamer.frags;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.iprodev.spotifystreamer.ArtistActivity;
import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.model.ArtistsAdaper;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

import static com.iprodev.spotifystreamer.ArtistActivity.ARTIST_ID;
import static com.iprodev.spotifystreamer.ArtistActivity.ARTIST_NAME;

/**
 * Created by curtisashby on 7/6/15.
 */
public class SearchFragment extends Fragment {
    private static SearchFragment sInstance;
    private static final String TAG = "SearchFragment";

    private ArtistsAdaper mResultsAdapter;
    private ArrayList<Artist> mResults;
    private ListView mResultsList;
    private ImageView mSpotIcon;


    public static SearchFragment getInstance() {
        if(sInstance == null)
            sInstance = new SearchFragment();

        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mResults = (ArrayList<Artist>) savedInstanceState.getSerializable("mResults");
        }
        View root = inflater.inflate(R.layout.frag_search, container, false);

        mResultsList = (ListView) root.findViewById(R.id.results_artists_list);
        mSpotIcon = (ImageView) root.findViewById(R.id.spot_icon);
//        if(mQueryString != null)
//            loadSearchData(mQueryString);
        if(mResults != null) {
            inflateSearchResults();
        } else {
            mResults = new ArrayList<Artist>();
        }

        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = (Artist) adapterView.getAdapter().getItem(i);

                Log.d(TAG, "artist id: " + artist.id);
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra(ARTIST_NAME, artist.name);
                intent.putExtra(ARTIST_ID, artist.id);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if(mResults != null && mResults.size() > 0)
//            outState.putSerializable("mResults",mResults);
        super.onSaveInstanceState(outState);
    }

    public void loadSearchData(final SpotifyService service, final String search) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ArtistsPager results = service.searchArtists(search);
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
                inflateSearchResults();
            }
        }.execute();
    }

    private void inflateSearchResults() {
        if(mResults.size() > 0) {
            if(mResultsAdapter == null) {
                mResultsAdapter = new ArtistsAdaper(getActivity(), mResults);
                mResultsList.setAdapter(mResultsAdapter);
            }
            mResultsAdapter.notifyDataSetChanged();
        }
        updateUI(false);
    }

    public void updateUI(boolean clearUI) {
        View noResults = (View) getView().findViewById(R.id.noresults_text);
        if (!clearUI) {
            mSpotIcon.setVisibility(View.INVISIBLE);
            noResults.setVisibility((mResults.size() == 0) ? View.VISIBLE : View.INVISIBLE);
            mResultsList.setVisibility((mResults.size() == 0) ? View.INVISIBLE : View.VISIBLE);
        } else {
            mSpotIcon.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
            mResults.clear();
        }

        //TODO: Maybe??
        class MyArtist extends Artist implements Parcelable {

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        }
    }
}