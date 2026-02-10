package org.lineageos.device.NubiaParts.gameswitch.actions;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.service.notification.Condition;
import android.util.Log;

import org.lineageos.device.NubiaParts.gameswitch.Constants;
import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;


public class DNDModesAction extends SwitchControllerBase {
    public static final int ID = 5;

    SharedPreferences prefs;


    NotificationManager nm;

    private final String TAG = this.getClass().getSimpleName();


    public DNDModesAction(Context context) {
        super(context);
        prefs = context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE);
        nm = context.getSystemService(NotificationManager.class);
    }

    @Override
    protected void processAction() {

        String pref = prefs.getString(Constants.DND_BEHAVIOR_KEY,
                String.valueOf(Settings.Global.ZEN_MODE_OFF));

        int mode = Integer.parseInt(pref);
        int filter = switch (mode) {
            case Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS ->
                    NotificationManager.INTERRUPTION_FILTER_PRIORITY;
            case Settings.Global.ZEN_MODE_ALARMS ->
                    NotificationManager.INTERRUPTION_FILTER_ALARMS;
            case Settings.Global.ZEN_MODE_NO_INTERRUPTIONS ->
                    NotificationManager.INTERRUPTION_FILTER_NONE;
            default -> NotificationManager.INTERRUPTION_FILTER_ALL;
        };

        if (nm.getCurrentInterruptionFilter() == filter) {
            filter = NotificationManager.INTERRUPTION_FILTER_ALL;
        }

        nm.setInterruptionFilter(filter);

    }


    @Override
    public void reset() {

    }

    @Override
    public void setup() {
    }







}
