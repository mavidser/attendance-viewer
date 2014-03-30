package com.juet.attendance;

import com.juet.attendance.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsView extends PreferenceActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
    }
}
