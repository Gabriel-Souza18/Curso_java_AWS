package com.gabrielSouza.redirectShortUrl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UrlData {
    private String originalUrl;
    private long expirationTime;

}
