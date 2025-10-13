package org.lineageos.device.NubiaParts.gameswitch;


import android.os.Bundle;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class GameSwitchActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG = "GameSwitch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                new GameSwitchSettings(), TAG).commit();

    }

}
