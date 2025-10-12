package org.lineageos.device.NubiaParts.fancontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.lineageos.device.NubiaParts.Utils.FileUtils;

public class FanController {

    private static final String TAG = FanController.class.getSimpleName();

    public static boolean toggle(boolean mode) {
        FileUtils.writeValue(Constants.FAN_TOGGLE_NODE, (mode) ? "1" : "0");
        return true;
    }

    public static String getSpeed() {
        return FileUtils.readLine(Constants.FAN_SPEED_NODE);
    }

    public static boolean setSpeed(Context context, int speed) {
        if (LockManager.isLocked(context)) {
            Log.w(TAG, "Fan is locked by " + LockManager.getLockReason(context));
            return false;
        }
        FileUtils.writeValue(Constants.FAN_SPEED_NODE, String.valueOf(speed));
        return true;
    }

    public static String getUserSpeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);

        return String.valueOf(prefs.getInt(Constants.USER_FAN_SPEED_KEY, 0));
    }

    public static boolean applyUserSpeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        if (LockManager.isLocked(context)) {
            Log.w(TAG, "Fan is locked by " + LockManager.getLockReason(context));
            return false;
        }
        setSpeed(context, prefs.getInt(Constants.USER_FAN_SPEED_KEY, 1));
        return true;
    }

}
