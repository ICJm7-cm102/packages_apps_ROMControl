/*
 * Copyright (C) 2013 Android Open Kang Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aokp.romcontrol.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.content.Intent;
import android.util.Log;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class ScreenStateToggles extends AOKPPreferenceFragment implements OnPreferenceChangeListener {
    private static final String TAG = "ScreenStateToggles";
    private static final String SCREEN_STATE_TOOGLES_ENABLE = "screen_state_toggles_enable_key";
    private static final String SCREEN_STATE_TOOGLES_TWOG = "screen_state_toggles_twog";
    private static final String SCREEN_STATE_TOOGLES_GPS = "screen_state_toggles_gps";
            
    private SwitchPreference mEnableScreenStateToggles;
    private CheckBoxPreference mEnableScreenStateTogglesTwoG;
    private CheckBoxPreference mEnableScreenStateTogglesGps;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.screen_state_toggles_title);

        addPreferencesFromResource(R.xml.prefs_screen_state_toggles);
        PreferenceScreen prefSet = getPreferenceScreen();

        mEnableScreenStateToggles = (SwitchPreference) prefSet.findPreference(
                SCREEN_STATE_TOOGLES_ENABLE);
        
        boolean enabled = Settings.System.getBoolean(getContentResolver(), Settings.System.START_SCREEN_STATE_SERVICE, false);
        mEnableScreenStateToggles.setChecked(enabled);
        mEnableScreenStateToggles.setOnPreferenceChangeListener(this);
        
        mEnableScreenStateTogglesTwoG = (CheckBoxPreference) prefSet.findPreference(
                SCREEN_STATE_TOOGLES_TWOG);
        mEnableScreenStateTogglesTwoG.setChecked(
            Settings.System.getBoolean(getContentResolver(), Settings.System.SCREEN_STATE_TWOG, false));
        mEnableScreenStateTogglesTwoG.setOnPreferenceChangeListener(this);

        mEnableScreenStateTogglesGps = (CheckBoxPreference) prefSet.findPreference(
                SCREEN_STATE_TOOGLES_GPS);
        mEnableScreenStateTogglesGps.setChecked(
            Settings.System.getBoolean(getContentResolver(), Settings.System.SCREEN_STATE_GPS, false));
        mEnableScreenStateTogglesGps.setOnPreferenceChangeListener(this);

        mEnableScreenStateTogglesTwoG.setEnabled(enabled);
        mEnableScreenStateTogglesGps.setEnabled(enabled);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableScreenStateToggles) {
            boolean value = ((Boolean) newValue).booleanValue();
            Settings.System.putBoolean(getContentResolver(),
                    Settings.System.START_SCREEN_STATE_SERVICE, value);

            Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.screenstate.ScreenStateService");
            if (value) {
                getActivity().stopService(service);
                getActivity().startService(service);
            } else {
                getActivity().stopService(service);
            }
            
            mEnableScreenStateTogglesTwoG.setEnabled(value);
            mEnableScreenStateTogglesGps.setEnabled(value);
            return true;
        } else if (preference == mEnableScreenStateTogglesTwoG) {
            Settings.System.putBoolean(getContentResolver(),
                    Settings.System.SCREEN_STATE_TWOG, (Boolean) newValue);

            Intent intent = new Intent("android.intent.action.SCREEN_STATE_SERVICE_UPDATE");
            mContext.sendBroadcast(intent);

            return true;
        } else if (preference == mEnableScreenStateTogglesGps) {
            Settings.System.putBoolean(getContentResolver(),
                    Settings.System.SCREEN_STATE_GPS, (Boolean) newValue);

            Intent intent = new Intent("android.intent.action.SCREEN_STATE_SERVICE_UPDATE");
            mContext.sendBroadcast(intent);

            return true;
        }

        return false;
    }
    
    private void restartService(){
        Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.screenstate.ScreenStateService");
        getActivity().stopService(service);
        getActivity().startService(service);
    }
}
