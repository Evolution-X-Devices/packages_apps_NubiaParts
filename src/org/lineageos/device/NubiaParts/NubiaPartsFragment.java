package org.lineageos.device.NubiaParts;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;

import org.lineageos.device.NubiaParts.R;

import java.util.HashSet;

public class NubiaPartsFragment extends SettingsBasePreferenceFragment {

    private final String PREFIX = this.getClass().getPackageName();
    private PreferenceScreen screen;

    private final HashSet<String> NubiaPartsPackages = new HashSet<>() {{
        add(PREFIX + ".fancontrol");
        add(PREFIX + ".gamekeys");
        add(PREFIX + ".gameswitch");
        add(PREFIX + ".power");
    }};

    private HashSet<Object> twoStatePreferences = new HashSet<>();

    PrimarySwitchPreference fanControlPreference;
    PrimarySwitchPreference gameswitchPreference;
    Preference gameKeysPreference;

    PreferenceCategory powerCategory;
    PreferenceCategory buttonsCategory;
    PreferenceCategory fanCategory;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        Context context = requireContext();
        PreferenceManager manager = getPreferenceManager();

        getActivity().setTitle(R.string.nubia_parts_title);

        addPreferencesFromResource(R.xml.parts_main);

        screen = getPreferenceScreen();

        fanCategory = findPreference("nubia_parts_fan_category");
        powerCategory = findPreference("nubia_parts_power_category");
        buttonsCategory = findPreference("nubia_parts_buttons_category");

        for (String pkg : NubiaPartsPackages) {
            String suffix = pkg.substring(pkg.lastIndexOf('.') + 1);
            if (isPackageInstalled(pkg)) {
                switch (suffix) {
                    case "power" -> {
                        ComponentName bypassChargingComponent
                                = ComponentName.unflattenFromString(pkg + "/.ChargingController");
                        SwitchPreferenceCompat bypassChargingToggle = addSwitchPreference(
                                pkg,
                                context.getDrawable(R.drawable.ic_bypass_charging),
                                context.getString(R.string.bypass_charging_title),
                                () -> controlService(bypassChargingComponent, true),
                                () -> controlService(bypassChargingComponent, false)
                        );
                        bypassChargingToggle.setPersistent(false);
                        powerCategory.addPreference(bypassChargingToggle);
                        twoStatePreferences.add(bypassChargingToggle);
                        bypassChargingToggle.setChecked(isServiceRunning(pkg));
                    }
                    case "gameswitch" -> {
                        Intent gameswitchIntent = new Intent();
                        gameswitchIntent.setComponent(new ComponentName(pkg, pkg + ".GameSwitchActivity"));
                        ComponentName gameswitchService = ComponentName.unflattenFromString(pkg + "/.KeyHandler");
                        gameswitchPreference = addPrimarySwitchPreference(pkg,
                                gameswitchIntent,
                                context.getDrawable(R.drawable.ic_unfold),
                                context.getString(R.string.game_switch_title),
                                () -> controlService(gameswitchService, true),
                                () -> controlService(gameswitchService, false)
                                );
                        gameswitchPreference.setPersistent(false);
                        buttonsCategory.addPreference(gameswitchPreference);
                        twoStatePreferences.add(gameswitchPreference);
                        gameswitchPreference.setChecked(isServiceRunning(pkg));
                    }
                    case "fancontrol" -> {
                        Intent fanControlSettings = new Intent();
                        fanControlSettings.setComponent(new ComponentName(
                                pkg,
                                pkg + ".FanSettingsActivity"
                        ));
                        Drawable icon = context.getDrawable(R.drawable.ic_fan);
                        ComponentName fanService = ComponentName.unflattenFromString(pkg + "/.FanService");
                        fanControlPreference =
                                addPrimarySwitchPreference(
                                        pkg,
                                        fanControlSettings,
                                        icon,
                                        context.getString(R.string.fan_control_title),
                                        () -> controlService(fanService, true),
                                        () -> controlService(fanService, false)
                                );
                        fanControlPreference.setChecked(isServiceRunning(pkg));
                        fanControlPreference.setPersistent(false);
                        fanCategory.addPreference(fanControlPreference);
                        twoStatePreferences.add(fanControlPreference);
                    }
                    case "gamekeys" -> {
                        Intent gameKeysIntent = new Intent();
                        gameKeysIntent.setComponent(new ComponentName(
                                pkg,
                                pkg + ".GameKeysActivity"
                        ));
                        Drawable icon = context.getDrawable(R.drawable.ic_gamekeys);
                        gameKeysPreference = addPreference(pkg, gameKeysIntent, icon,
                                context.getString(R.string.game_keys_title));
                        buttonsCategory.addPreference(gameKeysPreference);
                    }
                }

            } else {
                switch (suffix) {
                    case "fancontrol" -> screen.removePreference(fanCategory);
                    case "power" -> screen.removePreference(powerCategory);
                }
            }
        }

