package com.leyou.search.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.respository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //根据分类id获取分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 根据品牌id查询品牌名称
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        // 根据spuid查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化价格集合
        List<Long> prices = new ArrayList<>();
        // 搜集sku的必要字段信息
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            // 获取sku中的图片 ，数据库中的照片可能是多张，多张以"，"进行分割，所以切割后获取第一个
            map.put("image",StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            skuMapList.add(map);
        });
        // 根据spu中的spu3 获取所有所有的搜索规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        // 根据spuId 查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 把通用的规格参数，进行反序列化
        Map<String,Object>  genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<String,Object>>(){});
        // 把特殊的规格参数进行反序列化
        Map<String,List<Object>>  specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<String,List<Object>>>(){});
        Map<String, Object> specs = new HashMap<>();
        specParams.forEach(param->{
            // 判断规格参数的类型是否是通用的规格参数
            if(param.getGeneric()){
                // 如果是通用类型的参数，从genericSpecMap 获取参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                // 如果是数值类型返回一个区间
                if(param.getNumeric()){
                    value = chooseSegment(value,param);
                }
                specs.put(param.getName(),value);
            }else{
                // 如果是特殊的规格参数
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ") +" "+ brand.getName());
        // 获取所有spu 下的sku的价格
        goods.setPrice(prices);
        // 获取spu下的所有sku,并且转化成为json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        // 获取所有查询的规格参数{name : value}
        goods.setSpecs(specs);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }
        //1 自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2 添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        // 3、分页
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 4 添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //5 执行查询获取结果集
        Page<Goods> goodsPage = this.goodsRepository.search(queryBuilder.build());

        return new PageResult<>(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent());
    }
}
