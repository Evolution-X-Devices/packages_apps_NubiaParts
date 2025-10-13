package org.lineageos.device.NubiaParts.gameswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class BootCompletedReceiver extends BroadcastReceiver {

    private boolean userWantsServiceEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.SLIDER_ENABLE_KEY, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (userWantsServiceEnabled(context)) {
            context.startService(new Intent(context, KeyHandler.class));
        }

    }
}
