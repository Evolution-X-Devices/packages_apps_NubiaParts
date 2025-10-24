package org.lineageos.device.NubiaParts.gamekeys;

public class Constants {

    public class Prefs {

        public static final String PREF_KEY = "nubia_game_keys";
        public static final String USER_ENABLE_GAME_KEY_PREF = "game_keys_enabled";

        public static final String KEY_RIGHT_MODE = "right_game_key_enabled";
        public static final String KEY_LEFT_MODE = "left_game_key_enabled";

        public static final String KEY_LEFT_SHOULDER_SENS = "left_sensitivity";
        public static final String KEY_RIGHT_SHOULDER_SENS = "right_sensitivity";

    }

    public static final int SLEEP_MODE_INT = 2;
    public static final int WAKE_MODE_INT = 1;

    public static final String LEFT_SHOULDER_PATH = "/sys/class/leds/sar0";
    public static final String RIGHT_SHOULDER_PATH = "/sys/class/leds/sar1";

    public static final String LEFT_SHOULDER_MODE = LEFT_SHOULDER_PATH + "/mode_operation";
    public static final String RIGHT_SHOULDER_MODE = RIGHT_SHOULDER_PATH + "/mode_operation";

    public static final String LEFT_SHOULDER_SENS = LEFT_SHOULDER_PATH + "/sensy_config";
    public static final String RIGHT_SHOULDER_SENS = RIGHT_SHOULDER_PATH + "/sensy_config";
}
