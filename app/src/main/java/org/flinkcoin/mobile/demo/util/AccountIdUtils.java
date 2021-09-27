package org.flinkcoin.mobile.demo.util;

public class AccountIdUtils {

    private AccountIdUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String mask(String accoutId) {
        return accoutId.substring(0, 4) + " ... " + accoutId.substring(accoutId.length() - 4);
    }

}
