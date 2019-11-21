package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    /**
     * 根据分类id 来查询规格参数组
     * @Param: [cid]
     * @Return:
    **/
    List<SpecGroup> queryGroupsByCid(Long cid);

    /**
     * 根据条件查询规格参数
     * @Param: [gid, cid, generic, searching]
     * @Return:
     **/
    List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching);
    

}
