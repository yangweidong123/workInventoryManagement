package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.PackageDTO;
import com.inventory.dto.PackageQuery;
import com.inventory.entity.Package;
import org.apache.ibatis.annotations.Param;

public interface PackageMapper extends BaseMapper<Package> {

    IPage<PackageDTO> selectPageList(Page<?> page, @Param("query") PackageQuery query);

    PackageDTO selectDetailById(@Param("id") Long id);
}
