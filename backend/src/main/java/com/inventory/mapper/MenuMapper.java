package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuTree();

    List<Menu> selectByRoleId(@Param("roleId") Long roleId);

    List<Menu> selectByUserId(@Param("userId") Long userId);

    int countByPermission(@Param("permission") String permission);
}
