package org.lineageos.device.NubiaParts.fancontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.preference.PreferenceScreen;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.android.settingslib.widget.SettingsBasePreferenceFragment;
import com.android.settingslib.widget.SliderPreference;

import java.util.List;


public class ApplicationListFragment extends SettingsBasePreferenceFragment  {

    private PreferenceScreen appScreen;
    private SharedPreferences mPrefs;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        Context context = getPreferenceManager().getContext();
        appScreen = getPreferenceManager().createPreferenceScreen(context);
        getActivity().setTitle(R.string.per_app_fan_speed_title);

        mPrefs = context.getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);

        PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(Constants.FAN_PREF_NAME);
        prefManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

        Context ctx = prefManager.getContext();

        buildApplicationList(ctx);

        setPreferenceScreen(appScreen);
    }

    private void buildApplicationList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchableApps = pm.queryIntentActivities(launcherIntent, 0);

        launchableApps.sort((a, b) -> {
            String labelA = a.loadLabel(pm).toString();
            String labelB = b.loadLabel(pm).toString();
            return labelA.compareToIgnoreCase(labelB);
        });


        for (ResolveInfo info : launchableApps) {
            String packageName = info.activityInfo.packageName;
            String label = info.loadLabel(pm).toString();
            Drawable icon = info.loadIcon(pm);

            SliderPreference sp = new SliderPreference(context);
            sp.setMax(Constants.FAN_SPEED_MAX);
            sp.setMin(Constants.FAN_SPEED_MIN);
            sp.setKey(packageName);
            sp.setTitle(" " + label);
            sp.setIcon(icon);
            sp.setSliderIncrement(1);
            sp.setUpdatesContinuously(true);
            sp.setHapticFeedbackMode(SliderPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS);
            sp.setTickVisible(true);
            sp.setShowSliderValue(false);
            sp.setOnPreferenceChangeListener((preference, newValue) -> {
                int value = (int) newValue;
                setSliderSummary(value, preference);
                return true;
            });
            appScreen.addPreference(sp);
            setSliderSummary(sp.getValue(), sp);
        }
    }

    private void setSliderSummary(int val, Preference p) {
        if (val == 0) {
            p.setSummary(" Default");
        } else {
            p.setSummary(" " + val);
        }
    }

}
