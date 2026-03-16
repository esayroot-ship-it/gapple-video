package com.ap.apservice.controller;

import com.ap.apcommon.dao.seriesMapper;
import com.ap.apcommon.dao.videoMapper;
import com.ap.apcommon.dto.SeriesSearchDTO;
import com.ap.apcommon.dto.VideoSearchDTO;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.PinYinUtils;
import com.ap.apcommon.tools.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/u/search/sync")
public class SearchSyncController {

    @Autowired
    private videoMapper videoMapper;
    @Autowired
    private seriesMapper seriesMapper;
    @Autowired
    private Client meiliClient;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/full")
    public R fullSync() throws Exception {
        // 1. 同步视频
        List<Video> videos = videoMapper.selectList(new QueryWrapper<Video>().eq("status", 1));
        System.out.println("Found " + videos.size() + " videos to sync.");
        if (!videos.isEmpty()) {
            List<VideoSearchDTO> videoDTOs = videos.stream().map(v -> new VideoSearchDTO(
                    v.getId().toString(),
                    v.getTitle(),
                    v.getDescription(),
                    v.getCoverUrl(),
                    PinYinUtils.toPinyin(v.getTitle())
            )).collect(Collectors.toList());
            String json = objectMapper.writeValueAsString(videoDTOs);
            meiliClient.index("videos").addDocuments(json);
            System.out.println("Videos sync sent to Meilisearch.");
        }

        // 2. 同步剧集
        List<series> seriesList = seriesMapper.selectList(new QueryWrapper<series>().eq("status", 1));
        System.out.println("Found " + seriesList.size() + " series to sync.");
        if (!seriesList.isEmpty()) {
            List<SeriesSearchDTO> seriesDTOs = seriesList.stream().map(s -> new SeriesSearchDTO(
                    s.getId().toString(),
                    s.getTitle(),
                    s.getDescription(),
                    s.getCoverUrl(),
                    PinYinUtils.toPinyin(s.getTitle())
            )).collect(Collectors.toList());
            String json = objectMapper.writeValueAsString(seriesDTOs);
            meiliClient.index("series").addDocuments(json);
            System.out.println("Series sync sent to Meilisearch.");
        }

        return R.ok("全量同步指令已发送，请检查 Meilisearch 控制台");
    }
}
