package com.inventory.service.impl;

import com.inventory.dto.LoginDTO;
import com.inventory.dto.LoginResultDTO;
import com.inventory.entity.User;
import com.inventory.mapper.UserMapper;
import com.inventory.service.AuthService;
import com.inventory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResultDTO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if ("0".equals(user.getStatus())) {
            throw new RuntimeException("用户已禁用");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        LoginResultDTO result = new LoginResultDTO();
        result.setToken(token);
        result.setUsername(user.getUsername());
        result.setNickname(user.getNickname());
        return result;
    }

    @Override
    public void logout(String token) {
    }
}
