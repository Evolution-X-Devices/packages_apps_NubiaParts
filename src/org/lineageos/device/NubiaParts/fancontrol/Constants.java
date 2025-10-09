package org.lineageos.device.NubiaParts.fancontrol;


import android.content.Context;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;



public class Constants {

    public static Context CONTEXT;

    public static void init(Context context) {
        CONTEXT = context;
    }


    public static final String FAN_PREF_NAME = "nubia_fan";
    public static final String APP_FAN_SPEED_MAP_KEY = "app_fan_speeds";
    public static final String USER_ENABLE_FAN_KEY = "fan_main_switch";
    public static final String FAN_LOCK_KEY = "fan_locked";
    public static final String FAN_LOCK_REASON_KEY = "fan_lock_reason";
    public static final String USER_FAN_SPEED_KEY = "user_fan_speed";
    public static final String SCREEN_STATE_FAN_KEY = "follow_screen_state";

    public static final String MONITOR_CALL_KEY = "pause_on_call";
    public static final String FAN_AUTO_KEY = "use_thermal_monitor";

    public static final String FAN_CHARGING_BOOST_KEY = "charging_boost";

    public static final String INTENT_FAN_STOP = "ACTION_STOP_FAN_SERVICE";
    public static final String INTENT_FAN_START = "ACTION_START_FAN_SERVICE";
    public static final String INTENT_FAN_RELOAD = "ACTION_RELOAD";


    public static final int FAN_SPEED_MAX = 5;
    public static final int FAN_SPEED_MIN = 0;

    public static String FAN_TOGGLE_NODE = ResourceUtils.getString("fan_toggle_node");
    public static String FAN_SPEED_NODE = ResourceUtils.getString("fan_speed_node");
}