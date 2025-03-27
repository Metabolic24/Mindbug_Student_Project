package com.mindbug;

import java.util.Map;

import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {
    public static <T> T mvcResultToObject(MvcResult result, Class<T> objectType) throws Exception {
        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonString, objectType);
    }

    public static String convertMapToJson(Map<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

}
