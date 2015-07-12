package com.iprodev.spotifystreamer.frags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.Utils;
import com.iprodev.spotifystreamer.model.SpotifyMediaPlayer;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class PlayerFragment extends DialogFragment {

    public static final String TAG = "PlayerFragment";
    public static final String ARTIST_NAME = "artname";
    public static final String ALBUM_NAME = "aname";
    public static final String TRACK_NAME = "tname";
    public static final String IMAGE_URL = "iurl";
    public static final String PREVIEW_URL = "purl";

    private static PlayerFragment sInstance;

    private SpotifyMediaPlayer player;
    private ImageView mPlayBtn;
    private ImageView mPrevBtn;
    private ImageView mNextBtn;
    private TextView mProgressText;
    private SeekBar mProgressBar;
    private int mDuration;
    private TransportCallbacks mCallback;
    private boolean mIsModal;
    private int mCurrentProgress;
    private String mStreamURL;
    private String artistName;
    private String albumName;
    private String imageUrl;
    private String trackName;

    public interface TransportCallbacks {
        public Track getPreviousTrack();
        public Track getNextTrack();
    }

    public static PlayerFragment getInstance(TransportCallbacks callback, Bundle bnd, boolean isModal) {
        sInstance = new PlayerFragment();
        sInstance.mCallback = callback;
        sInstance.mIsModal = isModal;
        sInstance.setArguments(bnd);

        return sInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsModal = savedInstanceState.getBoolean("mIsModal");
        }
        //Conditionally set the style based on device type.
        if (mIsModal) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            inflateUI();
        if (mStreamURL == null)
            mStreamURL = getArguments().getString(PlayerFragment.PREVIEW_URL);
        String car = "http://www.podtrac.com/pts/redirect.mp3/traffic.libsyn.com/theadamcarollashow/2015-07-08acs_2015-07-07-200824-7770-0-8-0.64k.mp3";

        startAudio(car);//mStreamURL);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.stopAudio();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("mCurrentProgress", mCurrentProgress);
        outState.putString("mStreamURL", mStreamURL);
        outState.putBoolean("mIsModal", mIsModal);
        //UI metadata
        outState.putString("artistName", artistName);
        outState.putString("albumName", albumName);
        outState.putString("imageUrl", imageUrl);
        outState.putString("trackName", trackName);
        super.onSaveInstanceState(outState);
    }

    public void setCallback(TransportCallbacks callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentProgress = savedInstanceState.getInt("mCurrentProgress");
            mStreamURL = savedInstanceState.getString("mStreamURL");
            mIsModal = savedInstanceState.getBoolean("mIsModal");
            artistName = savedInstanceState.getString("artistName");
            albumName = savedInstanceState.getString("albumName");
            imageUrl = savedInstanceState.getString("imageUrl");
            trackName = savedInstanceState.getString("trackName");
        }

        View root = inflater.inflate(R.layout.frag_player, container, false);
        mPlayBtn = (ImageView) root.findViewById(R.id.btn_play);
        mPrevBtn = (ImageView) root.findViewById(R.id.btn_rewind);
        mNextBtn = (ImageView) root.findViewById(R.id.btn_fastforward);
        mProgressBar = (SeekBar) root.findViewById(R.id.song_progress);
        mProgressText = (TextView) root.findViewById(R.id.text_progress);

        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressText.setText(Utils.convertMilliToFriendlyText(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { /*nothing here yet */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.skipTo(seekBar.getProgress());
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
            }
        });

        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = mCallback.getPreviousTrack();
                if (track != null) {
                    player.stopAudio();
                    inflateUI(track);
                    mStreamURL = track.preview_url;
                    startAudio(mStreamURL);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_more_tracks), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = mCallback.getNextTrack();
                if (track != null) {
                    player.stopAudio();
                    inflateUI(track);
                    mStreamURL = track.preview_url;
                    startAudio(mStreamURL);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_more_tracks), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Check mStreamURL to see if this is a rotation
        if (savedInstanceState == null) {
            Bundle extras = getArguments();
            artistName = extras.getString(ARTIST_NAME);
            albumName = extras.getString(ALBUM_NAME);
            imageUrl = extras.getString(IMAGE_URL);
            trackName = extras.getString(TRACK_NAME);

            ((TextView) root.findViewById(R.id.text_artist_name)).setText(artistName);
            ((TextView) root.findViewById(R.id.text_album_name)).setText(albumName);
            ((TextView) root.findViewById(R.id.text_song_name)).setText(trackName);
            Picasso.with(getActivity()).load(imageUrl).into((ImageView) root.findViewById(R.id.album_artwork));
        }


        return root;
    }


    public void startAudio(String url) {
        if (player == null) {
            player = new SpotifyMediaPlayer(new SpotifyMediaPlayer.Callbacks() {
                @Override
                public void onStarted(int duration) {
                    player.skipTo(mCurrentProgress);
                    mDuration = duration;
                    mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView dur = (TextView) getView().findViewById(R.id.text_duration);
                            dur.setText(Utils.convertMilliToFriendlyText(mDuration));
                        }
                    });
                    mProgressBar.setMax(mDuration);
                }

                @Override
                public void onPlay() {
                    mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                }

                @Override
                public void onPause() {
                    mPlayBtn.setImageResource(android.R.drawable.ic_media_play);
                }

                @Override
                public void onProgressUpdate(final int progress) {
                    mCurrentProgress = progress;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressText.setText(Utils.convertMilliToFriendlyText(progress));
                            mProgressBar.setProgress(progress);

                        }
                    });
                }

                @Override
                public void onComplete() {
                    //Autoplay feature that will go through all available tracks automatically until
                    // the last one is reached showing a toast and dismissing the player.
                    Track track = mCallback.getNextTrack();
                    if (track != null) {
                        player.stopAudio();
                        inflateUI(track);
                        mStreamURL = track.preview_url;
                        startAudio(mStreamURL);
                    } else {
                        Toast.makeText(getActivity(),getString(R.string.no_more_tracks),Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                @Override
                public void onPlayerException(SpotifyMediaPlayer.SpotifyMediaPlayerError error) {

                }
            });
        }
        player.startAudio(url);
    }

    /**
     * Sets the metadata for the UI
     *
     * @param extras bundle that contains the metadata.
     *               Expects artist name, album name, album art url, and track name
     */
    public void setExtras(Bundle extras) {
        artistName = extras.getString(ARTIST_NAME);
        albumName = extras.getString(ALBUM_NAME);
        imageUrl = extras.getString(IMAGE_URL);
        trackName = extras.getString(TRACK_NAME);

        ((TextView) getView().findViewById(R.id.text_artist_name)).setText(artistName);
        ((TextView) getView().findViewById(R.id.text_album_name)).setText(albumName);
        ((TextView) getView().findViewById(R.id.text_song_name)).setText(trackName);
        Picasso.with(getActivity()).load(imageUrl).into((ImageView) getView().findViewById(R.id.album_artwork));
    }

    private void inflateUI(Track track) {
        artistName = track.artists.get(0).name;
        albumName = track.album.name;
        trackName = track.name;
        imageUrl = null;
        for (Image i : track.album.images) {
            if (i.height >= 300) {
                imageUrl = i.url;
                break;
            }
        }
        inflateUI();
    }

    private void inflateUI() {
        ((TextView) getView().findViewById(R.id.text_artist_name)).setText(artistName);
        ((TextView) getView().findViewById(R.id.text_album_name)).setText(albumName);
        ((TextView) getView().findViewById(R.id.text_song_name)).setText(trackName);
        Picasso.with(getActivity()).load(imageUrl).into((ImageView) getView().findViewById(R.id.album_artwork));
    }


    private class PlayerTrack implements Serializable {

    }
}