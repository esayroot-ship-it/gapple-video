package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.subsrcibeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户-订阅管理", description = "用户端订阅相关接口")
@RestController("user-subsrcibe")
@RequestMapping("/u/subsrcibe")
public class subsrcibeCon {

    @Autowired
    private subsrcibeService subsrcibeService;

    @Operation(summary = "订阅剧集")
    @PostMapping("/add")
    public R addSubscribe(@RequestParam("userId") Long userId, @RequestParam("seriesId") Long seriesId) {
        return subsrcibeService.addSubscribe(userId, seriesId);
    }

    @Operation(summary = "取消订阅")
    @PostMapping("/delete")
    public R deleteSubscribe(@RequestParam("userId") Long userId, @RequestParam("seriesId") Long seriesId) {
        return subsrcibeService.deleteSubscribe(userId, seriesId);
    }

    @Operation(summary = "获取订阅列表")
    @GetMapping("/list")
    public R getSubscribes(@RequestParam("userId") Long userId) {
        return subsrcibeService.getSubscribes(userId);
    }

    @Operation(summary = "判断是否订阅")
    @GetMapping("/isSubscribed")
    public R getIfSubscribed(@RequestParam("userId") Long userId, @RequestParam("seriesId") Long seriesId) {
        return subsrcibeService.getIfSubscribed(userId, seriesId);
    }
}