        setPreferenceScreen(screen);
    }

    private Preference addPreference(String pkg, Intent intent, Drawable icon, String title) {
        Preference p = new Preference(requireContext());
        if (icon != null) {
            p.setIcon(icon);
        }
        p.setKey(pkg);
        p.setTitle((title == null) ? getPackageLabel(pkg) : title);
        p.setIntent(intent);
        return p;
    }

    private PrimarySwitchPreference addPrimarySwitchPreference(String pkg, Intent intent,
                                                               Drawable icon, String title,
                                             Runnable onEnable, Runnable onDisable) {
        PrimarySwitchPreference psp = new PrimarySwitchPreference(requireContext());
        psp.setKey(pkg);
        if (icon != null) psp.setIcon(icon);
        psp.setTitle((title == null) ? getPackageLabel(pkg) : title);
        psp.setOnPreferenceChangeListener((p, newValue) -> {
            boolean enabled = (Boolean) newValue;

            if (enabled) {
                onEnable.run();
            } else {
                onDisable.run();
            }

            return true;
        });
        psp.setIntent(intent);
        return psp;
    }

    private SwitchPreferenceCompat addSwitchPreference(String pkg, Drawable icon, String title,
                                     Runnable onEnable, Runnable onDisable) {
        SwitchPreferenceCompat sp = new SwitchPreferenceCompat(requireContext());
        sp.setKey(pkg);
        if (icon != null) sp.setIcon(icon);
        sp.setOnPreferenceChangeListener((p, newValue) -> {
            boolean enabled = (Boolean) newValue;

            if (enabled) {
                onEnable.run();
            } else {
                onDisable.run();
            }

            return true;
        });
        sp.setTitle((title == null) ? getPackageLabel(pkg) : title);
        return sp;
    }

    private boolean isPackageInstalled(String packageName) {
        Context context = getContext();
        if (context == null) return false;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String getPackageLabel(String pkg) {
        String label;
        try {
            label = getContext().getPackageManager()
                    .getApplicationLabel(
                            getContext().getPackageManager().getApplicationInfo(pkg, 0)
                    ).toString();
        } catch (PackageManager.NameNotFoundException e) {
            label = pkg;
        }
        return label;
    }

    private boolean isServiceRunning(String packageName) {
        ActivityManager am = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : am.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName)) return true;
        }
        return false;
    }

    private void controlService(ComponentName cn, boolean state) {
        Intent remoteControlIntent = new Intent(state ?
                SharedConstants.Intent.REMOTE_START : SharedConstants.Intent.REMOTE_STOP);
        remoteControlIntent.setComponent(cn);
        requireContext().startService(remoteControlIntent);
    }

    private void reloadSwitches() {
        for (Object o : twoStatePreferences) {
            if (o instanceof PrimarySwitchPreference psp) {
                psp.setChecked(isServiceRunning(psp.getKey()));
            } else if (o instanceof SwitchPreferenceCompat sp) {
                sp.setChecked(isServiceRunning(sp.getKey()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadSwitches();
    }

}
