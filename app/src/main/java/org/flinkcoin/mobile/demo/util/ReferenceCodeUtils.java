package org.flinkcoin.mobile.demo.util;

public class ReferenceCodeUtils {

    private static final String REFERENCE_CODE_PREFIX = "#";

    private ReferenceCodeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String format(String referenceCode) {
        return referenceCode == null || referenceCode.isEmpty() ? null : REFERENCE_CODE_PREFIX + referenceCode;
    }

}
