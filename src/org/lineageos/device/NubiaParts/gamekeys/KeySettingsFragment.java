package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SliderPreference;

import com.android.settingslib.widget.SettingsBasePreferenceFragment;

public class KeySettingsFragment extends SettingsBasePreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MainSwitchPreference mainSwitch;
    private SliderPreference sensySlider;

    private int keyInt;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        Bundle args = getArguments();
        keyInt = args.getInt("type", -1);

        switch (keyInt) {
            case 0 -> {
                addPreferencesFromResource(R.xml.prefs_left_key);
                sensySlider = findPreference(Constants.Prefs.KEY_LEFT_SHOULDER_SENS);
                mainSwitch = findPreference(Constants.Prefs.KEY_LEFT_MODE);
            }
            case 1 -> {
                addPreferencesFromResource(R.xml.prefs_right_key);
                sensySlider = findPreference(Constants.Prefs.KEY_RIGHT_SHOULDER_SENS);
                mainSwitch = findPreference(Constants.Prefs.KEY_RIGHT_MODE);
            }
            case -1 -> {
                getParentFragmentManager().popBackStack();
                getActivity().finish();
            }
        }

        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.Prefs.PREF_KEY);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

        if (sensySlider != null) {
            sensySlider.setSliderIncrement(1);
            sensySlider.setUpdatesContinuously(true);
            sensySlider.setHapticFeedbackMode(SliderPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS);
            sensySlider.setShowSliderValue(true);
            sensySlider.setMin(1);
            sensySlider.setMax(3);
        }

        loadState();
    }

    private void loadState() {
        mainSwitch.setChecked(KeyController.getKeyMode(keyInt));
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
        if (key.equals(mainSwitch.getKey())) {
            boolean value = sharedPreferences.getBoolean(key, false);
           if (value) {
               int sensitivity = sharedPreferences.getInt(sensySlider.getKey(), 2);
               int currentSensitivity = KeyController.getSensitivity(keyInt);
                if (sensitivity != currentSensitivity) {
                    KeyController.initializeKey(keyInt, sensitivity);
                } else {
                    KeyController.setKeyMode(keyInt, true);
                }
            } else {
               KeyController.setKeyMode(keyInt, false);
            }
        }

        if (key.equals(sensySlider.getKey())) {
            int value = sharedPreferences.getInt(key, 2);
            KeyController.setSensitivity(keyInt, value);
        }

    }

}
