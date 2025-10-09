package org.lineageos.device.NubiaParts.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;



public final class ResourceUtils {

    private static final String TAG = "ResourceUtils";
    private static final boolean DEBUG = true;

    private static Context mContext;
    private static AssetManager assetManager;
    private static Resources resources;

    public static void init(Context context) {
        if (context != null && mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    public static Context getContext() {
        if (mContext == null) {
            throw new IllegalStateException("ResourceUtils not initialized. Call ResourceUtils.init(context) first.");
        }
        return mContext;
    }

    private static AssetManager getAssetManager() {
        if (assetManager == null) {
            assetManager = getContext().getAssets();
        }
        return assetManager;
    }

    private static Resources getResources() {
        if (resources == null) {
            resources = getContext().getResources();
        }
        return resources;
    }

    public static int getIdentifier(String id, String type) {
        return getResources().getIdentifier(id, type, getContext().getPackageName());
    }

    public static Boolean getBoolean(String id) {
        return getResources().getBoolean(getIdentifier(id, "bool"));
    }

    public static String getString(String id) {
        return getResources().getString(getIdentifier(id, "string"));
    }

    public static int getInteger(String id) {
        return getResources().getInteger(getIdentifier(id, "integer"));
    }

    public static String[] getStringArray(String id) {
        return getResources().getStringArray(getIdentifier(id, "array"));
    }

    public static int[] getIntArray(String id) {
        return getResources().getIntArray(getIdentifier(id, "array"));
    }
}
