package org.lineageos.device.NubiaParts.gameswitch;

public class Constants {

    public static final String PREF_KEY = "nubia_switch";

    public static final String SLIDER_USAGE_KEY = "slider_usage";
    public static final String SLIDER_ENABLE_KEY = "slider_enable";
    public static final String KEY_LAUNCH_APP_NAME = "launch_package_name";

    public static final String VIBRATION_KEY = "vibrate_on_action";
    public static final String RINGER_BEHAVIOR_KEY = "ringer_behavior";
    public static final String RINGER_SHOW_DIALOG_KEY = "show_volume_dialog_for_ringer";

    public static final String HELPER_PACKAGE_NAME =
            "org.lineageos.device.NubiaParts.helper";

    public static class Intent {

        public static final String ACTION_SET_TORCH =
                HELPER_PACKAGE_NAME + ".CameraHelper.ACTION_SET_TORCH";

        public static final String KEY_MONITOR_STOP = "key_monitor_stop";

        public static final String KEY_MONITOR_RELOAD = "key_monitor_reload";

    }
}
