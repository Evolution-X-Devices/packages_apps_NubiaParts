package org.lineageos.device.NubiaParts.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.AvailabilityCallback;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraHelper extends BroadcastReceiver {

    private CameraManager mCameraManager;
    private Executor executor;
    private AvailabilityCallback mCallback;
    boolean callbackRegistered = false;


    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent received!: " + intent.getAction());
        mCameraManager = context.getApplicationContext().getSystemService(CameraManager.class);
        if (Objects.equals(intent.getAction(), this.getClass().getName() + ".ACTION_SET_TORCH")) {
            setTorch(intent.getBooleanExtra("mode", true));
        }

        if (Objects.equals(intent.getAction(), this.getClass().getName()
                + ".ACTION_START_FRONT_CAMERA_CALLBACK")) {
            if (!callbackRegistered) {
                registerFrontCameraCallback();
            }
        }

        if (Objects.equals(intent.getAction(), this.getClass().getName()
                + ".ACTION_STOP_FRONT_CAMERA_CALLBACK")) {
            if (callbackRegistered) {
                unregisterFrontCameraCallback();
            }
        }
    }

    private String getCameraId() {
        try {
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                final CameraCharacteristics characteristics =
                        mCameraManager.getCameraCharacteristics(cameraId);
                final boolean hasFlash = Boolean.TRUE.equals(characteristics.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE));
                final int facing = characteristics.get(
                        CameraCharacteristics.LENS_FACING);
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
            Log.e(TAG, "No camera with flashlight found");
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to get camera", e);
        }
        return null;

    }

    private void setTorch(boolean mode) {
       String mCameraId = getCameraId();
        if (mCameraId == null) {
            Log.e(TAG, "Camera is not available");
            return;
        }

        try {
            CameraCharacteristics characteristics =
                    mCameraManager.getCameraCharacteristics(mCameraId);
            if (mode) {
                int maxLevel = 0;
                maxLevel = characteristics.get(
                        CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                if (maxLevel != 0) {
                    mCameraManager.turnOnTorchWithStrengthLevel(mCameraId, maxLevel);
                } else {
                    mCameraManager.setTorchMode(mCameraId, true);
                }
            } else {
                mCameraManager.setTorchMode(mCameraId, false);
            }
        } catch (CameraAccessException e){
            Log.e(TAG, "Failed to turn flashlight " + (mode ? "on" : "off"), e);
        }
    }

    private void unregisterFrontCameraCallback() {
        if (callbackRegistered) {
            Log.d(TAG, "Attempting to unregister camera callback");
            mCameraManager.unregisterAvailabilityCallback(mCallback);
        }
    }


    private void registerFrontCameraCallback() {
        executor = Executors.newSingleThreadExecutor();
                mCallback = new AvailabilityCallback() {
                    @Override
                    public void onCameraAvailable(String cameraId) {
                        CameraCharacteristics characteristics;
                        try {
                             characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                        } catch (CameraAccessException e) {
                            throw new RuntimeException(e);
                        }
                        int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                        if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            // It's the front camera
                        }
                    }

                    @Override
                    public void onCameraUnavailable(String cameraId) {
                        CameraCharacteristics characteristics;
                        try {
                            characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                        } catch (CameraAccessException e) {
                            throw new RuntimeException(e);
                        }
                        int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                        if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            // It's the front camera
                        }
                    }
                };
                new Handler(Looper.getMainLooper());
        Log.d(TAG, "Attempting to register camera callback");
        mCameraManager.registerAvailabilityCallback(executor, mCallback);
        callbackRegistered = true;
    }

}
