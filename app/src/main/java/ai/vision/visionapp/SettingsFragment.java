package ai.vision.visionapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.preference.Preference;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.content.res.Resources.Theme;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.content.Intent;


public class SettingsFragment extends PreferenceFragmentCompat

    {
    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }*/

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        load_configs();


    }

    public boolean load_configs() {
        SwitchPreferenceCompat themesw_instance = (SwitchPreferenceCompat) findPreference("THEME");
        themesw_instance.setOnPreferenceChangeListener(new androidx.preference.Preference.OnPreferenceChangeListener()
            {
            @Override
            public boolean onPreferenceChange(androidx.preference.Preference preference, Object obj) {

                boolean sw = (boolean) obj;

                if (sw) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Log.d("themeChange:","Yes");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    Log.d("themeChange:","NO");
                }

                return true;
            }
            });
        return true;
    }


    @Override
    public void onResume() {
        load_configs();
        super.onResume();
    }
    }
