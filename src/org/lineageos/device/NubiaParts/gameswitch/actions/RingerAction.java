package org.lineageos.device.NubiaParts.gameswitch.actions;

import android.adservices.customaudience.AddCustomAudienceOverrideRequest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import org.lineageos.device.NubiaParts.gameswitch.Constants;
import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;

public class RingerAction extends SwitchControllerBase {
    public static final int ID = 3;

    SharedPreferences prefs;


    final int TOGGLE_VIBRATE = 1;
    final int TOGGLE_SILENT = 2;
    final int CYCLE_ALL = 3;

    int ringerBehavior;
    boolean showDialog;
    AudioManager am;

    private final String TAG = this.getClass().getSimpleName();

    public RingerAction(Context context) {
        super(context);
        prefs = context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE);
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    protected void processAction() {
        switch (ringerBehavior) {
            case TOGGLE_VIBRATE:
                if (getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
                    setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                } else {
                    setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                break;
            case TOGGLE_SILENT: 
                if (getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    setRingerMode(AudioManager.RINGER_MODE_SILENT) ;
                } else {
                    setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                break;
            case CYCLE_ALL:
                cycleRingerMode();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void setup() {
       ringerBehavior = Integer.parseInt(prefs.getString(Constants.RINGER_BEHAVIOR_KEY, "1"));
       showDialog = prefs.getBoolean(Constants.RINGER_SHOW_DIALOG_KEY, false);
    }

    private void setRingerMode(int mode) {
        try {
            am.setRingerMode(mode);
            if (showDialog) {
                am.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            }
        } catch (SecurityException e) {
            Log.e("RingerReceiver", "Failed to set ringer mode", e);
        }
    }

    int[] ringerModes = {
            AudioManager.RINGER_MODE_SILENT,
            AudioManager.RINGER_MODE_VIBRATE,
            AudioManager.RINGER_MODE_NORMAL
    };

    private void cycleRingerMode() {
        int newRingerMode = ringerModes[(getRingerMode() + 1) % ringerModes.length];
        Log.d(TAG, "setting ringer mode to " + newRingerMode);
        setRingerMode(newRingerMode);
    }

    private int getRingerMode() {
    return am.getRingerMode();
    }


}