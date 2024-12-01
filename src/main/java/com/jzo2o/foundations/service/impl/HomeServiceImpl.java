package com.jzo2o.foundations.service.impl;

import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/12/1 18:32
 */
@Service
@Slf4j
public class HomeServiceImpl implements HomeService {
    @Resource
    private ServeMapper serveMapper;
    @Resource
    private IRegionService regionService;
    @Override
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {
        // 校验当前城市是否为启用状态
        Region region = regionService.getById(regionId);
        if(ObjectUtils.isEmpty(region)||ObjectUtils.equal(region.getActiveStatus(), FoundationStatusEnum.DISABLE.getStatus())){
            return Collections.emptyList();
        }
        // 根据城市编码查询所有的服务图标
        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if(ObjectUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        // 服务类型取前两个，每个类型下服务项取前4个
        int endIndex = Math.min(list.size(), 2);
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(list.subList(0, endIndex));
        serveCategoryResDTOS.forEach(v -> {
            List<ServeSimpleResDTO> serveResDTOList = v.getServeResDTOList();
            // serveResDTOList 的截止下标
            int endIndex2 = Math.min(serveResDTOList.size(), 4);
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex2));
            v.setServeResDTOList(serveSimpleResDTOS);
        });
        return serveCategoryResDTOS;
    }
}
