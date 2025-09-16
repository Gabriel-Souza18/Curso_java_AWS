package com.gabrielSouza.redirectShortUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main implements RequestHandler<Map<String,Object>, Map<String,Object>> {
    private final S3Client s3client = S3Client.builder().build();
    private final ObjectMapper ObjectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String pathParameters = input.get("rawPath").toString();
        String shortUrl = pathParameters.replace("/", "");

        if (shortUrl==null || shortUrl.isEmpty()){
            throw  new IllegalArgumentException("ShotUrl is null");
        }
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("gabr-shorter-url")
                .key(shortUrl+ ".json")
                .build();

        InputStream s3ObjectStream;

        try {
            s3ObjectStream = s3client.getObject(getObjectRequest);
        }catch (Exception e){
            throw new RuntimeException("Error to get object from S3", e);
        }

        UrlData urlData;
        try{
            urlData = ObjectMapper.readValue(s3ObjectStream, UrlData.class);
        }catch (Exception e){
            throw new RuntimeException("Error to parse object from S3", e);
        }

        long currentTime = System.currentTimeMillis()/1000;

        Map<String,Object> response = new HashMap<>();
        if (urlData.getExpirationTime() < currentTime){
            response.put("statusCode", 410);
            response.put("body", "URL expired");

            return response;
        }

        response.put("statusCode", 302);
        Map <String,String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalUrl());

        response.put ("headers", headers);
        return response;

    }
}