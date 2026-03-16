package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.favoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户-收藏管理", description = "用户端收藏相关接口")
@RestController
@RequestMapping("/u/favorite")
public class favoriteCon {

    @Autowired
    private favoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public R<Void> addFavorite(@RequestParam Long userId, @RequestParam Long videoId) {
        return favoriteService.addFavorite(userId, videoId);
    }
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/list")
    public R<List<com.ap.apcommon.entity.video.Video>> getFavoritesByUserId(@RequestParam Long userId) {
        return favoriteService.getFavoritesByUserId(userId);
    }

    @Operation(summary = "删除收藏")
    @PostMapping("/delete")
    public R<Void> deleteFavorite(@RequestParam Long favoriteId) {
        return favoriteService.deleteFavorite(favoriteId);
    }

}
