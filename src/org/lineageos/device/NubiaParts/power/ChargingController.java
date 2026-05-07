package org.lineageos.device.NubiaParts.power;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.lineageos.device.NubiaParts.SharedConstants;
import org.lineageos.device.NubiaParts.Utils.FileUtils;

import java.util.Objects;

public class ChargingController extends Service {

    public static boolean isChargingBypassed() {
        return FileUtils.getFileValue(Constants.BYPASS_CHARGING_NODE, Constants.BYPASS_CHARGING_DISABLED_VALUE)
                .equals(Constants.BYPASS_CHARGING_ENABLED_VALUE);
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isChargingBypassed()) {
            setBypass(true);
            NotificationHandler.showPersistentNotification(getApplicationContext());
        } else {
            setBypass(false);
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (Objects.equals(action, SharedConstants.Intent.REMOTE_STOP)
                || Objects.equals(action, Constants.Intent.NOTIFICATION_TAPPED)
                || (Objects.equals(action, SharedConstants.Intent.PARTS_SWITCH_CHANGED)
                && !intent.getBooleanExtra("state", false))) {

            setBypass(false);
            NotificationHandler.clearPersistentNotification(getApplicationContext());
            stopSelf();

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationHandler.clearPersistentNotification(getApplicationContext());
        super.onDestroy();
    }

}
