package com.leyou.item.api;


import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface GoodsApi {

    /**
     * 分页查询spu
     * @Param: [key, saleable, page, rows]
     * @Return:
     **/
    @GetMapping("spu/page")//key=&saleable=true&page=1&rows=5
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /**
     * 根据spuid查询spuDetail信息
     * @Param: [spuId]
     * @Return:
     **/
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);

    /**
     * 更根据SpuId查询sku集合
     * @Param: [SpuId]
     * @Return:
     **/
    @GetMapping("sku/list")
    List<Sku> querySkusBySpuId(@RequestParam("id")Long SpuId);
}
