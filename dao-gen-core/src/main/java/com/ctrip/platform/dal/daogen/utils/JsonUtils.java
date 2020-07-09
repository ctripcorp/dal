package com.ctrip.platform.dal.daogen.utils;

import com.google.gson.*;

public class JsonUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static volatile JsonUtils instance;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(DATE_FORMAT).create();
    private JsonParser parser = new JsonParser();

    private JsonUtils() {}

    private static JsonUtils getInstance() {
        if (instance == null)
            synchronized (JsonUtils.class) {
                if (instance == null)
                    instance = new JsonUtils();
            }
        return instance;
    }

    public static String toJson(Object object) {
        return getInstance().gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return getInstance().gson.fromJson(json, clazz);
    }

    public static JsonObject parseObject(String json) {
        return getInstance().parser.parse(json).getAsJsonObject();
    }

    public static JsonArray parseArray(String json) {
        return getInstance().parser.parse(json).getAsJsonArray();
    }

    public static JsonElement parse(String json) {
        return getInstance().parser.parse(json);
    }

}