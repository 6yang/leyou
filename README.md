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