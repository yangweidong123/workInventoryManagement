package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    void deleteByUserId(@Param("userId") Long userId);

    void deleteByRoleId(@Param("roleId") Long roleId);
}
