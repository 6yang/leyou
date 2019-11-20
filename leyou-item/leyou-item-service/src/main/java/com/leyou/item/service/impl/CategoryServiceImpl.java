package com.leyou.item.service.impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /*
    * 根据父id，查询子类目
    * */
    @Override
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }

    /**
     * 根据多个分类id查询分类名称
     * @Param: [ids]
     * @Return:
     **/
    @Override
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories =  this.categoryMapper.selectByIdList(ids);
        //把一个List<Category>集合转化为一个 List<String> 集合
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
