package org.lineageos.device.NubiaParts.gameswitch.actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.lineageos.device.NubiaParts.gameswitch.Constants;
import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;

public class FlashlightAction extends SwitchControllerBase {

    public static final int ID = 2;

    private final String TAG = this.getClass().getSimpleName();

    private boolean mTorchEnabled = false;


    public FlashlightAction(Context context) {
        super(context);
    }

    @Override
    protected void processAction() {
            toggleTorch();
        }
    @Override
    public void reset() {
        setTorchMode(false);
    }

    @Override
    public void setup() {

    }

    private void setTorchMode(boolean torchState) {
        Intent torchIntent = new Intent();
        torchIntent.setComponent(new ComponentName(Constants.HELPER_PACKAGE_NAME,
                Constants.HELPER_PACKAGE_NAME + ".CameraHelper"));
        torchIntent.setAction(Constants.Intent.ACTION_SET_TORCH);
        torchIntent.putExtra("mode", torchState);
        Log.d(TAG, "Calling helper for torch!");
        mContext.sendBroadcast(torchIntent);
        mTorchEnabled = !mTorchEnabled;
    }

    private void toggleTorch() {
        setTorchMode(!mTorchEnabled);
    }


}
