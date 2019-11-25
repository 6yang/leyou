package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*
    * 根据父id，查询子类目
    * */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesById(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        if (pid == null || pid < 0) {
            //响应400
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); //400
            // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
        if (CollectionUtils.isEmpty(categories)) {
            //响应400
            return ResponseEntity.notFound().build();
        }
        //响应200
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据多个分类id查询分类名称
     * @Param: [ids]
     * @Return:
    **/
    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            //响应400
            return ResponseEntity.notFound().build();
        }
        //响应200
        return ResponseEntity.ok(names);
    }
}
