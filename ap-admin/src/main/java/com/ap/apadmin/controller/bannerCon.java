package com.ap.apadmin.controller;

import com.ap.apadmin.service.bannerService;
import com.ap.apcommon.entity.banner;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "轮播图管理", description = "轮播图管理相关接口")
@RestController
@RequestMapping("/banner")
public class bannerCon {

    @Autowired
    private bannerService service;

    @Operation(summary = "创建轮播图")
    @PostMapping("/create")
    public R<Void> createBanner(@RequestBody banner banner){
        return service.createBanner(banner);
    }

    @Operation(summary = "删除轮播图")
    @PostMapping("/delete")
    public R<Void> deleteBanner(@RequestParam("id") Integer id){
        return service.deleteBanner(id);
    }

    @Operation(summary = "更新轮播图")
    @PostMapping("/update")
    public R<Void> updateBanner(@RequestBody banner banner){
        return service.updateBanner(banner);
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/get")
    public R<banner> getBanner(@RequestParam("id") Integer id){
        return service.getBanner(id);
    }

    @Operation(summary = "获取所有轮播图")
    @GetMapping("/all")
    public R<List<banner>> getAllBanner(){
        return service.getallBanner();
    }

    @Operation(summary = "更新轮播图状态")
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@RequestParam("id") Integer id, @RequestParam("status") Integer status){
        return service.updateStatus(id, status);
    }

}
