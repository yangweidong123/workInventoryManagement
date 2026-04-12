package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.entity.Menu;
import com.inventory.entity.Role;
import com.inventory.entity.RoleMenu;
import com.inventory.mapper.MenuMapper;
import com.inventory.mapper.RoleMapper;
import com.inventory.mapper.RoleMenuMapper;
import com.inventory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public IPage<Role> page(int current, int size, String keyword) {
        return roleMapper.selectPageList(new Page<>(current, size), keyword);
    }

    @Override
    public List<Role> list() {
        return roleMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(Role role) {
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional
    public void update(Long id, Role role) {
        role.setId(id);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role != null && role.getIsSystem() == 1) {
            throw new RuntimeException("系统内置角色不能删除");
        }
        roleMenuMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }
}
