package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));
        PrintWriter printWriter =null;
        try {
            //把静态页面生成到服务器本地
            File file = new File("F:\\nginx-1.14.0\\html\\item\\"+spuId+".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }
    /**
     * 新建线程来处理页面静态化
     * @Param: [spuId]
     * @Return:
    **/
    public void asyncExecute(Long spuId){
        ThreadUtil.execute(()->{
            createHtml(spuId);
        });
    }
    /**
     * 删除一个静态页面（未做删除功能）
     * @Param: [id]
     * @Return:
    **/
    public void deleteHtml(Long id) {
        File file = new File("F:\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
