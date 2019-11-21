package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {

    /**
     * 分页查询spu
     * @Param: [key, saleable, page, rows]
     * @Return:
     **/
    PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);

    /**
     * 新增商品
     * @Param: [spuBo]
     * @Return:
     **/
    void saveGoods(SpuBo spuBo);

    /**
     * 根据spuid查询spuDetail信息
     * @Param: [spuId]
     * @Return:
     **/
    SpuDetail querySpuDetailBySpuId(Long spuId);

    /**
     * 更根据SpuId查询sku集合
     * @Param: [SpuId]
     * @Return:
     **/
    List<Sku> querySkusBySpuId(Long spuId);

    /**
     * 更新商品
     * @Param: [spuBo]
     * @Return:
     **/
    void updateGoods(SpuBo spuBo);
}
