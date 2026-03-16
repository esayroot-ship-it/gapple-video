package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.videoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户-视频管理", description = "用户端视频查看接口")
@RestController("user-videoCon")
@RequestMapping("/u/video")
public class videoCon {
    @Autowired
    private videoService videoService;

    /**
     * 根据分类筛选视频
     */
    @Operation(summary = "根据分类获取视频")
    @GetMapping("/getByCategory")
    public R getVideosByCategory(@RequestParam("categoryId") Long categoryId) {
        return videoService.getVideosByCategory(categoryId);
    }

    /**
     * 获取视频详情
     */
    @Operation(summary = "获取视频详情")
    @GetMapping("/getById")
    public R getVideoById(@RequestParam("id") Long id) {
        return videoService.getVideoById(id);
    }

    /**
     * 根据剧集获取关联视频
     */
    @Operation(summary = "根据剧集获取视频")
    @GetMapping("/getBySeries")
    public R getVideoBySeries(@RequestParam("seriesId") String seriesId) {
        return videoService.getVideoBySeries(seriesId);
    }

    /**
     * 搜索视频（根据标题）
     */
    @Operation(summary = "搜索视频")
    @GetMapping("/search")
    public R getVideoByTitle(@RequestParam("title") String title) {
        return videoService.getVideoByTitle(title);
    }
}
