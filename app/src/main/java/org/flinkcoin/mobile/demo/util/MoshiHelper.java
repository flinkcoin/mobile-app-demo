package org.flinkcoin.mobile.demo.util;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okio.Buffer;

public class MoshiHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoshiHelper.class);

    private final static Moshi MOSHI;

    static {
        MOSHI = new Moshi.Builder().build();
    }

    private MoshiHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String writeStringAsFormattedString(String json) {
        Buffer source = new Buffer().writeUtf8(json);
        JsonReader reader = JsonReader.of(source);
        try {
            Object value = reader.readJsonValue();
            return MOSHI.adapter(Object.class).indent("  ").toJson(value);
        } catch (IOException ex) {
            LOGGER.error("Error when serializing/de-serializing JSON", ex);
        }
        return null;
    }

    public static String writeValueAsFormattedString(Object value) {
        if (null == value) {
            return null;
        }
        return MOSHI.adapter(Object.class).indent("  ").toJson(value);
    }

    public static <T> T readValueFromString(String content, Class<T> valueType) {
        T ret = null;
        try {
            ret = MOSHI.adapter(valueType).fromJson(content);
        } catch (IOException ex) {
            LOGGER.error("Error when serializing/de-serializing JSON", ex);
        }
        return ret;
    }

    public static Moshi getMoshi() {
        return MOSHI;
    }
}
