package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 新增商品
     * @Param: [spuBo]
     * @Return:
    **/
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 更新商品
     * @Param: [spuBo]
     * @Return:
     **/
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.noContent().build(); // 204
    }


    /**
     * 根据spuid查询spuDetail信息
     * @Param: [spuId]
     * @Return:
    **/
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail =  this.goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail ==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 更根据SpuId查询sku集合
     * @Param: [SpuId]
     * @Return:
    **/
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long SpuId){
          List<Sku> skus = this.goodsService.querySkusBySpuId(SpuId);
          if(CollectionUtils.isEmpty(skus)){
              return ResponseEntity.notFound().build();
          }
          return ResponseEntity.ok(skus);
    }
    /**
     * 根据spuId查询spu
     * @Param: [id]
     * @Return:
    **/
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if(spu ==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

    /**
     * 根据skuId查询sku
     * @Param: [id]
     * @Return:
     **/
    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("skuId")Long skuId){
        Sku sku = this.goodsService.querySkuById(skuId);
        if(sku ==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);
    }




}
