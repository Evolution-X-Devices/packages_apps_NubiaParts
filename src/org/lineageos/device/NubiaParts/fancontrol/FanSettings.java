package org.lineageos.device.NubiaParts.fancontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;
import com.android.settingslib.widget.SliderPreference;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.*;

import java.util.ArrayList;
import java.util.List;

public class FanSettings extends SettingsBasePreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MainSwitchPreference fanToggle;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.FAN_PREF_NAME);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.main_prefs);

        fanToggle = findPreference(Constants.USER_ENABLE_FAN_KEY);

        Preference mapAppPref = findPreference("per_app_fan_speed");
        if (mapAppPref != null) {
            mapAppPref.setOnPreferenceClickListener(preference -> {
                Intent i = new Intent(requireContext(), ApplicationListActivity.class);
                startActivity(i);
                return true;
            });
        }

        SliderPreference fanSpeedPref = findPreference(Constants.USER_FAN_SPEED_KEY);
        if (fanSpeedPref != null) {
            fanSpeedPref.setSliderIncrement(1);
            fanSpeedPref.setUpdatesContinuously(true);
            fanSpeedPref.setHapticFeedbackMode(SliderPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS);
            fanSpeedPref.setTickVisible(true);
            fanSpeedPref.setShowSliderValue(true);
        }

        updateMainSwitch();
    }


    private void updateMainSwitch() {
       SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        fanToggle.setChecked(prefs.getBoolean(Constants.USER_ENABLE_FAN_KEY, false)
                && FanController.isEnabled());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updateMainSwitch();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.USER_ENABLE_FAN_KEY)) {
            boolean value = sharedPreferences.getBoolean(key, false);
            if (!value) {
                sendFanServiceIntent(requireContext(), 2);
            } else {
                sendFanServiceIntent(requireContext(), 3);
            }
        } else {
            sendFanServiceIntent(requireContext(), 3);
        }
    }

    private boolean sendFanServiceIntent(Context context, int type) {
        Intent fanIntent = new Intent(context, FanService.class);
        switch (type) {
            case 1:
                fanIntent.setAction(Constants.INTENT_FAN_START);
                break;
            case 2:
                fanIntent.setAction(Constants.INTENT_FAN_STOP);
                break;
            case 3:
                fanIntent.setAction(Constants.INTENT_FAN_RELOAD);
                break;

        }
        context.startService(fanIntent);
        return true;
    }

}
