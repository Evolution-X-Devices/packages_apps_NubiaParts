package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.content.Intent;
import android.util.Log;

import org.lineageos.device.NubiaParts.Utils.FileUtils;
import static org.lineageos.device.NubiaParts.Utils.ResourceUtils.*;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.gamekeys.Constants;
import org.lineageos.device.NubiaParts.gamekeys.KeyController;
import org.lineageos.device.NubiaParts.gamekeys.ScreenStateReceiver;

public class GameKeyTileService extends TileService {

    private final Context context = this;
    private static boolean isRegistered = false;

    SharedPreferences prefs;

    
    private static final String TAG = GameKeyTileService.class.getSimpleName();


    private void registerScreenReceiver(Context context) {
        ScreenStateReceiver screenReceiver = new ScreenStateReceiver();
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, filter);
            isRegistered = true;
            Log.d(TAG, "Screen receiver registered");
        }
    }

    private void unregisterScreenReceiver(Context context) {
        ScreenStateReceiver screenReceiver = new ScreenStateReceiver();
        if (isRegistered) {
            try {
                unregisterReceiver(screenReceiver);
                isRegistered = false;
                Log.d(TAG, "Screen receiver unregistered");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Receiver was not registered: " + e.getMessage());
            }
        }
    }

    @Override
    public void onStartListening() {
        prefs = getApplicationContext().getSharedPreferences(
            Constants.Prefs.PREF_KEY, Context.MODE_PRIVATE);
        ResourceUtils.init(getApplicationContext());
        Tile tile = getQsTile();
        tile.setLabel("Shoulder Buttons");
        if (FileUtils.readLine(Constants.LEFT_SHOULDER_MODE).isEmpty()
        || FileUtils.readLine(Constants.RIGHT_SHOULDER_MODE).isEmpty()) {
            tile.setState(Tile.STATE_UNAVAILABLE);
        }

        boolean leftState = KeyController.getKeyMode(0);
        boolean rightState = KeyController.getKeyMode(1);

        if (leftState && rightState) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (L+R)");
        } else if (leftState) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (L)");
        } else if (rightState) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (R)");
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_state_disabled_subtitle"));
        }

        tile.updateTile();
    }

    @Override
    public void onClick() {
            cycleState();
    }

    private void cycleState() {
        boolean leftState = KeyController.getKeyMode(0);
        boolean rightState = KeyController.getKeyMode(1);
        boolean newLeftState = leftState;
        boolean newRightState = rightState;
        Tile tile = getQsTile();

        if (!leftState && !rightState) {
            newLeftState = true;
            newRightState = true; // L + R
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (L+R)");
        } else if (leftState && rightState) {
            newRightState = false; // L only
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (L)");
        } else if (leftState) {
            newLeftState = false;
            newRightState = true; // R only
            tile.setSubtitle(ResourceUtils.getString("tile_state_enabled_subtitle") + " (R)");
        } else {
            newRightState = false; // OFF
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_state_disabled_subtitle"));
            unregisterScreenReceiver(this);
        }

        if (newRightState || newLeftState) {
            tile.setState(Tile.STATE_ACTIVE);
            registerScreenReceiver(this);
        }

        KeyController.setKeyMode(newLeftState, newRightState);
        prefs.edit().putBoolean(Constants.Prefs.KEY_LEFT_MODE, newLeftState)
                .putBoolean(Constants.Prefs.KEY_RIGHT_MODE, newRightState).apply();
            tile.updateTile();
    }


}

