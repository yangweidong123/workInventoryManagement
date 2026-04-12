package com.inventory.dto;

import lombok.Data;

@Data
public class LoginResultDTO {

    private String token;

    private String username;

    private String nickname;
}
