package com.gabrielSouza.createUrl;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
public class UrlData {
    private String orginalUrl;
    private long expirationTime;

}
