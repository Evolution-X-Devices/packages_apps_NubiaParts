package org.lineageos.device.NubiaParts.gameswitch;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

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

    private static SwitchControllerBase mSwitchController;

    private static int usage = 0;
    private static boolean running = false;

    private static Runnable pollRunnable;
    private static Handler mainHandler;
    private static int currentState = -1;

    private static String SYSFS_PATH;

    private static boolean vibrationEnabled;

    private static SharedPreferences mPrefs;
    private static Vibrator mVibrator;
    private Context mContext;

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
        mContext = getApplicationContext();
        mFlashlightAction = new FlashlightAction(mContext);
        mRingerAction = new RingerAction(mContext);
        mAppLauncher = new AppLauncher(mContext);
        mOrientationLockAction = new OrientationLockAction(mContext);

        VibratorManager vm = (VibratorManager) mContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        mVibrator = vm.getDefaultVibrator();

        mPrefs = mContext.getApplicationContext().getSharedPreferences(
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

    private static void startMonitoring() {
        currentState = readState();
        Log.d(TAG, "Initial game switch state: " + currentState);

        pollRunnable = new Runnable() {
            @Override
            public void run() {
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
        Log.d(TAG, "Started polling game switch");
    }

    private static void doHapticFeedback() {
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    private static void processAction(int state) {
        if (usage != 0) {
            mSwitchController.processAction();
            if (vibrationEnabled) doHapticFeedback();
        }
    }

    private static void init() {

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
            case OrientationLockAction.ID:
                mSwitchController = mOrientationLockAction;
                mSwitchController.setup();
                break;
        }

        vibrationEnabled = mPrefs.getBoolean(Constants.VIBRATION_KEY, true);
        startMonitoring();
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
        if (pollRunnable != null) {
            mainHandler.removeCallbacks(pollRunnable);
        }
        super.onDestroy();
    }
}

