package com.iprodev.spotifystreamer.frags;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iprodev.spotifystreamer.R;

public class SettingsFragment extends DialogFragment {

    public static final String TAG = "SettingsFragment";
    private static SettingsFragment sInstance;

    /* Shared preferences to persist user settings */
    public static final String PREFS = "prefs";
    public static final String COUNTRY_CODE = "country_code";
    private EditText mCountryCodeEdit;


    public static SettingsFragment getInstance() {
        if(sInstance == null) sInstance = new SettingsFragment();
        return sInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Dialog fragment
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_settings, container, false);
        mCountryCodeEdit = (EditText) root.findViewById(R.id.country_code_edit_txt);
        Button commitBtn = (Button) root.findViewById(R.id.commit_btn);
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commitSettings()) dismiss();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String curCode = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(COUNTRY_CODE, null);
        if(curCode != null)
            mCountryCodeEdit.setText(curCode);
    }

    /**
     * Saves the settings choices to file.
     * @return true if settings were saved
     */
    private boolean commitSettings() {
        String code = mCountryCodeEdit.getText().toString();
        if(code.length() == 2) {
            //commit settings
            SharedPreferences.Editor prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
            prefs.putString(COUNTRY_CODE,code).commit();
            Toast.makeText(getActivity(), String.format("%1$s %2$s",
                    getString(R.string.country_code_setting_saved), code), Toast.LENGTH_SHORT).show();
            mCountryCodeEdit.setText("");
            return true;
        } else {
            Toast.makeText(getActivity(),getString(R.string.error_country_code_edit),Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
