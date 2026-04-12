package com.inventory.service.impl;

import com.inventory.entity.Menu;
import com.inventory.mapper.MenuMapper;
import com.inventory.mapper.RoleMenuMapper;
import com.inventory.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> tree() {
        List<Menu> allMenus = menuMapper.selectMenuTree();
        return buildTree(allMenus);
    }

    private List<Menu> buildTree(List<Menu> menus) {
        Map<Long, List<Menu>> grouped = menus.stream()
            .collect(Collectors.groupingBy(Menu::getParentId));

        List<Menu> rootMenus = grouped.getOrDefault(0L, new ArrayList<>());
        for (Menu menu : rootMenus) {
            buildChildren(menu, grouped);
        }
        return rootMenus;
    }

    private void buildChildren(Menu parent, Map<Long, List<Menu>> grouped) {
        List<Menu> children = grouped.getOrDefault(parent.getId(), new ArrayList<>());
        for (Menu child : children) {
            buildChildren(child, grouped);
        }
        parent.setChildren(children);
    }

    @Override
    public List<Menu> listByRoleId(Long roleId) {
        return menuMapper.selectByRoleId(roleId);
    }

    @Override
    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(Menu menu) {
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    @Transactional
    public void update(Long id, Menu menu) {
        menu.setId(id);
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<Menu> children = menuMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id)
        );
        if (!children.isEmpty()) {
            throw new RuntimeException("请先删除子菜单");
        }
        roleMenuMapper.deleteByMenuId(id);
        menuMapper.deleteById(id);
    }
}
