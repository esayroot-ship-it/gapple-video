package com.ap.apadmin.controller;

import com.ap.apadmin.service.seriesService;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "剧集管理", description = "剧集管理相关接口")
@RestController
@RequestMapping("/series")
public class seriesCon {

    @Autowired
    private seriesService seriesService;

    @Operation(summary = "获取所有剧集")
    @GetMapping("/all")
    public R<List<series>> getAllSeries() {
        return seriesService.getAllSeries();
    }

    @Operation(summary = "获取剧集详情")
    @GetMapping("/get")
    public R<series> getSeriesById(@RequestParam("id") Long id) {
        return seriesService.getSeriesById(id);
    }

    @Operation(summary = "创建剧集")
    @PostMapping("/create")
    public R<Void> createSeries(@RequestParam("title") String title,
                                @RequestParam("description") String description,
                                @RequestParam("status") Byte status,
                                @RequestParam("ecount") Byte ecount,
                                @RequestParam("category_id") Integer category_id,
                                @RequestParam("file") MultipartFile file) {
        series series = new series();
        series.setTitle(title);
        series.setDescription(description);
        series.setStatus(status);
        series.setEcount(ecount);
        series.setCid(category_id);
        series.setFile(file);
        return seriesService.createSeries(series);
    }

    @Operation(summary = "删除剧集")
    @PostMapping("/delete")
    public R<Void> deleteSeries(@RequestParam("id") Long id) {
        return seriesService.deleteSeries(id);
    }

    @Operation(summary = "更新剧集")
    @PostMapping("/update")
    public R<Void> updateSeries(@RequestBody series series) {
        return seriesService.updateSeries(series);
    }
}
