package xyz.synse.datacenter.utils.utilities;

public class BatteryInfo{
        private final int batteryLevel;
        private final boolean charging;
        private final String chargePlug;

        public BatteryInfo(int batteryLevel, boolean charging, String chargePlug) {
            this.batteryLevel = batteryLevel;
            this.charging = charging;
            this.chargePlug = chargePlug;
        }

        public String getChargePlug() {
            return chargePlug;
        }

        public boolean isCharging() {
            return charging;
        }

        public int getBatteryLevel() {
            return batteryLevel;
        }
    }