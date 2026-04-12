package com.inventory.controller;

import com.inventory.annotation.RequirePermission;
import com.inventory.dto.Result;
import com.inventory.entity.Menu;
import com.inventory.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/tree")
    public Result<List<Menu>> tree() {
        return Result.success(menuService.tree());
    }

    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @PostMapping
    @RequirePermission("system:menu:create")
    public Result<Long> create(@RequestBody Menu menu) {
        return Result.success(menuService.create(menu));
    }

    @PutMapping("/{id}")
    @RequirePermission("system:menu:update")
    public Result<Void> update(@PathVariable Long id, @RequestBody Menu menu) {
        menuService.update(id, menu);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:menu:delete")
   公众Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success(null);
    }
}
