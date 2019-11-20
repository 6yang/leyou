package com.leyou.item.service;


import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 根据父id，查询子类目
     * @Param: [pid]
     * @Return:
    **/
    List<Category> queryCategoriesByPid(Long pid);

    /**
     * 根据多个分类id查询分类名称
     * @Param: [ids]
     * @Return:
    **/
    List<String> queryNamesByIds(List<Long> ids);
}
