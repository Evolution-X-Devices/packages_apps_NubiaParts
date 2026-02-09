package org.lineageos.device.NubiaParts.gameswitch;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.gameswitch.R;
import org.lineageos.device.NubiaParts.gameswitch.actions.*;

public class GameSwitchSettings extends SettingsBasePreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PackageManager pm;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ResourceUtils.init(requireContext());

        Context user0Ctx = requireContext().createContextAsUser(UserHandle.of(0), 0);

        pm = user0Ctx.getPackageManager();

        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.PREF_KEY);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.prefs);

        Preference appLaunchPref = findPreference(Constants.KEY_LAUNCH_APP_NAME);

        if (appLaunchPref != null) {
            appLaunchPref.setOnPreferenceClickListener(preference -> {
                showAppListDialog(requireContext());
                return true;
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(Constants.SLIDER_ENABLE_KEY)) {
            boolean value = sharedPreferences.getBoolean(key, false);
            if (!value) {
                sendServiceIntent(requireContext(), 2);
            } else {
                sendServiceIntent(requireContext(), 1);
            }
        } else {
            sendServiceIntent(requireContext(), 1);
        }
        processLayout(sharedPreferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        processLayout(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setAppLaunchLabel(SharedPreferences prefs) {
        String appLaunchPackageName = prefs.getString(Constants.KEY_LAUNCH_APP_NAME, null);
        Preference appLaunchPref = findPreference(Constants.KEY_LAUNCH_APP_NAME);
        String appLabel;

        if (appLaunchPackageName != null) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(appLaunchPackageName, 0);
                appLabel = pm.getApplicationLabel(appInfo).toString();
                appLaunchPref.setSummary(appLabel + " (" + appLaunchPackageName + ")" );
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Application: " + appLaunchPackageName + " is not installed");
                appLaunchPref.setSummary(appLaunchPackageName + " (Not installed)");
            }

        }
    }

    private void processLayout(SharedPreferences prefs) {
        PreferenceScreen screen = getPreferenceScreen();
        boolean mainToggle = prefs.getBoolean(Constants.SLIDER_ENABLE_KEY, false);
        String appLaunchValue = prefs.getString(Constants.KEY_LAUNCH_APP_NAME, null);
        int usage = Integer.parseInt(prefs.getString(Constants.SLIDER_USAGE_KEY, "0"));
        Preference sliderUsage = findPreference(Constants.SLIDER_USAGE_KEY);
        Preference vibrationToggle = findPreference(Constants.VIBRATION_KEY);
        Preference ringerBehavior = findPreference(Constants.RINGER_BEHAVIOR_KEY);
        Preference appLaunch = findPreference(Constants.KEY_LAUNCH_APP_NAME);
        Preference ringerShowDialog = findPreference(Constants.RINGER_SHOW_DIALOG_KEY);
        Preference screenBehaviorCategory = findPreference(Constants.SCREEN_BEHAVIOR_CATEGORY_KEY);
        SwitchPreferenceCompat wakeDevice = findPreference(Constants.WAKE_DEVICE_KEY);

        HashSet<Preference> defaultPrefs = new HashSet<Preference>() {{
            add(sliderUsage);
            add(screenBehaviorCategory);
            add(vibrationToggle);
        }};

        HashSet<Preference> ringerNeeded = new HashSet<Preference>() {{
            add(ringerBehavior);
            add(ringerShowDialog);
        }};

        HashSet<Preference> appLaunchNeeded = new HashSet<Preference>() {{
            add(appLaunch);
        }};

        HashSet<Preference> dynamicPrefs = new HashSet<Preference>();
        dynamicPrefs.addAll(ringerNeeded);
        dynamicPrefs.addAll(appLaunchNeeded);


        if (!mainToggle) {
            boolean hideRest = false;
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference pref = screen.getPreference(i);

                if (Constants.SLIDER_ENABLE_KEY.equals(pref.getKey())) {
                    hideRest = true;
                    continue;
                }

                if (hideRest) {
                    pref.setVisible(false);
                }
            }
            return;
        } else {
            for (Preference pref : defaultPrefs) {
                pref.setVisible(true);
            }
            wakeDevice.setEnabled(true);
            wakeDevice.setChecked(prefs.getBoolean(Constants.WAKE_DEVICE_KEY, true));
        }

        for (Preference pref : dynamicPrefs) {
            pref.setVisible(false);
        }

        switch (usage) {
            case FlashlightAction.ID:
                break;
            case AppLauncher.ID:
                for (Preference pref : appLaunchNeeded) {
                    pref.setVisible(true);
                }
                if (appLaunchValue != null) {
                    setAppLaunchLabel(prefs);
                }
                wakeDevice.setEnabled(false);
                wakeDevice.setChecked(true);
                break;
            case RingerAction.ID:
                for (Preference pref : ringerNeeded) {
                    pref.setVisible(true);
                }
                break;
            default:
                for (Preference pref : dynamicPrefs) {
                    pref.setVisible(false);
            }
        }
    }

    private boolean sendServiceIntent(Context context, int type) {
        Intent intent = new Intent(context, KeyHandler.class);
        switch (type) {
            case 1:
                intent.setAction(Constants.Intent.KEY_MONITOR_RELOAD);
                break;
            case 2:
                intent.setAction(Constants.Intent.KEY_MONITOR_STOP);
                break;
        }
        context.startService(intent);
        return true;
    }


    private void showAppListDialog(Context context) {
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA
                | PackageManager.MATCH_ALL);

        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);

        // Store label + package name pairs
        List<Pair<String, String>> labelPackagePairs = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                String label = pm.getApplicationLabel(app).toString();
                labelPackagePairs.add(new Pair<>(label, app.packageName));
            }
        }

        labelPackagePairs.sort((o1, o2) -> o1.first.compareToIgnoreCase(o2.first));

        List<String> labels = new ArrayList<>();
        List<String> packageNames = new ArrayList<>();

        for (Pair<String, String> pair : labelPackagePairs) {
            String displayLabel = pair.first + " (" + pair.second + ")";
            labels.add(displayLabel);
            packageNames.add(pair.second);
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.app_launch_dialog_title)
                .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                    String selectedPackage = packageNames.get(which);
                    if (!selectedPackage.isEmpty()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.KEY_LAUNCH_APP_NAME, selectedPackage);
                        editor.apply();
                    }

                })
                .show();
    }
}
