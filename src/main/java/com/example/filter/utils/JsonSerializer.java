package com.example.filter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @author QXX3704
 * @since 2020/11/26
 */
@Slf4j
public class JsonSerializer {

    private static ObjectMapper mapper = new ObjectMapper();
    private static JavaTimeModule javaTimeModule = new JavaTimeModule();

    /**
     * Converts a java entity into a json string value
     *
     * @param object object to map
     * @return json string representation of incoming object
     */
    public static String convertToJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return object.toString();
        }
    }

    /**
     * Remove json property from json object
     *
     * @param origin json object
     * @param key    property key to remove
     * @return json object with provided property key omitted
     */
    public static JsonObject removeProperty(JsonObject origin, String key) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        for (Map.Entry<String, JsonValue> entry : origin.entrySet()) {
            if (entry.getKey().equals(key)) {
                continue;
            } else {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public static <T> T mapperTypeRef(Class<T> type, String jsonString) {
        try {
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(jsonString, type);
        } catch (IOException var3) {
            throw new IllegalArgumentException(var3);
        }
    }

    public static <T> T mapper(Class<T> type, String jsonString) {
        try {
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(jsonString, type);
        } catch (IOException var3) {
            throw new IllegalArgumentException(var3);
        }
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}

