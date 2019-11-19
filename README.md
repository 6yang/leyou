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

