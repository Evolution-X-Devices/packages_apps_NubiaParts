package org.lineageos.device.NubiaParts.fancontrol;

import android.content.Context;
import android.content.SharedPreferences;

public class LockManager {

    public static void lockFan(Context context, int speed, String reason) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(Constants.FAN_LOCK_KEY, true)
                .putInt("fan_locked_speed", speed)
                .putString(Constants.FAN_LOCK_REASON_KEY, reason)
                .apply();
    }

    public static void unlockFan(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(Constants.FAN_LOCK_KEY, false)
                .remove("fan_locked_speed")
                .remove(Constants.FAN_LOCK_REASON_KEY)
                .apply();
        FanController.applyUserSpeed(context);
        // always return to user speed when there's no locks present.
    }

    public static boolean isLocked(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("fan_locked", false);
    }

    public static int getLockedSpeed(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt("fan_locked_speed", 0);
    }

    public static String getLockReason(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.FAN_LOCK_REASON_KEY, "unknown");
    }

}
