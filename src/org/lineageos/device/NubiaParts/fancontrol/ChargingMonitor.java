package org.lineageos.device.NubiaParts.fancontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;

public class ChargingMonitor extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    private boolean fanBoosted = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            Log.d(TAG, "Power connected");
            boostFan(true, context);
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            Log.d(TAG, "Power disconnected");
            boostFan(false, context);
        }

    }

    private void boostFan(boolean bool, Context context) {
        if (bool) {
            FanController.setSpeed(context, Constants.FAN_SPEED_MAX);
            fanBoosted = true;
        } else {
                FanController.applyUserSpeed(context);
                fanBoosted = false;
            }
        }
}




