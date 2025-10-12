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
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class FanSettings extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.FAN_PREF_NAME);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.main_prefs);

        Context context = requireContext();


        Preference mapAppPref = findPreference("per_app_fan_speed");
        if (mapAppPref != null) {
            mapAppPref.setOnPreferenceClickListener(preference -> {
                showAppListDialog(requireContext());
                return true;
            });
        }

        SeekBarPreference fanSpeedPref = findPreference(Constants.USER_FAN_SPEED_KEY);
        if (fanSpeedPref != null) {
            fanSpeedPref.setSeekBarIncrement(1);
            fanSpeedPref.setUpdatesContinuously(true);
            fanSpeedPref.setShowSeekBarValue(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
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
                sendFanServiceIntent(requireContext(), 1);
            }
        } else {
            sendFanServiceIntent(requireContext(), 3);
        }
    }

    public boolean sendFanServiceIntent(Context context, int type) {
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

    private void showAppListDialog(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // Store label + package name pairs
        List<Pair<String, String>> labelPackagePairs = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                String label = pm.getApplicationLabel(app).toString();
                labelPackagePairs.add(new Pair<>(label, app.packageName));
            }
        }

        // Sort by label (case-insensitive)
        labelPackagePairs.sort((o1, o2) -> o1.first.compareToIgnoreCase(o2.first));

        // Extract sorted lists
        List<String> labels = new ArrayList<>();
        List<String> packageNames = new ArrayList<>();

        for (Pair<String, String> pair : labelPackagePairs) {
            labels.add(pair.first);
            packageNames.add(pair.second);
        }

        new AlertDialog.Builder(context)
                .setTitle("Select App")
                .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                    String selectedPackage = packageNames.get(which);
                    showValueSelector(requireContext(), selectedPackage);
                })
                .show();
    }

    private void showValueSelector(Context context, String packageName) {
        String[] labels = { "Default", "Speed 1", "Speed 2", "Speed 3", "Speed 4", "Speed 5" };
        String[] values = { null, "1", "2", "3", "4", "5" };

        SharedPreferences prefs = context.getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);

        new AlertDialog.Builder(context)
                .setTitle("Select Speed for " + packageName)
                .setItems(labels, (dialog, which) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    if (values[which] == null) {
                        editor.remove(packageName);
                        Toast.makeText(context, "Removed mapping for " + packageName, Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putString(packageName, values[which]);
                        Toast.makeText(context, "Set " + packageName + " to speed " + values[which], Toast.LENGTH_SHORT).show();
                    }
                    editor.apply();
                })
                .show();
    }

}
