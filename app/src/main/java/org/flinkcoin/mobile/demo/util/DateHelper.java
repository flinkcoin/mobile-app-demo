package org.flinkcoin.mobile.demo.util;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateHelper {

    private DateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(getUTC());
    }

    public static ZoneId getUTC() {
        return ZoneId.of("UTC");
    }

    public static String parseMilliSeconds(Long millis) {
        if (null == millis) {
            return null;
        }
        Duration duration = Duration.ofMillis(millis);
        StringBuilder stringBuilder = new StringBuilder();
        duration = parse(duration, stringBuilder);
        long seconds = duration.getSeconds();
        if (seconds > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(seconds);
            duration = duration.minusSeconds(seconds);
            long milli = duration.toMillis();
            if (milli > 0) {
                DecimalFormat millisFormat = new DecimalFormat("000");
                stringBuilder.append(".").append(millisFormat.format(milli));
            }
            stringBuilder.append(" ").append(seconds > 1 ? "seconds" : "second");
        } else {
            long milli = duration.toMillis();
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(milli).append(" ms");
        }
        return stringBuilder.toString();
    }

    private static Duration parse(Duration duration, StringBuilder stringBuilder) {
        long days = duration.toDays();
        if (days > 0) {
            stringBuilder.append(days).append(" ").append(days > 1 ? "days" : "day").append(" ");
            duration = duration.minusDays(days);
        }

        long hours = duration.toHours();
        if (hours > 0) {
            stringBuilder.append(hours).append(" ").append(hours > 1 ? "hours" : "hour").append(" ");
            duration = duration.minusHours(hours);
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            stringBuilder.append(minutes).append(" ").append(minutes > 1 ? "minutes" : "minute");
            duration = duration.minusMinutes(minutes);
        }

        return duration;
    }

}
