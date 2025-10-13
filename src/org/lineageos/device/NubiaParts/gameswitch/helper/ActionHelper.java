package org.lineageos.device.NubiaParts.gameswitch.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class ActionHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("ACTION_SET_RINGER".equals(intent.getAction())) {
            int mode = intent.getIntExtra("mode", AudioManager.RINGER_MODE_NORMAL);
            boolean showDialog = intent.getBooleanExtra("showDialog", false);
            AudioManager am = context.getSystemService(AudioManager.class);
            try {
                am.setRingerMode(mode);
                if (showDialog) {
                    am.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                }
            } catch (SecurityException e) {
                Log.e("RingerReceiver", "Failed to set ringer mode", e);
            }
        }
    }
}
