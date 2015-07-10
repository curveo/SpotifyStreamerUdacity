package com.iprodev.spotifystreamer.frags;

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

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.model.ArtistsAdaper;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

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
    private SearchCallbacks mCallbacks;
    private int mPosition;

    public interface SearchCallbacks {
        public void onArtistSelected(Artist artist);
    }

    public static SearchFragment getInstance(SearchCallbacks callbacks) {
        if (sInstance == null)
            sInstance = new SearchFragment();

        sInstance.setCallbacks(callbacks);
        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mResults = (ArrayList<Artist>) savedInstanceState.getSerializable("mResults");
            mPosition = savedInstanceState.getInt("mPosition");
        }
        View root = inflater.inflate(R.layout.frag_search, container, false);

        mResultsList = (ListView) root.findViewById(R.id.results_artists_list);
        mSpotIcon = (ImageView) root.findViewById(R.id.spot_icon);
//        if(mQueryString != null)
//            loadSearchData(mQueryString);
        mResults = new ArrayList<Artist>();
        mResultsAdapter = new ArtistsAdaper(getActivity(), mResults);
        mResultsList.setAdapter(mResultsAdapter);

        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = (Artist) adapterView.getAdapter().getItem(i);
                Log.d(TAG, "artist id: " + artist.id);
                mCallbacks.onArtistSelected(artist);
                mPosition = i;
//
//                Intent intent = new Intent(getActivity(), ArtistActivity.class);
//                intent.putExtra(ARTIST_NAME, artist.name);
//                intent.putExtra(ARTIST_ID, artist.id);
//                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if(mResults != null && mResults.size() > 0)
//            outState.putSerializable("mResults",mResults);
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt("mPosition", mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mResults != null && mResults.size() > 0)
            inflateSearchResults();
//        mResultsList.smoothScrollToPosition(mPosition);
    }

    public void setCallbacks(SearchCallbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
    }

    public void loadSearchData(final SpotifyService service, final String search) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ArtistsPager results = service.searchArtists(search);
                Log.d(TAG, results.toString());
                mResults.clear();
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
        mResultsAdapter.notifyDataSetChanged();
        updateUI(false);
//        mResultsList.smoothScrollToPosition(mPosition);
        mResultsList.requestFocus();
        mResultsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mResultsList.setItemChecked(mPosition,true);
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
    }
}