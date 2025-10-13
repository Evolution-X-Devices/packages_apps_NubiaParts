package org.lineageos.device.NubiaParts.gameswitch;

/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

public abstract class SwitchControllerBase {

    private final String TAG = this.getClass().getSimpleName();

    protected final Context mContext;

    private Vibrator mVibrator;


    public SwitchControllerBase(Context context) {
        mContext = context;
    }

    protected abstract void processAction();

    public abstract void reset();

    public abstract void setup();


}