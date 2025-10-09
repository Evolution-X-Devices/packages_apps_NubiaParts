package org.lineageos.device.NubiaParts.fancontrol;


import org.lineageos.device.NubiaParts.Utils.ResourceUtils;

import android.app.Application;

public class FanControl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ResourceUtils.init(this);
        Constants.init(this);
    }
}