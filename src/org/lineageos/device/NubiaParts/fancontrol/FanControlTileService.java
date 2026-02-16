package org.lineageos.device.NubiaParts.fancontrol;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.fancontrol.Constants;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FanControlTileService extends TileService {

    private final Context context = this;
    private SharedPreferences prefs;

    private Handler mainHandler;
    private Executor executor;

    private static final String TAG = FanControlTileService.class.getSimpleName();


    @Override
    public void onCreate() {
        ResourceUtils.init(getApplicationContext());
        Constants.init(getApplicationContext());
        prefs = getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setLabel(context.getString(R.string.tile_title));
        if (FanController.getSpeed() != null) {
            refreshTile(prefs.getBoolean(Constants.USER_ENABLE_FAN_KEY, false)
                    && FanController.isEnabled());
        } else {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.updateTile();
        }

    }

    @Override
    public void onClick() {
            Tile tile = getQsTile();
            if (tile.getState() == Tile.STATE_ACTIVE) {
                prefs.edit().putBoolean(Constants.USER_ENABLE_FAN_KEY, false).apply();
                Intent intent = new Intent(context, FanService.class);
                intent.setAction(Constants.INTENT_FAN_STOP);
                context.startService(intent);
                refreshTile(false);
            } else {
                prefs.edit().putBoolean(Constants.USER_ENABLE_FAN_KEY, true).apply();
                Intent intent = new Intent(context, FanService.class);
                intent.setAction(Constants.INTENT_FAN_RELOAD);
                context.startService(intent);
                refreshTile(true);
            }
    }
    
    private void refreshTile(boolean state) {
        Tile tile = getQsTile();
        if (state) {
            executor.execute(() -> {
                String speed = FanController.getSpeed();
                mainHandler.post(() -> {
                    tile.setSubtitle("On (Speed: " + speed + ")");
                    tile.setState(Tile.STATE_ACTIVE);
                    tile.updateTile();
                });
            });
        } else {
            tile.setSubtitle("Off");
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
        }

    }

}

