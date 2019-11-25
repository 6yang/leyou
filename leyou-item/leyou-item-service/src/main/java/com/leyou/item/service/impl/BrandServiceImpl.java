package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 分页品牌的查询
     * @Param: [key, page, rows, sortBy, desc]
     * @Return:
     **/
    @Override
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //1 初始化一个example 对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //2 添加模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%")
                    .orEqualTo("letter",key);
        }
        //3 添加分页
        PageHelper.startPage(page,rows);
        //4 添加排序
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + ( desc ? " desc" : " asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增品牌
     * @Param: [brand, cids]
     * @Return:
    **/
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        //1 新增品牌
        this.brandMapper.insertSelective(brand);
        cids.forEach(cid -> this.brandMapper.saveCategoryAndBrand(cid,brand.getId()));

    }

    /**
     * 根据分类id查询 分类集合
     * @Param: [cid]
     * @Return:
     **/
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.selectByCid(cid);
    }

    /**
     * 根据品牌Id查询品牌
     * @Param: [id]
     * @Return:
     **/
    @Override
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
