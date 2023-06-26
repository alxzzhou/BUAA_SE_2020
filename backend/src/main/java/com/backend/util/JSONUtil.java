package com.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JSONUtil {
    public static final ObjectMapper MAP = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String toJSON(Object obj) throws JsonProcessingException {
        return MAP.writeValueAsString(obj);
    }

    public static <T> T readJSON(String json, Class<T> t) throws JsonProcessingException {
        return MAP.readValue(json, t);
    }
}
