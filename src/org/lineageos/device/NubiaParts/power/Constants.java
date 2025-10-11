package org.lineageos.device.NubiaParts.power;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;

public class Constants {

    public static String BYPASS_CHARGING_NODE =
            ResourceUtils.getString("bypass_charging_status_node");
    public static String BYPASS_CHARGING_ENABLED_VALUE =
            ResourceUtils.getString("bypass_charging_enabled_value");

    public static String BYPASS_CHARGING_DISABLED_VALUE =
            ResourceUtils.getString("bypass_charging_disabled_value");

    public static class Intent {
        public static String NOTIFICATION_TAPPED = "notification_tapped";

    }
}
