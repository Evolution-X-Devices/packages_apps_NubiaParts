package org.lineageos.device.NubiaParts.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import org.lineageos.device.NubiaParts.Utils.FileUtils;
import org.lineageos.device.NubiaParts.Utils.ResourceUtils;


public class BypassChargingTile extends TileService {

    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean plugged = Intent.ACTION_POWER_CONNECTED.equals(intent.getAction());
            updatePluggedState(plugged);
        }
    };

    private void updatePluggedState(boolean plugged) {
        if (!plugged) {
            getQsTile().setState(Tile.STATE_UNAVAILABLE);
        } else if (!ChargingController.isChargingBypassed()) {
            getQsTile().setState(Tile.STATE_INACTIVE);
        }
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening() {
        ResourceUtils.init(this);
        Tile tile = getQsTile();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(powerReceiver, filter);

        if (!FileUtils.fileWritable(Constants.BYPASS_CHARGING_NODE)) {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.setSubtitle(ResourceUtils.getString("tile_unavailable"));
        } else if (ChargingController.isChargingBypassed()) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_enabled"));
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_disabled"));
        }
        tile.updateTile();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        ChargingController.setBypass(!ChargingController.isChargingBypassed());
        if (ChargingController.isChargingBypassed()) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_enabled"));
            NotificationHandler.showPersistentNotification(getApplicationContext());
        } else {
            NotificationHandler.clearPersistentNotification(getApplicationContext());
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(ResourceUtils.getString("tile_disabled"));
        }
        tile.updateTile();
    }

    @Override
    public void onStopListening() {
        unregisterReceiver(powerReceiver);
        super.onStopListening();
    }

 }
