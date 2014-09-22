package com.ctrip.platform.dal.daogen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Object serialization and deserialization 
 * @author wcyuan
 */
public class JsonUtils {
	static ObjectMapper objectMapper;
	 
    public static <T> T readValue(String content, Class<T> valueType) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return null;
    }
 
    public static String toJSon(Object object) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return null;
    }
}
