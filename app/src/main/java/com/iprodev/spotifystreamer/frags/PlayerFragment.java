package com.iprodev.spotifystreamer.frags;

import android.app.Activity;
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
import com.iprodev.spotifystreamer.model.SpotifyMediaPlayer;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by curtis on 7/8/15.
 */
public class PlayerFragment extends DialogFragment {

    public static final String TAG = "PlayerFragment";
    public static final String ARTIST_NAME = "artname";
    public static final String ALBUM_NAME = "aname";
    public static final String TRACK_NAME = "tname";
    public static final String IMAGE_URL = "iurl";
    public static final String PREVIEW_URL = "purl";
    //Time constants
    public static final int ONE_HOUR = 3600000;
    public static final int ONE_MINUTE = 60000;
    public static final int ONE_SECOND = 1000;

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
        if(sInstance == null)
            sInstance = new PlayerFragment();
        sInstance.mCallback = callback;
        sInstance.mIsModal = isModal;
        sInstance.setArguments(bnd);

        return sInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mIsModal = savedInstanceState.getBoolean("mIsModal");
        }
//        setRetainInstance(true);
        if(mIsModal) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
            inflateUI();

        String car = "http://www.podtrac.com/pts/redirect.mp3/traffic.libsyn.com/theadamcarollashow/2015-07-08acs_2015-07-07-200824-7770-0-8-0.64k.mp3";
        if(mStreamURL == null)
            mStreamURL = getArguments().getString(PlayerFragment.PREVIEW_URL);
        startAudio(mStreamURL);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.stopAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("mCurrentProgress", mCurrentProgress);
        outState.putString("mStreamURL", mStreamURL);
        outState.putBoolean("mIsModal", mIsModal);
        //UI metadata
        outState.putString("artistName", artistName);
        outState.putString("albumName", albumName);
        outState.putString("imageUrl",imageUrl);
        outState.putString("trackName", trackName);

        super.onSaveInstanceState(outState);
    }

    public void setCallback(TransportCallbacks callback) {
        mCallback = callback;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mCurrentProgress = savedInstanceState.getInt("mCurrentProgress");
            mStreamURL = savedInstanceState.getString("mStreamURL");
            mIsModal = savedInstanceState.getBoolean("mIsModal");
            artistName = savedInstanceState.getString("artistName");
            albumName = savedInstanceState.getString("albumName");
            imageUrl = savedInstanceState.getString("imageUrl");
            trackName = savedInstanceState.getString("trackName");
        }

        View root = inflater.inflate(R.layout.frag_player,container, false);
        mPlayBtn = (ImageView) root.findViewById(R.id.btn_play);
        mPrevBtn = (ImageView) root.findViewById(R.id.btn_rewind);
        mNextBtn = (ImageView) root.findViewById(R.id.btn_fastforward);
        mProgressBar = (SeekBar) root.findViewById(R.id.song_progress);
        mProgressText = (TextView) root.findViewById(R.id.text_progress);

        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressText.setText(convertMilliToFreindlyText(seekBar.getProgress()));
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
        if(savedInstanceState == null) {
            Bundle extras = getArguments();
            String artistName = extras.getString(ARTIST_NAME);
            String albumName = extras.getString(ALBUM_NAME);
            String imageUrl = extras.getString(IMAGE_URL);
            String trackName = extras.getString(TRACK_NAME);

            ((TextView)root.findViewById(R.id.text_artist_name)).setText(artistName);
            ((TextView)root.findViewById(R.id.text_album_name)).setText(albumName);
            ((TextView)root.findViewById(R.id.text_song_name)).setText(trackName);
            Picasso.with(getActivity()).load(imageUrl).into((ImageView) root.findViewById(R.id.album_artwork));

        }


        return root;
    }



    public void startAudio(String url) {
        if(player == null) {
            player = new SpotifyMediaPlayer(new SpotifyMediaPlayer.Callbacks() {
                @Override
                public void onStarted(int duration) {
                    player.skipTo(mCurrentProgress);
                    mDuration = duration;
                    mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
    //                        Toast.makeText(getActivity(), "DEBUG: started playing, duration: "
    //                                + mDuration, Toast.LENGTH_SHORT).show();

                            TextView dur = (TextView)getView().findViewById(R.id.text_duration);
                            dur.setText(convertMilliToFreindlyText(mDuration));
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
                            mProgressText.setText(convertMilliToFreindlyText(progress));
                            mProgressBar.setProgress(progress);

                        }
                    });
                }

                @Override
                public void onComplete() {
                    getActivity().onBackPressed();
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
     * @param extras bundle that contains the metadata.
     *               Expects artist name, album name, album art url, and track name
     */
    public void setExtras(Bundle extras) {
        artistName = extras.getString(ARTIST_NAME);
        albumName = extras.getString(ALBUM_NAME);
        imageUrl = extras.getString(IMAGE_URL);
        trackName = extras.getString(TRACK_NAME);

        ((TextView)getView().findViewById(R.id.text_artist_name)).setText(artistName);
        ((TextView)getView().findViewById(R.id.text_album_name)).setText(albumName);
        ((TextView)getView().findViewById(R.id.text_song_name)).setText(trackName);
        Picasso.with(getActivity()).load(imageUrl).into((ImageView) getView().findViewById(R.id.album_artwork));
    }

    private void inflateUI(Track track) {
        artistName = track.artists.get(0).name;
        albumName = track.album.name;
        trackName = track.name;
        imageUrl = null;
        for(Image i : track.album.images) {
            if(i.height >= 300 ) {
                imageUrl = i.url;
                break;
            }
        }
        inflateUI();
    }

    private void inflateUI() {
        ((TextView)getView().findViewById(R.id.text_artist_name)).setText(artistName);
        ((TextView)getView().findViewById(R.id.text_album_name)).setText(albumName);
        ((TextView)getView().findViewById(R.id.text_song_name)).setText(trackName);
//        String trackName = track.name;
        Picasso.with(getActivity()).load(imageUrl).into((ImageView) getView().findViewById(R.id.album_artwork));
    }



    private class PlayerTrack implements Serializable {

    }

    private String convertMilliToFreindlyText(int millis) {
        String retVal = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        //Extract hour
        if(millis > ONE_HOUR) {
            float f = millis / ONE_HOUR;
            hours = (int) Math.floor(f);
        }
        //Extract minutes
        if(hours > 0) {
            int tmp = millis - (hours * ONE_HOUR);
            float f = tmp / ONE_MINUTE;
            minutes = (int) Math.floor(f);
        } else {
            float f = millis / ONE_MINUTE;
            minutes = (int) Math.floor(f);
        }
        if(millis > ONE_MINUTE) {
        }
        //Extract seconds
        if(hours > 0) {
            int tmp = millis - (hours * ONE_HOUR);
            tmp = tmp - (minutes * ONE_MINUTE);
            seconds = (int)Math.floor(tmp / ONE_SECOND);
        } else if(minutes > 0) {
            int tmp = millis - (minutes * ONE_MINUTE);
            seconds = (int)Math.floor(tmp / ONE_SECOND);
        } else{
            seconds = (int)Math.floor(millis / ONE_SECOND);
        }
        if(seconds < 10) {
            retVal = (hours > 0) ? hours + ":":"" + minutes + ":0" + seconds;
        } else {
            retVal = (hours > 0) ? hours + ":":"";
            retVal = retVal + minutes + ":" + seconds;
        }

        return retVal;
    }
}