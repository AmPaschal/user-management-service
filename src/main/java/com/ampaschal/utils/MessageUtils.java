package com.ampaschal.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Amusuo Paschal C.
 * @since 7/22/2020 10:12 AM
 */

@Slf4j
@RequestScoped
public class MessageUtils {

    private final ObjectMapper objectMapper = getObjectMapper();

    public  <T> T post(String url, Object body, Class<T> responseClass, Map<String, Object> templateValues) {

        return null;
    }

    public  <T> T get(String url, Map<String, String> queryParams, Class<T> responseClass) {


        return null;
    }

    public Map<String, String> getHeaders(){

        Map<String, String> headers = new ConcurrentHashMap<>(1);

        headers.put("content-type", "application/json");

        return headers;
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {

        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {

            log.error("error converting from jsonString : {}, clazz : {}", jsonString, clazz, e);

            throw new RuntimeException("Error getting json string", e);
        }
    }

    private ObjectMapper getObjectMapper() {

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
