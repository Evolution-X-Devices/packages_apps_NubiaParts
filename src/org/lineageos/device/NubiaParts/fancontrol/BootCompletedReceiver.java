package org.lineageos.device.NubiaParts.fancontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootCompletedReceiver extends BroadcastReceiver {

    private boolean userWantsFanEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.USER_ENABLE_FAN_KEY, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (userWantsFanEnabled(context)) {
                context.startService(new Intent(context, FanService.class));
        }

//            if (auto) {
//                context.startService(new Intent(context, ThermalMonitor.class));
//            }
        }
    }

}

