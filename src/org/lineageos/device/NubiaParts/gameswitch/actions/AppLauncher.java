package org.lineageos.device.NubiaParts.gameswitch.actions;

import android.app.IActivityTaskManager;
import android.app.ActivityTaskManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import org.lineageos.device.NubiaParts.gameswitch.Constants;
import org.lineageos.device.NubiaParts.gameswitch.SwitchControllerBase;
import org.lineageos.device.NubiaParts.gameswitch.UnlockTrampolineActivity;

import java.util.List;

public class AppLauncher extends SwitchControllerBase {

    public static final int ID = 1;
    private final String TAG = this.getClass().getSimpleName();

    SharedPreferences mPrefs;

    private String focusedPackage = "";
    String previousPackage = "";


    private String requestedPackage = "";


    KeyguardManager km;
    PackageManager pm;

    Context mContext;

    public AppLauncher(Context context) {
        super(context);
        pm = context.getPackageManager();
        km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mPrefs = context.getApplicationContext().getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);
        mContext = context;
    }

    protected void processAction() {
        if (!requestedPackage.isEmpty()) {
            getFocusedPkg();
            if (focusedPackage.equals(requestedPackage) && !isDeviceLocked()) {
                exitTask();
            } else {
                launchPackage(requestedPackage);
            }
        } else {
            Log.w(TAG, "Action triggered but no package is configured to launch.");
        }
    }

    @Override
    public void reset() {
        focusedPackage = "";
    }

    @Override
    public void setup() {
        requestedPackage = mPrefs.getString(Constants.KEY_LAUNCH_APP_NAME, "");
    }

    private void launchPackage(String pkg) {
        Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            Log.w(TAG, "No main activity found for package: " + pkg);
            return;
        }
        if (km != null && isDeviceLocked()) {
            Intent trampoline = new Intent(mContext, UnlockTrampolineActivity.class);
            trampoline.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            trampoline.putExtra(
                    UnlockTrampolineActivity.EXTRA_TARGET_INTENT,
                    launchIntent
            );
            mContext.startActivity(trampoline);
        } else {
            mContext.startActivity(launchIntent);
        }
    }

    private void getFocusedPkg() {
        try {
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (tasks != null && !tasks.isEmpty()) {
                focusedPackage = tasks.getFirst().topActivity.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            focusedPackage = "";
        }
    }

    private void exitTask() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    private boolean isDeviceLocked() {
       return km.isKeyguardLocked();
    }
}
