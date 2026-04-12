package com.inventory.controller;

import com.inventory.annotation.RequirePermission;
import com.inventory.dto.Result;
import com.inventory.dto.RoleQuery;
import com.inventory.entity.Role;
import com.inventory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @RequirePermission("system:role:list")
    public Result<?> page(RoleQuery query) {
        return Result.success(roleService.page(query.getCurrent(), query.getSize(), query.getKeyword()));
    }

    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    @RequirePermission("system:role:create")
    public Result<Long> create(@RequestBody Role role) {
        return Result.success(roleService.create(role));
    }

    @PutMapping("/{id}")
    @RequirePermission("system:role:update")
    public Result<Void> update(@PathVariable Long id, @RequestBody Role role) {
        roleService.update(id, role);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getMenuIdsByRoleId(id));
    }

    @PostMapping("/{id}/menus")
    @RequirePermission("system:role:assign")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success(null);
    }
}
