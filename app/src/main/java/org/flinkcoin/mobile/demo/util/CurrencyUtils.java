package org.flinkcoin.mobile.demo.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CurrencyUtils {

    private static final NumberFormat CURRENCY_FORMAT_FULL = new DecimalFormat("0.##########");
    private static final String CURRENCY_SYMBOL = "\u20A3";

    private static final long MAIN_UNIT_MULTIPLIER = 10_000_000_000L;

    private CurrencyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String format(long number) {
        return CURRENCY_FORMAT_FULL.format((double) number / MAIN_UNIT_MULTIPLIER) + " " + CURRENCY_SYMBOL;
    }

}
