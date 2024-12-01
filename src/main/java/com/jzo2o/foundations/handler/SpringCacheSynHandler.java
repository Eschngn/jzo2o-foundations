package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/12/1 17:25
 */
@Component
@Slf4j
public class SpringCacheSynHandler {

    @Resource
    private IRegionService regionService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private HomeService homeService;

    /**
     * 已启用区域缓存更新
     * 每日凌晨1点执行
     */
    @XxlJob(value = "activeRegionCacheSync")
    public void activeRegionCacheSync() {
        log.info(">>>>>>>>开始进行缓存同步，更新已启用区域");
        // 删除缓存
        Boolean delete = redisTemplate.delete(RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS");

        // 通过查询开通区域列表进行缓存
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionListCache();

        // 遍历区域对该区域下的服务类型进行缓存
        regionSimpleResDTOS.forEach(item->{
            // 区域id
            Long regionId = item.getId();

            // 删除该区域下的首页服务列表
            String serve_type_key = RedisConstants.CacheName.SERVE_ICON + "::" + regionId;
            redisTemplate.delete(serve_type_key);
            homeService.queryServeIconCategoryByRegionIdCache(regionId);
            // 删除该区域下的服务类型列表缓存
            String serve_icon_key = RedisConstants.CacheName.SERVE_TYPE + "::" + regionId;
            redisTemplate.delete(serve_icon_key);
            homeService.queryServeTypeByRegionIdCache(regionId);
            // 删除该区域下的热门服务列表缓存
            String hot_serve_key = RedisConstants.CacheName.HOT_SERVE + "::" + regionId;
            redisTemplate.delete(hot_serve_key);
            homeService.queryHotServeListByRegionId(regionId);

        });
    }
}
