package com.inventory.service;

import com.inventory.dto.LoginDTO;
import com.inventory.dto.LoginResultDTO;

public interface AuthService {

    LoginResultDTO login(LoginDTO dto);

    void logout(String token);
}
