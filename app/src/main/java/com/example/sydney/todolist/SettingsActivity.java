package com.example.sydney.todolist;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

//import android.support.v4.app.Fragment;

/**
 * Settings Activity
 * Various settings user can select shown here.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Fragment fragment = new SettingsScreen();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(savedInstanceState == null) {
            //created for the first time
            fragmentTransaction.add(R.id.settings_layout, fragment, "settings_fragment");
            fragmentTransaction.commit();
        }
        else {
            fragment = getFragmentManager().findFragmentByTag("settings_fragment");
        }
    }

    public static class SettingsScreen extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);
            SwitchPreference switchPreference = (SwitchPreference) findPreference("");
        }
    }
}
