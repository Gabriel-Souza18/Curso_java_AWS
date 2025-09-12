package com.gabrielSouza.createUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        var body = input.get("body").toString();
        Map<String, String> bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, Map.class);
            var url = bodyMap.get("url");

        }catch (Exception e ) {
            throw new RuntimeException("Error parsing JSON", e);
        }

        String originalUrl = bodyMap.get("url");
        String expirationTime = bodyMap.get("expirationTime");

        UUID uuid = UUID.randomUUID();
        String shortUrl = "https://short.url/" + uuid.toString().substring(0, 8);

        Map <String,Object> responseBody = new HashMap<>();
        responseBody.put("shortUrl", shortUrl);
        return responseBody;
    }
}