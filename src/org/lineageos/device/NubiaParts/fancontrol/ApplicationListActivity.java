package org.lineageos.device.NubiaParts.fancontrol;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;


public class ApplicationListActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG = ApplicationListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                        new ApplicationListFragment(), TAG).commit();

    }
}

