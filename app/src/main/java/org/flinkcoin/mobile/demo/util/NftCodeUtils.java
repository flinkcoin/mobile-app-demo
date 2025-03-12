package org.flinkcoin.mobile.demo.util;

public class NftCodeUtils {

    private NftCodeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String mask(String nftCode) {
        return nftCode.substring(0, 8) + " ... " + nftCode.substring(nftCode.length() - 8);
    }

}
