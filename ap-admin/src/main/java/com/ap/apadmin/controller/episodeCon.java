package com.ap.apadmin.controller;

import com.ap.apadmin.service.episodeService;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "剧集分集管理", description = "剧集分集管理相关接口")
@RestController
@RequestMapping("/episode")
public class episodeCon {

    @Autowired
    private episodeService episodeService;

    @Operation(summary = "获取剧集的所有分集")
    @GetMapping("/list")
    public R<List<episode>> getEpisodesBySeriesId(@RequestParam("seriesId") Long seriesId) {
        return episodeService.getEpisodesBySeriesId(seriesId);
    }

    @Operation(summary = "获取分集详情")
    @GetMapping("/get")
    public R<episode> getEpisodeById(@RequestParam("id") Integer id) {
        return episodeService.getEpisodeById(id);
    }

    @Operation(summary = "创建分集")
    @PostMapping("/create")
    public R<Void> createEpisode(@RequestBody episode episode) {
        return episodeService.createEpisode(episode);
    }

    @Operation(summary = "删除分集")
    @PostMapping("/delete")
    public R<Void> deleteEpisode(@RequestParam("id") Integer id) {
        return episodeService.deleteEpisode(id);
    }
}
