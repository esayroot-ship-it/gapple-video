package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.episodeMapper;
import com.ap.apcommon.dao.videoMapper;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.videoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service("user-video")
public class videoIm implements videoService {
    @Autowired
    private videoMapper videoMapper;

    @Autowired
    private episodeMapper episodeMapper;

    @Autowired
    private Client meiliClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 1. 根据分类获取视频列表
     */
    @Override
    public R getVideosByCategory(Long categoryId) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.eq("status", 1); // 仅查询已上架视频
        List<Video> list = videoMapper.selectList(queryWrapper);
        return R.ok("查询成功", list);
    }

    /**
     * 2. 根据 ID 获取视频详情
     */
    @Override
    public R getVideoById(Long id) {
        Video video = videoMapper.selectById(id);
        if (video == null || video.getStatus() != 1) {
            return R.fail("视频不存在或已下架");
        }
        return R.ok("查询成功", video);
    }

    /**
     * 3. 根据剧集 ID 获取该剧集下的所有视频
     */
    @Override
    public R getVideoBySeries(String seriesId) {
        // 先从剧集-视频关联表 (episode) 中查找
        QueryWrapper<episode> episodeWrapper = new QueryWrapper<>();
        episodeWrapper.eq("series_id", seriesId);
        episodeWrapper.orderByAsc("episode_no"); // 按集数排序
        List<episode> episodes = episodeMapper.selectList(episodeWrapper);

        if (episodes == null || episodes.isEmpty()) {
            return R.fail("该剧集暂无视频内容");
        }

        // 提取视频 ID 列表
        List<Integer> videoIds = episodes.stream()
                .map(episode::getVid)
                .collect(Collectors.toList());

        // 批量查询视频详情并过滤上架状态
        QueryWrapper<Video> videoWrapper = new QueryWrapper<>();
        videoWrapper.in("id", videoIds);
        videoWrapper.eq("status", 1);
        List<Video> videos = videoMapper.selectList(videoWrapper);

        return R.ok("查询成功", videos);
    }

    /**
     * 4. 根据标题关键词搜索视频 (Meilisearch)
     */
    @Override
    public R getVideoByTitle(String title) {
        try {
            // 1. 从 Meilisearch 搜索
            SearchResult result = meiliClient.index("videos").search(title);
            ArrayList<HashMap<String, Object>> hits = result.getHits();

            // 2. 转换结果
            List<Video> list = hits.stream().map(hit -> {
                // 注意：由于 Meilisearch 存的是 DTO，这里需要小心映射或直接查库
                // 方案：根据 ID 查库保证状态最新，或者直接用缓存的展示数据
                Integer id = Integer.valueOf(hit.get("id").toString());
                return videoMapper.selectById(id);
            }).filter(v -> v != null && v.getStatus() == 1).collect(Collectors.toList());

            return R.ok("搜索成功", list);
        } catch (Exception e) {
            // 降级：如果搜索引擎故障，回退数据库模糊查询
            QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("title", title);
            queryWrapper.eq("status", 1);
            return R.ok("搜索成功(DB)", videoMapper.selectList(queryWrapper));
        }
    }
}
