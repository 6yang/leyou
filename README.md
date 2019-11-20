## day08总结

### 新增品牌

**页面**
 	qs工具 把json对象转化为查询字符串，把字符串转化为查询json对象

 	校验规则：rules = ”nameRules“

​	nameRules[	

​		v=>!!v||"",

​		v=>v.length>1 || ""

​	]

​	执行校验 this.$refs.myBrandForm.validate() 或者 this.valid

​	自定义组件

​		级联

​		文件上传

**后台**

	1. 添加品牌
 	2. 添加品牌和分类的中间表

### 文件上传

**controller**

- 请求路径/upload/image
- 请求方式post
- 请求参数file
- 响应参数url	

**service**

1. 检验文件类型 file.getContentType();
2. 文件内容检验：ImageIO.read(file.getInputStream()); 
3. 保存文件到本地服务器
4. 组织url 返回

**测试中出现的问题**

​	**404** ：文件上传微服务没有走网关，但是访问时有走网关

​	在Nginx中重写路径

​	location /api/upload{

​		proxy_pass: http://127.0.0.1:8082

​	

​		rewrite "^/api/(.*)$" /$1 break;	

​	}

​	**option 400:** 预检请求没有通过

​		在文件上传的微服务中，配置corsfilter过滤器

​		响应数据

​		responseEntity.status(201).body(url);	

### 分布式文件系统 FastDFS

- client upload-service
- tracker 跟踪服务器
- storage 存储服务器

<u>怎么用？</u>

1. 引入java客户端依赖
2. 提供java配置类
3. 覆盖默认配置
4. FastFileStorageClient(uploadFile方法（inputStream,size,后缀，null）)

## day9

```java
// 把spu所有的属性copy给spuBo
BeanUtils.copyProperties(spu,spuBo);
```

转成一个集合

```java
Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())
```

把一个List集合转化为一个String字符串

```java
StringUtils.join(names,">")
```

### spu和 sku

spu:商品集（标准产品单元）

sku:具体的商品(库存量单位)

tb_spu tb_spu_detail

tb_sku tb_stock

查询具体service层实现

```java
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
```

