package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/11/21 12:39
 */

public interface IServeService extends IService<Serve> {
    /**
     * 分页查询服务列表
     * @param servePageQueryReqDTO 查询条件
     * @return 分页结果
     */
    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 批量新增
     *
     * @param serveUpsertReqDTOList 批量新增数据
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    /**
     * 服务价格修改
     *
     * @param id 服务id
     * @param price 价格
     * @return 服务
     */
    Serve update(Long id, BigDecimal price);

    /**
     * 上架
     *
     * @param id 服务id
     */
    Serve onSale(Long id);

    /**
     * 下架
     *
     * @param id 服务id
     */
    Serve offSale(Long id);

    /**
     * 设置热门
     * @param id
     */
    Serve onHot(Long id);

    /**
     * 取消热门
     * @param id
     */
    Serve offHot(Long id);
}
