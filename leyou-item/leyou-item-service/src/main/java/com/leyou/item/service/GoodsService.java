package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;

public interface GoodsService {

    /**
     * 分页查询spu
     * @Param: [key, saleable, page, rows]
     * @Return:
     **/
    PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);

}
