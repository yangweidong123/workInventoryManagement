package com.inventory.dto;

import lombok.Data;

@Data
public class PlatformCredential {
    private String appKey;
    private String appSecret;
    private String accessToken;
    private String refreshToken;
}
