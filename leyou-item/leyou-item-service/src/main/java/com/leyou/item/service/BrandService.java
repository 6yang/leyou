package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    /**
     * 分页品牌的查询
     * @Param: [key, page, rows, sortBy, desc]
     * @Return:
     **/
    PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    /**
     * 新增品牌
     * @Param: [brand, cids]
     * @Return:
     **/
    void saveBrand(Brand brand, List<Long> cids);

    /**
     * 根据分类id查询 分类集合
     * @Param: [cid]
     * @Return:
     **/
    List<Brand> queryBrandsByCid(Long cid);

    /**
     * 根据品牌Id查询品牌
     * @Param: [id]
     * @Return:
     **/
    Brand queryBrandById(Long id);
}
