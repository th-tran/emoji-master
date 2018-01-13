package com.github.tran_thomas.emojimaster;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.OutputStreamWriter;

/**
 * A fragment of SettingsActivity
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference recentClearPref = (Preference) findPreference(SettingsActivity.KEY_PREF_RECENT_CLEAR);
        recentClearPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                openRecentClearPrompt();
                return true;
            }
        });
        Preference customClearPref = (Preference) findPreference(SettingsActivity.KEY_PREF_CUSTOM_CLEAR);
        customClearPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openCustomClearPrompt();
                return true;
            }
        });
        Preference sendFeedbackPref = (Preference) findPreference(SettingsActivity.KEY_PREF_SEND_FEEDBACK);
        sendFeedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                composeEmail();
                return true;
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPref,
                                          String key) {
        if (key.equals(SettingsActivity.KEY_PREF_BG_DARK)){
            MainActivity mActivity = new MainActivity();
            mActivity.recreate();
            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void openRecentClearPrompt() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View promptView = inflater.inflate(R.layout.recent_clear_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearRecent();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void clearRecent() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(getActivity().openFileOutput("recent.txt", Context.MODE_PRIVATE));
            writer.write("");
            writer.close();
            MainActivity.numRecent = 0;
            Snackbar snackbar = Snackbar.make(getView(), "Recently used emoticons have been reset.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Exception: "+e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void openCustomClearPrompt() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View promptView = inflater.inflate(R.layout.custom_clear_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearCustom();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void clearCustom() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(getActivity().openFileOutput("custom.txt", Context.MODE_PRIVATE));
            writer.write("");
            writer.close();
            Snackbar snackbar = Snackbar.make(getView(), "Custom emoticons have been reset.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Exception: "+e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void composeEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","doggostone@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Emoji Master Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Snackbar snackbar = Snackbar.make(getView(), "No email app is available.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}
