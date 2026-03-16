package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.impl.categoryIm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户-分类管理", description = "用户端分类查看接口")
@RestController("user-category")
@RequestMapping("/u/category")
public class categoryCon {
    @Autowired
    private categoryIm service;

    @Operation(summary = "获取所有分类")
    @GetMapping("/all")
    public R<List<com.ap.apcommon.entity.category>> getAllCategory(){
        return service.getallCategory();
    }
}
