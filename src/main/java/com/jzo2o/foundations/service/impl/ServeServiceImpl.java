package com.jzo2o.foundations.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.enums.FoundationHotEnum;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/11/21 12:40
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {
    @Resource
    private ServeItemMapper serveItemMapper;
    @Resource
    private RegionMapper regionMapper;
    /**
     * 分页查询
     *
     * @param servePageQueryReqDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        //调用mapper查询数据，这里由于继承了ServiceImpl<ServeMapper, Serve>，使用baseMapper相当于使用ServeMapper
        PageResult<ServeResDTO> serveResDTOPageResult = PageHelperUtils.
                selectPage(servePageQueryReqDTO, () -> baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
        return serveResDTOPageResult;
    }

    @Override
    @Transactional
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        for (ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            // 1.校验服务项是否为启用状态，不是启用状态不能新增
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            // 如果服务项信息不存在或未启用
            if(ObjectUtil.isNull(serveItem)||serveItem.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()){
                throw new ForbiddenOperationException("该服务不存在或未启用,无法添加到区域下使用");
            }

            // 2.校验是否重复新增
            Integer count = lambdaQuery()
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                    .eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .count();
            if(count>0){
                throw new ForbiddenOperationException(serveItem.getName()+"服务已存在");
            }

            // 3.新增服务
            Serve serve = BeanUtil.toBean(serveUpsertReqDTO, Serve.class);
            Region region = regionMapper.selectById(serveUpsertReqDTO.getRegionId());
            serve.setCityCode(region.getCityCode());
            baseMapper.insert(serve);
        }
    }

    @Override
    @Transactional
    public Serve update(Long id, BigDecimal price) {
        // 更新服务价格
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getPrice, price)
                .update();
        if(!update){
            throw new CommonException("修改服务价格失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve onSale(Long id) {
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        // 上架状态
        Integer saleStatus = serve.getSaleStatus();
        // 草稿或下架状态方可上架
        if(!(saleStatus==FoundationStatusEnum.INIT.getStatus()||saleStatus==FoundationStatusEnum.DISABLE.getStatus())){
            throw new ForbiddenOperationException("草稿或下架状态方可上架");
        }
        // 服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("所属服务项不存在");
        }
        // 服务项的启用状态
        Integer activeStatus = serveItem.getActiveStatus();
        // 服务项为启用状态方可上架
        if (!(FoundationStatusEnum.ENABLE.getStatus()==activeStatus)) {
            throw new ForbiddenOperationException("服务项为启用状态方可上架");
        }
        // 更新上架状态
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus())
                .update();
        if(!update){
            throw new CommonException("上架服务失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve offSale(Long id) {
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        // 下架状态
        Integer saleStatus = serve.getSaleStatus();
        // 上架状态方可下架
        if(!(saleStatus==FoundationStatusEnum.INIT.getStatus()||saleStatus==FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("上架状态方可下架");
        }
        // 服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("所属服务项不存在");
        }
        // 服务项的启用状态
        Integer activeStatus = serveItem.getActiveStatus();
        // 服务项为启用状态方可上架
        if (!(FoundationStatusEnum.ENABLE.getStatus()==activeStatus)) {
            throw new ForbiddenOperationException("服务项为启用状态方可下架");
        }
        // 更新下架状态
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.DISABLE.getStatus())
                .update();
        if(!update){
            throw new CommonException("下架服务失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve onHot(Long id) {
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        // 上架状态
        Integer saleStatus = serve.getSaleStatus();
        if(saleStatus!=FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("上架状态方可设置为热门");
        }
        // 服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("所属服务项不存在");
        }
        if(serveItem.getActiveStatus()!=FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务项为启用状态方可设置为热门");
        }
        // 更新热门状态
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getIsHot, FoundationHotEnum.HOT.getStatus())
                .update();
        if(!update){
            throw new CommonException("服务设置热门失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve offHot(Long id) {
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        // 上架状态
        Integer saleStatus = serve.getSaleStatus();
        if(saleStatus!=FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("上架状态方可取消热门");
        }
        // 服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("所属服务项不存在");
        }
        if(serveItem.getActiveStatus()!=FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务项为启用状态方可取消热门");
        }
        // 更新热门状态
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getIsHot, FoundationHotEnum.notHot.getStatus())
                .update();
        if(!update){
            throw new CommonException("服务取消热门失败");
        }
        return baseMapper.selectById(id);
    }
}
