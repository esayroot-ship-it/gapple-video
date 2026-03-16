package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.ratingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户-评分管理", description = "用户端评分相关接口")
@RestController
@RequestMapping("/u/rating")
public class ratingCon {

    @Autowired
    private ratingService ratingService;

    @Operation(summary = "提交评分")
    @PostMapping("/add")
    public R addRating(@RequestParam Long userId,
                       @RequestParam Long videoId,
                       @RequestParam Integer score) {
        return ratingService.addRating(userId, videoId, score);
    }

    @Operation(summary = "删除评分")
    @DeleteMapping("/delete")
    public R deleteRating(@RequestParam Long userId,
                          @RequestParam Long videoId) {
        return ratingService.deleteRating(userId, videoId);
    }

    @Operation(summary = "获取用户对视频的评分")
    @GetMapping("/user")
    public R getUserRating(@RequestParam Long userId,
                           @RequestParam Long videoId) {
        return ratingService.getUserRating(userId, videoId);
    }

    @Operation(summary = "获取视频所有评分")
    @GetMapping("/list")
    public R getRatingByVideoId(@RequestParam Long videoId) {
        return ratingService.getRatingByVideoId(videoId);
    }
}