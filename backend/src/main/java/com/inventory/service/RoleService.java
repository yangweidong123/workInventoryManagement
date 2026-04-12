package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.entity.Role;

import java.util.List;

public interface RoleService {

    IPage<Role> page(int current, int size, String keyword);

    List<Role> list();

    Role getById(Long id);

    Long create(Role role);

    void update(Long id, Role role);

    void delete(Long id);

    void assignMenus(Long roleId, List<Long> menuIds);

    List<Long> getMenuIdsByRoleId(Long roleId);
}
