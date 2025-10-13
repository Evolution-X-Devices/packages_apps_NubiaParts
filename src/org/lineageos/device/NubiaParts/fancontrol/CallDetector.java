package org.lineageos.device.NubiaParts.fancontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CallDetector extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        boolean stopOnCall = prefs.getBoolean(Constants.MONITOR_CALL_KEY, false);


        if (stopOnCall && TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                Log.d(TAG, "Call answered or outgoing -> Pause fan");
                FanController.toggle(false);
            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                Log.d(TAG, "Call ended / idle -> Resume fan");
                FanController.toggle(true);
            }
        }
    }
}
