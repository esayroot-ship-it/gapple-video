package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.seriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户-剧集管理", description = "用户端剧集查看接口")
@RestController("user-seriesCon")
@RequestMapping("/u/series")
public class seriesCon {

    @Autowired
    private seriesService seriesService;

    @Operation(summary = "获取所有剧集")
    @GetMapping("/all")
    public R getAllSeries() {
        return seriesService.getAllSeries();
    }

    @Operation(summary = "搜索剧集")
    @GetMapping("/search")
    public R getSeriesByTitle(@RequestParam("title") String title) {
        return seriesService.getSeriesByTitle(title);
    }

    @Operation(summary = "根据分类获取剧集")
    @GetMapping("/getByCategory")
    public R getSeriesByCategory(@RequestParam("categoryId") Long categoryId) {
        return seriesService.getSeriesByCategory(categoryId);
    }

    @Operation(summary = "获取剧集详情")
    @GetMapping("/getById")
    public R getSeriesById(@RequestParam("id") Long id) {
        return seriesService.getSeriesById(id);
    }


}
