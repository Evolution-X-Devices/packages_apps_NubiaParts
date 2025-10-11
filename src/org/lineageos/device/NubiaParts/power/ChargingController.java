package org.lineageos.device.NubiaParts.power;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.service.quicksettings.TileService;

import org.lineageos.device.NubiaParts.Utils.FileUtils;

import java.util.Objects;

public class ChargingController extends BroadcastReceiver {

    public static boolean isChargingBypassed() {
        return FileUtils.getFileValue(Constants.BYPASS_CHARGING_NODE, Constants.BYPASS_CHARGING_DISABLED_VALUE)
                .matches(Constants.BYPASS_CHARGING_ENABLED_VALUE);
    }

    public static void setBypass(boolean state) {
        if (state) {
            FileUtils.writeValue(Constants.BYPASS_CHARGING_NODE,
                    Constants.BYPASS_CHARGING_ENABLED_VALUE);
        } else {
            FileUtils.writeValue(Constants.BYPASS_CHARGING_NODE,
                    Constants.BYPASS_CHARGING_DISABLED_VALUE);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        TileService.requestListeningState(context,
                new ComponentName(context, BypassChargingTile.class));
        if (Objects.equals(intent.getAction(), Constants.Intent.NOTIFICATION_TAPPED)) {
            if (isChargingBypassed()) {
                setBypass(false);
            }
            NotificationHandler.clearPersistentNotification(context.getApplicationContext());
        }
    }


}
