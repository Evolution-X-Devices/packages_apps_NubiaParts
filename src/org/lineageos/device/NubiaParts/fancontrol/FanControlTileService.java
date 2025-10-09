package org.lineageos.device.NubiaParts.fancontrol;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.fancontrol.Constants;

public class FanControlTileService extends TileService {

    private final Context context = this;

    private static final String TAG = FanControlTileService.class.getSimpleName();


    @Override
    public void onCreate() {
        ResourceUtils.init(this);
        Constants.init(this);
    }

    @Override
    public void onStartListening() {
         SharedPreferences prefs = getApplicationContext().getSharedPreferences(
            Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        Tile tile = getQsTile();
        tile.setLabel(context.getString(R.string.tile_title));
        if (FanController.getSpeed() != null) {
            if (prefs.getBoolean(Constants.USER_ENABLE_FAN_KEY, false)) {
                tile.setState(Tile.STATE_ACTIVE);
                tile.setSubtitle("On (Speed: " + FanController.getSpeed() + ")");
            } else {
                tile.setState(Tile.STATE_INACTIVE);
                tile.setSubtitle("Off");  
            }
        } else {
            tile.setState(Tile.STATE_UNAVAILABLE);
        }
        tile.updateTile();
    }


    @Override
    public void onClick() {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(
            Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
            Tile tile = getQsTile();
            if (tile.getState() == Tile.STATE_ACTIVE) {
                prefs.edit().putBoolean(Constants.USER_ENABLE_FAN_KEY, false).apply();
                Intent intent = new Intent(context, FanService.class);
                intent.setAction(Constants.INTENT_FAN_STOP);
                context.startService(intent);
                tile.setSubtitle("Off");  
                tile.setState(Tile.STATE_INACTIVE);
            } else {
                prefs.edit().putBoolean(Constants.USER_ENABLE_FAN_KEY, true).apply();
                Intent intent = new Intent(context, FanService.class);
                intent.setAction(Constants.INTENT_FAN_RELOAD);
                context.startService(intent);
                tile.setSubtitle("On (Speed: " + FanController.getSpeed() + ")");
                tile.setState(Tile.STATE_ACTIVE);
            }
            tile.updateTile();
    }

}

