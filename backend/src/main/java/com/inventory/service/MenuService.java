package com.inventory.service;

import com.inventory.entity.Menu;

import java.util.List;

public interface MenuService {

    List<Menu> tree();

    List<Menu> listByRoleId(Long roleId);

    Menu getById(Long id);

    Long create(Menu menu);

    void update(Long id, Menu menu);

    void delete(Long id);
}
