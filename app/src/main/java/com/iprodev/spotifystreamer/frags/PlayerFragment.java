package com.iprodev.spotifystreamer.frags;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.SpotifyMediaPlayer;
import com.squareup.picasso.Picasso;

/**
 * Created by curtis on 7/8/15.
 */
public class PlayerFragment extends Fragment {

    public static final String ARTIST_NAME = "artname";
    public static final String ALBUM_NAME = "aname";
    public static final String TRACK_NAME = "tname";
    public static final String IMAGE_URL = "iurl";
    public static final String PREVIEW_URL = "purl";

    private SpotifyMediaPlayer player;
    private ImageView mPlayBtn;
    private ImageView mRewindBtn;
    private ImageView mFastForwardBtn;
    private TextView mProgressText;
    private ProgressBar mProgressBar;
    private int mDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_player,container, false);
        mPlayBtn = (ImageView) root.findViewById(R.id.btn_play);
        mRewindBtn = (ImageView) root.findViewById(R.id.btn_rewind);
        mFastForwardBtn = (ImageView) root.findViewById(R.id.btn_fastforward);
        mProgressBar = (ProgressBar) root.findViewById(R.id.song_progress);
        mProgressText = (TextView) root.findViewById(R.id.text_progress);


        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
            }
        });

        mRewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mFastForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
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

    public void startAudio(String url) {
        player = new SpotifyMediaPlayer(new SpotifyMediaPlayer.Callbacks() {
            @Override
            public void onStarted(int duration) {
                mDuration = duration;
                mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getActivity(), "DEBUG: started playing, duration: "
//                                + mDuration, Toast.LENGTH_SHORT).show();

                        TextView dur = (TextView)getView().findViewById(R.id.text_duration);
                        dur.setText("test: " + mDuration);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setText("test: " + progress);
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
        player.startAudio(url);
    }

    /**
     * Sets the metadata for the UI
     * @param extras bundle that contains the metadata.
     *               Expects artist name, album name, album art url, and track name
     */
    public void setExtras(Bundle extras) {
        String artistName = extras.getString(ARTIST_NAME);
        String albumName = extras.getString(ALBUM_NAME);
        String imageUrl = extras.getString(IMAGE_URL);
        String trackName = extras.getString(TRACK_NAME);

        ((TextView)getView().findViewById(R.id.text_artist_name)).setText(artistName);
        ((TextView)getView().findViewById(R.id.text_album_name)).setText(albumName);
        ((TextView)getView().findViewById(R.id.text_song_name)).setText(trackName);
        Picasso.with(getActivity()).load(imageUrl).into((ImageView) getView().findViewById(R.id.album_artwork));
    }
}