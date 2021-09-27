package org.flinkcoin.mobile.demo.util;

import java.util.concurrent.TimeUnit;

public class AppTime {

    private AppTime() {
        throw new IllegalStateException("Utility class");
    }

    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    public static long uptime() {
        return System.nanoTime() / 1000000L;
    }

    public static boolean isSystemTimeElapsed(long timestamp, long duration, TimeUnit unit) {
        if (timestamp == 0) {
            return true;
        }
        return Math.abs(getSystemTime() - timestamp) > unit.toMillis(duration);
    }

    public static boolean isUptimeElapsed(long timestamp, long duration, TimeUnit unit) {
        if (timestamp == 0) {
            return true;
        }
        return Math.abs(uptime() - timestamp) > unit.toMillis(duration);
    }
}
