package org.lineageos.device.NubiaParts.gameswitch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.ServiceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import org.lineageos.device.NubiaParts.SharedConstants;
import org.lineageos.device.NubiaParts.gameswitch.actions.*;
import org.lineageos.device.NubiaParts.gameswitch.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class KeyHandler extends Service {
    
    private static FlashlightAction mFlashlightAction;
    private static OrientationLockAction mOrientationLockAction;
    private static RingerAction mRingerAction;

    private static AppLauncher mAppLauncher;
    private static DNDModesAction mDNDModesAction;

    private static SwitchControllerBase mSwitchController;

    private int usage = 0;
    private static boolean running = false;

    private Runnable pollRunnable;
    private static Handler mainHandler;
    private int currentState = -1;

    private static String SYSFS_PATH;

    private static boolean vibrationEnabled;
    private static boolean shouldWake;
    private static boolean screenOffEnabled;

    private static SharedPreferences mPrefs;
    private static IPowerManager ipm;
    private static Vibrator mVibrator;

    private static final String TAG = KeyHandler.class.getSimpleName();

    public KeyHandler() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFlashlightAction = new FlashlightAction(this);
        mRingerAction = new RingerAction(this);
        mAppLauncher = new AppLauncher(this);
        mOrientationLockAction = new OrientationLockAction(this);
        mDNDModesAction = new DNDModesAction(this);

        VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        mVibrator = vm.getDefaultVibrator();

        IBinder b = ServiceManager.getService(Context.POWER_SERVICE);
        ipm = IPowerManager.Stub.asInterface(b);

        mPrefs = getApplicationContext().getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);

        SYSFS_PATH = getApplicationContext().getResources().getString(R.string.switch_sysfs_path);

        mainHandler = new Handler(Looper.getMainLooper());

        init();
    }

    private static int readState() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SYSFS_PATH))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            Log.e(TAG, "Failed to read switch state", e);
        }
        return -1;
    }

    private void startMonitoring() {
        currentState = readState();
        Log.d(TAG, "Initial game switch state: " + currentState);

        pollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                int newState = readState();
                if (newState != -1 && newState != currentState) {
                    currentState = newState;
                    Log.d(TAG, "Game switch changed to: " + newState);
                    processAction(newState);
                }
                mainHandler.postDelayed(this, 100);
            }
        };

        mainHandler.post(pollRunnable);
        running = true;
        Log.d(TAG, "Started polling game switch");
    }

    private void stopMonitoring() {
        if (pollRunnable != null) {
            running = false;
            mainHandler.removeCallbacks(pollRunnable);
            Log.d(TAG, "Switch monitoring stopped");
        }
    }

    private static void doHapticFeedback() {
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    private void processAction(int state) {
        if (usage != 0) {
            if (shouldWake) wakeScreen();
            mSwitchController.processAction();
            if (vibrationEnabled) doHapticFeedback();
        }
    }

    private void init() {

        if (mSwitchController != null) {
            mSwitchController.reset();
        }

        screenOffEnabled = mPrefs.getBoolean(Constants.HANDLE_SCREEN_OFF_KEY, true);

        usage = Integer.parseInt(mPrefs.getString(Constants.SLIDER_USAGE_KEY, "0"));

        switch (usage) {
            case FlashlightAction.ID:
                mSwitchController = mFlashlightAction;
                mSwitchController.setup();
                break;
            case AppLauncher.ID:
                mSwitchController = mAppLauncher;
                mSwitchController.setup();
                shouldWake = true;
                break;
            case RingerAction.ID:
                mSwitchController = mRingerAction;
                mSwitchController.setup();
                break;
            case OrientationLockAction.ID:
                mSwitchController = mOrientationLockAction;
                mSwitchController.setup();
                break;
            case DNDModesAction.ID:
                mSwitchController = mDNDModesAction;
                break;
        }

        if (mSwitchController != mAppLauncher) {
            shouldWake = (mPrefs.getBoolean(Constants.WAKE_DEVICE_KEY, false) && screenOffEnabled);
        }

        if (!screenOffEnabled) {
            registerScreenReceiver(this);
        } else {
            unregisterScreenReceiver(this);
        }

        vibrationEnabled = mPrefs.getBoolean(Constants.VIBRATION_KEY, true);
        startMonitoring();
    }

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (!running) {
                    startMonitoring();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                stopMonitoring();
            }
        }
    };

    private void registerScreenReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        context.registerReceiver(screenReceiver, filter);
    }

    private void unregisterScreenReceiver(Context context) {
        try {
            context.unregisterReceiver(screenReceiver);
        } catch (Exception ignored) {
            Log.w(TAG, "Screen receiver was never registered.");
        }
    }

    private void wakeScreen() {
        try {
            if (ipm.isInteractive()) {
                return;
            }
            ipm.wakeUp(
                    SystemClock.uptimeMillis(),
                    PowerManager.WAKE_REASON_APPLICATION,
                    getApplicationInfo().name,
                    getPackageName()
            );
        } catch (RemoteException ignored) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (Constants.Intent.KEY_MONITOR_RELOAD.equals(action)) {
                Log.d(TAG, "Starting key monitor");
                init();
            }

            if (SharedConstants.Intent.REMOTE_START.equals(action)) {
                Log.d(TAG, "Starting key monitor (remote intent)");
                mPrefs.edit().putBoolean(Constants.SLIDER_ENABLE_KEY, true).apply();
                init();
            }

            if (Constants.Intent.KEY_MONITOR_STOP.equals(action)
                    || SharedConstants.Intent.REMOTE_STOP.equals(action)) {
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
        stopMonitoring();
        unregisterScreenReceiver(this);
        mPrefs.edit().putBoolean(Constants.SLIDER_ENABLE_KEY, false).apply();
        super.onDestroy();
    }
}

