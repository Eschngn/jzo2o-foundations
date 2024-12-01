package com.jzo2o.foundations.mapper;

import com.jzo2o.foundations.model.domain.ServeType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeTypeListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务类型表 Mapper 接口
 * </p>
 *
 * @author itcast
 * @since 2023-07-03
 */
public interface ServeTypeMapper extends BaseMapper<ServeType> {
    /**
     * 根据区域id查询服务类型
     * @param regionId
     * @return
     */
    List<ServeTypeListDto> findServeTypeByRegionId(Long regionId);

}
