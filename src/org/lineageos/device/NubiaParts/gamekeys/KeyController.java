package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.lineageos.device.NubiaParts.Utils.FileUtils;
import org.lineageos.device.NubiaParts.gamekeys.Constants;

public class KeyController {

    private static final String TAG = KeyController.class.getSimpleName();

    public static void setKeyMode(boolean leftMode, boolean rightMode) {
        if (leftMode) {
            FileUtils.writeValue(Constants.LEFT_SHOULDER_MODE, String.valueOf(Constants.WAKE_MODE_INT));
        } else {
            FileUtils.writeValue(Constants.LEFT_SHOULDER_MODE, String.valueOf(Constants.SLEEP_MODE_INT));
        }

        if (rightMode) {
            FileUtils.writeValue(Constants.RIGHT_SHOULDER_MODE, String.valueOf(Constants.WAKE_MODE_INT));
        } else {
            FileUtils.writeValue(Constants.RIGHT_SHOULDER_MODE, String.valueOf(Constants.SLEEP_MODE_INT));
        }

        Log.d(TAG, Constants.LEFT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.LEFT_SHOULDER_MODE));
        Log.d(TAG, Constants.RIGHT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.RIGHT_SHOULDER_MODE));
    }

    public static void initializeKey(int key, int sensitivity) {
        switch (key) {
            case 0 -> setLeftKeyMode(true);
            case 1 -> setRightKeyMode(true);
        }
        setSensitivity(key, sensitivity);
    }

    public static void setLeftKeyMode(boolean mode) {
        if (mode) {
            FileUtils.writeValue(Constants.LEFT_SHOULDER_MODE,
                    String.valueOf(Constants.WAKE_MODE_INT));
        } else {
            FileUtils.writeValue(Constants.LEFT_SHOULDER_MODE,
                    String.valueOf(Constants.SLEEP_MODE_INT));
        }

        Log.d(TAG, Constants.LEFT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.LEFT_SHOULDER_MODE));
    }

    public static void setRightKeyMode(boolean mode) {
        if (mode) {
            FileUtils.writeValue(Constants.RIGHT_SHOULDER_MODE,
                    String.valueOf(Constants.WAKE_MODE_INT));
        } else {
            FileUtils.writeValue(Constants.RIGHT_SHOULDER_MODE,
                    String.valueOf(Constants.SLEEP_MODE_INT));
        }

        Log.d(TAG, Constants.RIGHT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.RIGHT_SHOULDER_MODE));
    }

    public static boolean getKeyMode(int key) {
        String sb = switch (key) {
            case 0 -> Constants.LEFT_SHOULDER_MODE;
            case 1 -> Constants.RIGHT_SHOULDER_MODE;
            default -> "";
        };
        return FileUtils.readLine(sb).startsWith("mode" + " : "
                + Constants.WAKE_MODE_INT) ;
    }


    public static void sleep() {
        FileUtils.writeValue(Constants.LEFT_SHOULDER_MODE,
                String.valueOf(Constants.SLEEP_MODE_INT));
        Log.d(TAG, Constants.LEFT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.LEFT_SHOULDER_MODE));
        FileUtils.writeValue(Constants.RIGHT_SHOULDER_MODE,
                String.valueOf(Constants.SLEEP_MODE_INT));
        Log.d(TAG, Constants.RIGHT_SHOULDER_MODE + " value is "
                + FileUtils.readLine(Constants.RIGHT_SHOULDER_MODE));
    }

    public static void setSensitivity(int key, int level) {
        String sb = switch (key) {
            case 0 -> Constants.LEFT_SHOULDER_SENS;
            case 1 -> Constants.RIGHT_SHOULDER_SENS;
            default -> "";
        };
        if (!sb.isEmpty()) FileUtils.writeValue(sb, String.valueOf(level));
    }

    public static int getSensitivity(int key) {
        String sens = switch (key) {
            case 0 -> Constants.LEFT_SHOULDER_SENS;
            case 1 -> Constants.RIGHT_SHOULDER_SENS;
            default -> "";
        };
        return Integer.parseInt(FileUtils.readLine(sens));
    }

    public static void restoreState(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.Prefs.PREF_KEY, Context.MODE_PRIVATE);
        setKeyMode(prefs.getBoolean(Constants.Prefs.KEY_LEFT_MODE, false),
                prefs.getBoolean(Constants.Prefs.KEY_RIGHT_MODE, false));
    }


}
