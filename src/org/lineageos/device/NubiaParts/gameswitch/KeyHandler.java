package org.lineageos.device.NubiaParts.gameswitch;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;


import org.lineageos.device.NubiaParts.gameswitch.actions.*;

public class KeyHandler extends AccessibilityService {
    

    private static FlashlightAction mFlashlightAction;
    private static OrientationLockAction mOrientationLockAction;
    private static RingerAction mRingerAction;

    private static AppLauncher mAppLauncher;

    private static ScreenshotAction mScreenshotAction;


    private static SwitchControllerBase mSwitchController;

    private static int usage = 0;
    private static boolean running = false;
    private boolean ignoreKeys = false;

    private static boolean vibrationEnabled;

    private static SharedPreferences mPrefs;
    private Vibrator mVibrator;
    private Context mContext;


    private final String TAG = this.getClass().getSimpleName();


    public KeyHandler() {

    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        String serviceId = context.getPackageName() + "/" + KeyHandler.class.getName();
            String settingValue = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
                colonSplitter.setString(settingValue);
                for (String service : colonSplitter) {
                    if (service.equalsIgnoreCase(serviceId)) {
                        return true;
                    }
                }
            }
        return false;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
        boolean handlerPref = mPrefs.getBoolean(Constants.SLIDER_ENABLE_KEY, false);
        if (handlerPref && !isAccessibilityServiceEnabled(mContext)) {
            Log.d(TAG, "User disabled accessibility service");
            mPrefs.edit().putBoolean(Constants.SLIDER_ENABLE_KEY, false).apply();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mFlashlightAction = new FlashlightAction(mContext);
        mRingerAction = new RingerAction(mContext);
        mAppLauncher = new AppLauncher(mContext);
        mScreenshotAction = new ScreenshotAction(mContext);
        mOrientationLockAction = new OrientationLockAction(mContext);

        VibratorManager vm = (VibratorManager) mContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        mVibrator = vm.getDefaultVibrator();

        mPrefs = mContext.getApplicationContext().getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, filter);
        init();
    }

    private final BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                ignoreKeys = true;
                Log.d(TAG, "ignoring events temporarily due to screen wake");
                new Handler(Looper.getMainLooper()).postDelayed(() -> ignoreKeys = false, 1950);
            }
        }
    };

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (running && !ignoreKeys) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_PROG_GREEN -> {
                        Log.d(TAG, "Switch moved up");
                        processAction();
                        return true;
                    }
                    case KeyEvent.KEYCODE_PROG_RED -> {
                        Log.d(TAG, "Switch moved down");
                        processAction();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void doHapticFeedback() {
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }


    private void processAction() {
        mSwitchController.processAction();
        if (vibrationEnabled) doHapticFeedback();
    }

    static void init() {

        if (mSwitchController != null) {
            mSwitchController.reset();
        }

        usage = Integer.parseInt(mPrefs.getString(Constants.SLIDER_USAGE_KEY, "0"));

        switch (usage) {
            case FlashlightAction.ID:
                mSwitchController = mFlashlightAction;
                mSwitchController.setup();
                break;
            case AppLauncher.ID:
                mSwitchController = mAppLauncher;
                mSwitchController.setup();
                break;
            case RingerAction.ID:
                mSwitchController = mRingerAction;
                mSwitchController.setup();
                break;
            case ScreenshotAction.ID:
                mSwitchController = mScreenshotAction;
                mSwitchController.setup();
                break;
            case OrientationLockAction.ID:
                mSwitchController = mOrientationLockAction;
                mSwitchController.setup();
                break;
        }

        vibrationEnabled = mPrefs.getBoolean(Constants.VIBRATION_KEY, true);
        running = true;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (Constants.Intent.KEY_MONITOR_RELOAD.equals(action)) {
                Log.d(TAG, "Starting key monitor");
                init();
            }

            if (Constants.Intent.KEY_MONITOR_STOP.equals(action)) {
                Log.d(TAG, "STOP intent caught!");
                running = false;
                stopSelf();
            }

        } else {
            Log.w(TAG, "Intent or action is null, Starting service normally");
            init();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(screenStateReceiver);
        } catch (IllegalArgumentException ignored) {
            Log.d(TAG, "Screen state receiver is already gone or not registered.");
        }
        super.onDestroy();
    }
}

