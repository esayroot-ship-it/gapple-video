package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.episodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户-剧集分集管理", description = "用户端分集查看接口")
@RestController("user-episode")
@RequestMapping("/u/episode")
public class episodeCon {

    @Autowired
    private episodeService episodeService;

    @Operation(summary = "根据剧集获取分集列表")
    @GetMapping("/list")
    public R<List<com.ap.apcommon.entity.video.episode>> getEpisodesBySeriesId(Long seriesId){
        return episodeService.getEpisodesBySeriesId(seriesId);
    }

    @Operation(summary = "根据视频获取剧集")
    @GetMapping("/getSeries")
    public R<com.ap.apcommon.entity.video.series> getSeriesByVideoId(Long videoId){
        return episodeService.getSeriesByVideoId(videoId);
    }
}
