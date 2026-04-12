package com.inventory.controller;

import com.inventory.annotation.RequirePermission;
import com.inventory.dto.Result;
import com.inventory.dto.UserQuery;
import com.inventory.entity.User;
import com.inventory.mapper.UserMapper;
import com.inventory.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
public class SystemUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @RequirePermission("system:user:list")
    public Result<?> page(UserQuery query) {
        return Result.success(userMapper.selectPageList(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getCurrent(), query.getSize()),
            query.getKeyword()
        ));
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userMapper.selectById(id));
    }

    @PostMapping
    @RequirePermission("system:user:create")
    public Result<Long> create(@RequestBody User user) {
        userMapper.insert(user);
        return Result.success(user.getId());
    }

    @PutMapping("/{id}")
    @RequirePermission("system:user:update")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userMapper.updateById(user);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    public Result<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/reset-password")
    @RequirePermission("system:user:reset-password")
    public Result<String> resetPassword(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        String newPassword = "123456";
        user.setPassword(newPassword);
        userMapper.updateById(user);
        return Result.success("密码已重置为: " + newPassword);
    }
}
