package com.gabrielSouza.createUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();


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

        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");

        Long expirationTimeInSec = Long.parseLong(expirationTime) ;

        UUID uuid = UUID.randomUUID();
        String shortUrl = uuid.toString().substring(0, 8);


        UrlData urlData = new UrlData(originalUrl, expirationTimeInSec);

        try{
            String urlDataJson = objectMapper.writeValueAsString(urlData);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("gabr-shorter-url")
                    .key(shortUrl+ ".json")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
        }catch (Exception e){
            throw new RuntimeException("Error ao Salvar URL no S3", e);
        }

        Map <String,Object> responseBody = new HashMap<>();
        responseBody.put("shortUrl", shortUrl);

        return responseBody;
    }
}