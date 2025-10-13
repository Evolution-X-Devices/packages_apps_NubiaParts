package org.lineageos.device.NubiaParts.gameswitch.actions;

import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;


public class ScreenshotAction extends SwitchControllerBase {

    private final String TAG = this.getClass().getSimpleName();
    public static final int ID = 4;

    public ScreenshotAction(Context context) {
        super(context);
    }

    @Override
    protected void processAction() {
        long now = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SYSRQ, 0);
        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SYSRQ, 0);

        InputManager im = InputManager.getInstance();
        im.injectInputEvent(down, 0);
        im.injectInputEvent(up, 0);
    }

    @Override
    public void reset() {

    }

    @Override
    public void setup() {

    }

}
