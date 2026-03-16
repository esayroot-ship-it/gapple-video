package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.bannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户-轮播图管理", description = "用户端轮播图查看接口")
@RestController("user-banner")
@RequestMapping("/u/banner")
public class bannerCon {
    @Autowired
    private bannerService Service;

    @Operation(summary = "获取所有轮播图")
    @GetMapping("/all")
    public R<List<com.ap.apcommon.entity.banner>> getAllBanner(){
        return Service.getallBanner();
    }
}
