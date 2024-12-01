package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeTypeListDto;

import java.util.List;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/12/1 18:32
 */
public interface HomeService {
    /**
     * 根据区域id获取服务图标信息
     *
     * @param regionId 区域id
     * @return 服务图标列表
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);

    /**
     * 根据区域id获取服务类型信息
     * @param regionId
     * @return
     */
    List<ServeTypeListDto> queryServeTypeByRegionIdCache(Long regionId);

    /**
     * 根据区域id获取热门服务列表
     * @param regionId
     * @return
     */
    List<ServeAggregationSimpleResDTO> queryHotServeListByRegionId(Long regionId);

    /**
     * 根据服务id查询服务详情
     * @param id
     * @return
     */
    List<ServeAggregationSimpleResDTO> queryServeDetail(Long id);



}
