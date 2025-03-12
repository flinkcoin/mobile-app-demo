package org.flinkcoin.mobile.demo.util;

public class AccountCodeUtils {

    private static final String ACCOUNT_CODE_PREFIX = "#";

    private AccountCodeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String format(String accountCode) {
        return accountCode == null || accountCode.isEmpty() ? null : ACCOUNT_CODE_PREFIX + accountCode;
    }

}
