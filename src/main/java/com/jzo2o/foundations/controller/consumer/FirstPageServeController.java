package com.jzo2o.foundations.controller.consumer;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeTypeListDto;
import com.jzo2o.foundations.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/12/1 17:39
 */
@RestController("consumerServeController")
@RequestMapping("/customer/serve")
@Api(tags = "用户端 - 首页服务查询接口")
public class FirstPageServeController {
    @Resource
    private HomeService homeService;

    @GetMapping("/firstPageServeList")
    @ApiOperation("首页服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeCategoryResDTO> serveCategory(@RequestParam("regionId") Long regionId) {
        List<ServeCategoryResDTO> serveCategoryResDTOS = homeService.queryServeIconCategoryByRegionIdCache(regionId);
        return serveCategoryResDTOS;

    }

    @GetMapping("/serveTypeList")
    @ApiOperation("服务类型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeTypeListDto> serveTypeList(@RequestParam("regionId") Long regionId) {
        return homeService.queryServeTypeByRegionIdCache(regionId);
    }

    @GetMapping("/hotServeList")
    @ApiOperation("热门服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeAggregationSimpleResDTO> hotServeList(@RequestParam("regionId") Long regionId) {
        return homeService.queryHotServeListByRegionId(regionId);

    }

    @GetMapping("/{id}")
    @ApiOperation("服务详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeAggregationSimpleResDTO> serveDetail(@PathVariable("id") Long id) {
        return homeService.queryServeDetail(id);

    }


}
