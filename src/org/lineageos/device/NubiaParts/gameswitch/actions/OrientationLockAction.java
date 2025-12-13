package org.lineageos.device.NubiaParts.gameswitch.actions;

import android.content.Context;
import android.provider.Settings;
import com.android.internal.view.RotationPolicy;

import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;

public class OrientationLockAction extends SwitchControllerBase {
    public static final int ID = 4;

    private final Context mContext;
    private final String TAG = this.getClass().getSimpleName();
    public OrientationLockAction(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void processAction() {
        setRotationLocked(!isRotationLocked());
    }

    @Override
    public void reset() {

    }

    @Override
    public void setup() {

    }

    public void setRotationLocked(boolean locked) {
        RotationPolicy.setRotationLock(mContext, locked, TAG);
    }

    public boolean isRotationLocked() {
        return RotationPolicy.isRotationLocked(mContext);
    }
}
