package com.iprodev.spotifystreamer.model;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by curtis on 7/8/15.
 */
public class SpotifyMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public static final String TAG = "SpotifyMediaPlayer";

    private MediaPlayer mPlayer;
    private Callbacks mCallbacks;
    private PlayerState mPlayerState;

    public interface Callbacks {
        public void onStarted(int duration);
        public void onPlay();
        public void onPause();
        public void onProgressUpdate(int progress);
        public void onComplete();
        public void onPlayerException(SpotifyMediaPlayerError error);
    }

    public enum PlayerState {
        PREPARING,
        PREPARED,
        PLAYING,
        PAUSED,
        STOPPED,
        COMPLETED,
        ERROR
    }

    public class SpotifyMediaPlayerError extends Exception{

        public SpotifyMediaPlayerError(String msg) {
            super(msg);
        }
    }

    public SpotifyMediaPlayer(Callbacks callbacks){
        mCallbacks = callbacks;
    }

    public void startAudio(String url) {
        Log.d(TAG, "startAudio with url: " + url);
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
            mPlayerState = PlayerState.PREPARING;
        } catch (IOException e) {
            e.printStackTrace();
            mCallbacks.onPlayerException(new SpotifyMediaPlayerError(e.getLocalizedMessage()));
        }
    }

    /* Transport controls */
    public void play() {
        switch(mPlayerState) {
            case PLAYING:
                mPlayerState = PlayerState.PAUSED;
                mPlayer.pause();
                mCallbacks.onPause();
                break;
            case PAUSED:
                mPlayerState = PlayerState.PLAYING;
                mPlayer.start();
                mCallbacks.onPlay();
                startTrackingProgress();
                break;
            default:
                Log.e(TAG, "UNKNOWN player state! Should be playing or paused.");
                break;
        }
    }

    public void skipTo(int to) {
//        to = mPlayer.getCurrentPosition() + to;
        mPlayer.seekTo(to);
    }

    public void stopAudio() {
        mPlayerState = PlayerState.STOPPED;
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayerState = PlayerState.PLAYING;
        mPlayer.start();
        mCallbacks.onStarted(mp.getDuration());
        startTrackingProgress();
    }

    private void startTrackingProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mPlayerState == PlayerState.PLAYING) {
                    try {
                        Thread.sleep(250);
                        mCallbacks.onProgressUpdate(mPlayer.getCurrentPosition());
                    } catch (IllegalStateException ie) {
                        /* Nothing to do yet */
                    }catch (InterruptedException e ) {
                        /* Nothing to do yet */
                    } catch (NullPointerException ne) {
                        /* Player can be null, this means stopAudio was called so we just ignore */
                    }
                }
            }
        }).start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayerState = PlayerState.COMPLETED;
        mCallbacks.onComplete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mPlayerState = PlayerState.ERROR;
        mCallbacks.onPlayerException(new SpotifyMediaPlayerError(
                "ERROR in player! What: " + what + ", extra: " + extra));
        return false;
    }
}
