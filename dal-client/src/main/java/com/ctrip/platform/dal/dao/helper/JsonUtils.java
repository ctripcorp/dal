package com.ctrip.platform.dal.dao.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class JsonUtils {

    private static final AtomicReference<ObjectMapper> MAPPER_REF = new AtomicReference<>();

    public static String toJson(Object object) throws JsonProcessingException {
        return getMapper().writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return getMapper().readValue(json, clazz);
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = MAPPER_REF.get();
        if (mapper == null)
            synchronized (MAPPER_REF) {
                mapper = MAPPER_REF.get();
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    MAPPER_REF.set(mapper);
                }
            }
        return mapper;
    }

}
