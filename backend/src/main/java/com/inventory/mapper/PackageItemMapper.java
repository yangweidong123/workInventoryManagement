package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.PackageItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PackageItemMapper extends BaseMapper<PackageItem> {

    List<PackageItem> selectByPackageId(@Param("packageId") Long packageId);

    void deleteByPackageId(@Param("packageId") Long packageId);
}
