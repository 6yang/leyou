package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu
     * @Param: [key, saleable, page, rows]
     * @Return:
    **/
    @GetMapping("spu/page")//key=&saleable=true&page=1&rows=5
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
        @RequestParam(value = "key",required = false)String key,
        @RequestParam(value = "saleable",required = false)Boolean saleable,
        @RequestParam(value = "page",defaultValue = "1")Integer page,
        @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> result =  this.goodsService.querySpuByPage(key,saleable,page,rows);
        if(CollectionUtils.isEmpty(result.getItems())){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

}
