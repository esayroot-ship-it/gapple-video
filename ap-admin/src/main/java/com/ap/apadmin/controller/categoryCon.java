package com.ap.apadmin.controller;

import com.ap.apadmin.service.categoryService;
import com.ap.apcommon.entity.category;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "分类管理", description = "分类管理相关接口")
@RestController
@RequestMapping("/category")
public class categoryCon {

    @Autowired
    private categoryService service;

    @Operation(summary = "创建分类")
    @PostMapping("/create")
    public R<Void> createCategory(@RequestBody category category) {
        return service.createCategory(category);
    }

    @Operation(summary = "删除分类")
    @PostMapping("/delete")
    public R<Void> deleteCategory(@RequestParam("id") Integer id) {
        return service.deleteCategory(id);
    }

    @Operation(summary = "更新分类")
    @PostMapping("/update")
    public R<Void> updateCategory(@RequestBody category category) {
        return service.updateCategory(category);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/get")
    public R<category> getCategory(@RequestParam("id") Integer id) {
        return service.getCategory(id);
    }

    @Operation(summary = "获取所有分类")
    @GetMapping("/all")
    public R<List<category>> getAllCategory() {
        return service.getAllCategory();
    }
}
