package com.iprodev.spotifystreamer.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;
import com.iprodev.spotifystreamer.frags.SettingsFragment;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by curtis on 6/18/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SpotifyService mSpotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHandlers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                //Lets use the activity backstack instead so the state of the search results is
                // maintained.
                onBackPressed();
                return true;
            case R.id.action_settings:
                //Check for fragment existance and add to backstack.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SettingsFragment prev = (SettingsFragment)getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                //Get and show settings fragment.
                SettingsFragment settingsFrag = SettingsFragment.getInstance();
                settingsFrag.show(ft, SettingsFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initService() {
        mSpotifyService =  new SpotifyApi().getService();
    }

    protected SpotifyService getService() {
        return mSpotifyService;
    }

    protected void showServiceError(Exception e) {
        Toast.makeText(this, String.format("%1$s %2$s", getString(R.string.service_error),e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    /**
     * Implement to set the click and touch event handlers
     */
    protected abstract void setHandlers();
}
