package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import com.android.settingslib.widget.MainSwitchPreference;

import com.android.settingslib.widget.SettingsBasePreferenceFragment;
public class RightKeySettingsFragment extends SettingsBasePreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MainSwitchPreference mainSwitch;
    private SeekBarPreference sensySlider;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.Prefs.PREF_KEY);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.prefs_right_key);

        mainSwitch = findPreference(Constants.Prefs.KEY_RIGHT_MODE);

        sensySlider = findPreference(Constants.Prefs.KEY_RIGHT_SHOULDER_SENS);
        if (sensySlider != null) {
            sensySlider.setMin(1);
            sensySlider.setMax(3);
        }
    }

    private void loadState() {
        mainSwitch.setChecked(KeyController.getKeyMode(1));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        loadState();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.Prefs.KEY_RIGHT_MODE)) {
            boolean value = sharedPreferences.getBoolean(key, false);
            if (value) {
                int sensitivity = sharedPreferences.getInt(Constants.RIGHT_SHOULDER_SENS, 2);
                int currentSensitivity = KeyController.getSensitivity(1);
                if (sensitivity != currentSensitivity) {
                    KeyController.initializeKey(1, sensitivity);
                } else {
                    KeyController.setRightKeyMode(true);
                }
            } else {
                KeyController.setRightKeyMode(false);
            }
        }

        if (key.equals(Constants.Prefs.KEY_RIGHT_SHOULDER_SENS)) {
            int value = sharedPreferences.getInt(key, 2);
            KeyController.setSensitivity(1, value);
        }

    }

}
