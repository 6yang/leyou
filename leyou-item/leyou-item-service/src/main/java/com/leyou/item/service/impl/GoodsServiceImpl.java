package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    /**
     * 分页查询spu
     * @Param: [key, saleable, page, rows]
     * @Return:
     **/
    @Override
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //1.判断key是否为空 ，添加模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //2.添加是否上架的过滤
        if(saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //3.添加分页
        PageHelper.startPage(page,rows);
        //4.执行查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //5.把List<Spu> 转化为 List<SpuBo>
        List<SpuBo> spuBoList = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            // 把spu所有的属性copy给spuBo
            BeanUtils.copyProperties(spu, spuBo);
            //设置品牌名称和分类名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, " >> "));
            return spuBo;
        }).collect(Collectors.toList());
        //5.返回结果
        return new PageResult<>(pageInfo.getTotal(),spuBoList);
    }

    /**
     * 新增商品
     * @Param: [spuBo]
     * @Return:
     **/
    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {
        //1 新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //2 新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        saveSkuAndStock(spuBo);
    }

    /**
     * 新增sku 和stock
     * @Param: [spuBo]
     * @Return:
    **/
    private void saveSkuAndStock(SpuBo spuBo) {
        //3 新增sku
        spuBo.getSkus().forEach(sku->{
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            //4 新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuid查询spuDetail信息
     * @Param: [spuId]
     * @Return:
     **/
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {

        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 更新商品
     * @Param: [spuBo]
     * @Return:
     **/
    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {
        //0 根据spuId查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            //1 删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        //2  删除sku
        Sku delete_sku = new Sku();
        delete_sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(delete_sku);
        //3 新增sku and 新增stock
        saveSkuAndStock(spuBo);
        //4 更新spu and spuDetail
            //对不能更新的字段进行设置
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }

    /**
     * 更根据SpuId查询sku集合
     * @Param: [SpuId]
     * @Return:
     **/
    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku->{
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }
}
